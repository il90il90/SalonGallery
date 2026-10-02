package com.il90.salongallery.net

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.StatFs
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.ByteArrayOutputStream
import java.io.File

/** Runs on the Display device: HTTP server, NSD advertisement, photo library, playback & controls. */
class ScreenSession(
    context: Context,
    val displayName: String,
    private val versionName: String,
) : ScreenCommands {

    private val app = context.applicationContext
    private val nsd = NsdController(app)
    private val audio = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var server: PhotoServer? = null

    val library = LibraryStore(File(app.filesDir, "library"))
    val albums = AlbumStore(File(app.filesDir, "albums.json"), library)
    val transforms = TransformStore(File(app.filesDir, "transforms.json"))
    val durations = DurationStore(File(app.filesDir, "durations.json"))
    val focus = FocusStore(File(app.filesDir, "focus.json"))
    val sources = SourceStore(File(app.filesDir, "sources.json"))
    val music = MusicStore(File(app.filesDir, "music"))
    val prefs = DisplayPrefs(app)
    val videoFile = File(app.filesDir, "display_current.mp4")
    private val thumbDir = File(app.filesDir, "thumbs").apply { runCatching { mkdirs() } }

    /** The device's shown name: a user-set custom name, else the model name. */
    fun effectiveName(): String = prefs.customName.ifBlank { displayName }

    /** Files for the active album (or the whole library), in order. */
    fun activeFiles(): List<File> = albums.activePhotoNames().mapNotNull { library.fileFor(it) }

    fun transformFor(name: String): PhotoTransform = transforms.get(name)

    fun durationFor(name: String): Int = durations.get(name)

    fun focusFor(name: String): PhotoFocus? = focus.get(name)

    val mode = MutableStateFlow(if (library.count() > 0) DisplayMode.SLIDESHOW else DisplayMode.WAITING)
    // Seeded with the boot time (not 0) so every process start presents a fresh version: a Remote
    // that was connected across a Display restart sees a change and re-lists.
    val libraryVersion = MutableStateFlow(System.currentTimeMillis())
    val videoVersion = MutableStateFlow(0L)
    val musicVersion = MutableStateFlow(0L)
    // Resume background music automatically when the frame boots with a playlist.
    val musicPlaying = MutableStateFlow(music.count() > 0)
    val musicShuffle = MutableStateFlow(false)
    val musicNextTrigger = MutableStateFlow(0L)
    val musicPrevTrigger = MutableStateFlow(0L)
    /** Bumped when the Remote asks the Display to open Android's screensaver settings. */
    val screensaverTrigger = MutableStateFlow(0L)
    /** Bumped when the Remote asks the Display to drop back to role selection. */
    val resetRoleTrigger = MutableStateFlow(0L)
    /** Index into the music library that is currently playing (updated by the player). */
    val musicIndex = MutableStateFlow(0)
    val frameId = MutableStateFlow(0)
    val frameRandom = MutableStateFlow(false)
    /** Frame ids to shuffle among when frameRandom is on. */
    val framePool = MutableStateFlow(listOf(1, 3, 4, 8))
    val frameWidth = MutableStateFlow(1f)
    val intervalMs = MutableStateFlow(30000L)
    val shuffle = MutableStateFlow(false)
    val effect = MutableStateFlow(SlideEffect.FADE)
    /** Transitions to shuffle among when effect == RANDOM. */
    val effectPool = MutableStateFlow(listOf("fade", "slide", "zoom", "dissolve"))
    /** Which looks the "Random" look shuffles between (per-photo). */
    val filterPool = MutableStateFlow(listOf("none", "mono", "sepia", "warm", "cool", "vignette"))
    val photoFit = MutableStateFlow(PhotoFit.FILL)
    val photoFilter = MutableStateFlow(PhotoFilter.NONE)
    /** Auto-fill the screen with a tasteful collage when a photo's orientation leaves big gaps. */
    val collage = MutableStateFlow(false)
    /** Slide composition: single photos, mosaics, scatters, or a random mix. */
    val layout = MutableStateFlow(LayoutMode.SINGLE)
    /** Spread photos appear one by one (a staggered entrance) instead of all at once. */
    val spreadStagger = MutableStateFlow(false)
    /** The slide on the wall right now, so the Remote can show exactly what the wall shows. */
    val nowSlide = MutableStateFlow<NowSlide?>(null)
    /** How often a spread appears instead of a single photo. */
    val spreadMix = MutableStateFlow(SpreadMix.ALWAYS)
    /** Subtle motion while a still waits on screen, and how fast it runs. */
    val motion = MutableStateFlow(MotionMode.OFF)
    val motionSpeed = MutableStateFlow(MotionSpeed.MEDIUM)
    val textOverlay = MutableStateFlow(TextOverlay())
    val clock = MutableStateFlow(ClockConfig())
    val rssOn = MutableStateFlow(prefs.rssEnabled)
    val rssFeeds = MutableStateFlow(prefs.rssFeeds)
    val rssConfig = MutableStateFlow(
        RssConfig(prefs.rssPos, prefs.rssShowImage, prefs.rssShowSource, prefs.rssShowSummary)
    )
    val orientation = MutableStateFlow(ScreenOrientation.AUTO)
    val brightness = MutableStateFlow(-1f)
    /** App-level playback volume 0..1, applied directly to the players (reliable on TV). */
    val volume = MutableStateFlow(1f)
    val running = MutableStateFlow(false)
    /** Index into the ordered library that the slideshow is currently showing. */
    val currentIndex = MutableStateFlow(0)

    private val screenW = app.resources.displayMetrics.widthPixels
    private val screenH = app.resources.displayMetrics.heightPixels

    var port: Int = 0
        private set

    fun start() {
        if (server != null) return
        // Prefer a fixed, well-known port so the Remote can connect by IP alone; fall back to a
        // random free port if it's taken (NSD auto-discovery still works either way).
        for (p in listOf(PhotoServer.FIXED_PORT, 0)) {
            val s = PhotoServer({ pingBody() }, this, p, { nowBody() })
            val ok = runCatching { s.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false) }.isSuccess
            if (ok) {
                server = s
                port = s.listeningPort
                Log.i("SalonScreen", "PhotoServer on port $port as '${effectiveName()}'")
                runCatching { nsd.register(effectiveName(), port) }
                running.value = true
                sweepOrientation()
                return
            } else runCatching { s.stop() }
        }
    }

    fun stop() {
        runCatching { nsd.unregister() }
        runCatching { server?.stop() }
        server = null
        running.value = false
    }

    /** Renames the device: persists it and re-advertises on the network. */
    fun renameDevice(name: String) {
        prefs.customName = name
        runCatching {
            nsd.unregister()
            if (port > 0) nsd.register(effectiveName(), port)
        }
    }

    private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")

    /** JSON describing the current slide: its layout, seed and the photos it is made of. */
    private fun nowBody(): String {
        val n = nowSlide.value
        val names = n?.members.orEmpty()
        val members = names.joinToString(",") { "\"${esc(it)}\"" }
        // Authoritative rotation for each member, so the Remote mirrors the wall exactly (no stale map).
        val rots = names.joinToString(",") { transforms.get(it).rotNorm.toString() }
        val style = n?.style ?: "single"
        val seed = n?.seed ?: 0
        return """{"style":"$style","seed":$seed,"w":$screenW,"h":$screenH,"members":[$members],"rots":[$rots]}"""
    }

    private fun pingBody(): String {
        val stat = runCatching { StatFs(app.filesDir.path) }.getOrNull()
        val free = stat?.availableBytes ?: 0L
        val total = stat?.totalBytes ?: 0L
        val b = if (brightness.value < 0f) 1f else brightness.value
        return """{"name":"${esc(effectiveName())}","version":"${esc(versionName)}",""" +
            """"w":$screenW,"h":$screenH,"free":$free,"total":$total,"count":${library.count()},""" +
            // "lib" changes on every library mutation (add/delete/clear/rotate/reorder) so the Remote can
            // refresh what it shows from a single poll instead of guessing after each of its own actions.
            """"brightness":$b,"volume":${volume.value},"interval":${intervalMs.value},"lib":${libraryVersion.value}}"""
    }

    // ---- ScreenCommands (called on server threads) ----

    /** The album an upload should land in: an explicit target, else the active album (none for "All"). */
    private fun targetAlbum(album: String?): String? =
        album?.takeIf { it.isNotBlank() && it != "all" } ?: albums.activeId.takeIf { !albums.isAllActive() }

    override fun onPhoto(bytes: ByteArray, album: String?) {
        runCatching {
            val f = library.add(bytes)
            targetAlbum(album)?.let { albums.addToAlbum(it, f.name) }
            mode.value = DisplayMode.SLIDESHOW
            libraryVersion.value = System.currentTimeMillis()
            showNewest(f.name)
            autoOrient(f)
        }
    }

    /** Jump the wall to a freshly added photo so each one the user sends appears right away (only if
     *  it's in what's showing now — the active album, or everything). */
    private fun showNewest(name: String) {
        val idx = activeFiles().indexOfFirst { it.name == name }
        if (idx >= 0) currentIndex.value = idx
    }

    // Bumped on every Clear so background downloads that were already in flight don't
    // silently re-populate the wall after the user wipes it.
    private val clearGen = java.util.concurrent.atomic.AtomicInteger(0)

    override fun onPhotoUrl(url: String) {
        val gen = clearGen.get()
        Thread {
            runCatching {
                val u = java.net.URL(url)
                val referer = "${u.protocol}://${u.host}/"
                val conn = (u.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 15000; readTimeout = 30000; instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36")
                    setRequestProperty("Referer", referer)
                }
                val bytes = conn.inputStream.use { it.readBytes() }
                conn.disconnect()
                if (bytes.isNotEmpty() && clearGen.get() == gen) {
                    val f = library.add(bytes)
                    sources.set(f.name, url)
                    if (!albums.isAllActive()) albums.addToAlbum(albums.activeId, f.name)
                    mode.value = DisplayMode.SLIDESHOW
                    libraryVersion.value = System.currentTimeMillis()
                    showNewest(f.name)
                    autoOrient(f)
                }
            }
        }.start()
    }

    override fun removeByUrl(url: String) {
        runCatching {
            sources.namesForUrl(url).forEach { deletePhoto(it) }
        }
    }

    override fun sourcesJson(): String {
        val present = sources.presentUrls(library.names().toSet())
        val arr = present.joinToString(",") { "\"${esc(it)}\"" }
        return """{"urls":[$arr]}"""
    }

    override fun onVideo(bytes: ByteArray, album: String?) {
        runCatching {
            // Videos now live in the unified library and play inline in the slideshow,
            // so photos and clips can be mixed on the wall.
            val f = library.addVideo(bytes)
            targetAlbum(album)?.let { albums.addToAlbum(it, f.name) }
            mode.value = DisplayMode.SLIDESHOW
            videoVersion.value = System.currentTimeMillis()
            libraryVersion.value = System.currentTimeMillis()
            showNewest(f.name)
        }
    }

    override fun onMusic(bytes: ByteArray, title: String) {
        runCatching {
            val wasEmpty = music.count() == 0
            music.add(bytes, title)
            if (wasEmpty) { musicIndex.value = 0; musicPlaying.value = true }
            musicVersion.value = System.currentTimeMillis()
        }
    }

    override fun musicListJson(): String {
        val inner = music.listJson().removePrefix("{").removeSuffix("}")
        return "{\"playing\":${musicPlaying.value},\"shuffle\":${musicShuffle.value}," +
            "\"current\":${musicIndex.value},$inner}"
    }

    override fun musicDelete(name: String) {
        runCatching {
            music.delete(name)
            if (music.count() == 0) musicPlaying.value = false
            musicIndex.value = musicIndex.value.coerceIn(0, maxOf(0, music.count() - 1))
            musicVersion.value = System.currentTimeMillis()
        }
    }

    override fun musicControl(action: String) {
        when (action.lowercase()) {
            "play" -> if (music.count() > 0) musicPlaying.value = true
            "pause" -> musicPlaying.value = false
            "toggle" -> if (music.count() > 0) musicPlaying.value = !musicPlaying.value
            "next" -> musicNextTrigger.value = System.currentTimeMillis()
            "prev" -> musicPrevTrigger.value = System.currentTimeMillis()
            "shuffle" -> musicShuffle.value = !musicShuffle.value
        }
    }

    override fun musicDownload(url: String, title: String) {
        Thread {
            runCatching {
                val conn = (java.net.URL(url).openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 15000; readTimeout = 30000; instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Android) SalonGallery")
                }
                val bytes = conn.inputStream.use { it.readBytes() }
                conn.disconnect()
                if (bytes.isNotEmpty()) onMusic(bytes, title)
            }
        }.start()
    }

    override fun onBrightness(value: Float) { brightness.value = value.coerceIn(0f, 1f) }

    override fun onVolume(value: Float) {
        val v = value.coerceIn(0f, 1f)
        volume.value = v  // applied to the ExoPlayers by the display (works on TV)
        runCatching {
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (v * max).toInt(), 0)
        }
    }

    override fun onFrame(id: Int) { frameRandom.value = false; frameId.value = id }

    override fun onFrameRandom(on: Boolean, pool: List<Int>) {
        frameRandom.value = on
        if (pool.isNotEmpty()) framePool.value = pool
    }

    override fun onFrameWidth(value: Float) { frameWidth.value = value.coerceIn(0.4f, 2.2f) }

    override fun onSlideshow(intervalMs: Long, shuffle: Boolean) {
        this.intervalMs.value = intervalMs.coerceIn(2000L, 86_400_000L)
        this.shuffle.value = shuffle
    }

    override fun onDefaultDuration(seconds: Int) {
        intervalMs.value = (seconds * 1000L).coerceIn(2000L, 86_400_000L)
    }

    override fun onEffect(effect: String) {
        this.effect.value = SlideEffect.from(effect)
    }

    override fun onFilterPool(names: List<String>) {
        if (names.isNotEmpty()) filterPool.value = names
    }

    override fun onEffectPool(names: List<String>) {
        if (names.isNotEmpty()) effectPool.value = names
    }

    override fun onFit(fit: String) {
        this.photoFit.value = PhotoFit.from(fit)
    }

    override fun onFilter(filter: String) {
        this.photoFilter.value = PhotoFilter.from(filter)
    }

    override fun onCollage(on: Boolean) { collage.value = on }

    override fun onLayout(mode: String) { layout.value = LayoutMode.from(mode) }

    override fun onStagger(on: Boolean) { spreadStagger.value = on }

    override fun onSpreadMix(level: String) { spreadMix.value = SpreadMix.from(level) }

    override fun onMotion(mode: String, speed: String) {
        if (mode.isNotBlank()) motion.value = MotionMode.from(mode)
        if (speed.isNotBlank()) motionSpeed.value = MotionSpeed.from(speed)
    }

    override fun onText(content: String, pos: String, size: String, color: String) {
        textOverlay.value = TextOverlay(content, TextPos.from(pos), size, color)
    }


    override fun onClock(on: Boolean, pos: String, showDate: Boolean, style: String, size: String) {
        clock.value = ClockConfig(on, ClockPos.from(pos), showDate, ClockStyle.from(style), size)
    }

    override fun onOpenScreensaver() { screensaverTrigger.value = System.currentTimeMillis() }

    override fun onRename(name: String) { renameDevice(name) }

    override fun settingsJson(): String =
        """{"name":"${esc(prefs.customName)}","hasPin":${prefs.hasPin},""" +
            """"schedOn":${prefs.scheduleEnabled},"sleepStart":${prefs.sleepStartMin},"sleepEnd":${prefs.sleepEndMin}}"""

    override fun onSchedule(on: Boolean, start: Int, end: Int) {
        prefs.scheduleEnabled = on
        prefs.sleepStartMin = start.coerceIn(0, 1439)
        prefs.sleepEndMin = end.coerceIn(0, 1439)
    }

    override fun onSetScreenPin(code: String) { prefs.pin = code.filter { it.isDigit() }.take(6) }

    override fun screenPinCheckJson(code: String): String =
        """{"ok":${!prefs.hasPin || code == prefs.pin}}"""

    override fun onResetRole() { resetRoleTrigger.value = System.currentTimeMillis() }

    override fun onRss(on: Boolean, feeds: List<String>, pos: String, showImage: Boolean, showSource: Boolean, showSummary: Boolean) {
        val clean = feeds.map { it.trim() }.filter { it.isNotBlank() }
        prefs.rssEnabled = on
        prefs.rssFeeds = clean
        prefs.rssPos = pos
        prefs.rssShowImage = showImage
        prefs.rssShowSource = showSource
        prefs.rssShowSummary = showSummary
        rssOn.value = on
        rssFeeds.value = clean
        rssConfig.value = RssConfig(pos, showImage, showSource, showSummary)
    }

    override fun rssJson(): String {
        val f = rssFeeds.value.joinToString(",") { "\"${esc(it)}\"" }
        val c = rssConfig.value
        return """{"on":${rssOn.value},"feeds":[$f],"pos":"${esc(c.pos)}",""" +
            """"image":${c.showImage},"source":${c.showSource},"summary":${c.showSummary}}"""
    }

    override fun onOrientation(o: String) {
        orientation.value = when (o.lowercase()) {
            "portrait" -> ScreenOrientation.PORTRAIT
            "landscape" -> ScreenOrientation.LANDSCAPE
            else -> ScreenOrientation.AUTO
        }
    }

    override fun onClear() {
        runCatching {
            clearGen.incrementAndGet()
            library.clear()
            albums.onPhotosCleared()
            sources.clear()
            runCatching { thumbDir.listFiles()?.forEach { it.delete() } }
            if (mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
            currentIndex.value = 0
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    private val dimCache = java.util.concurrent.ConcurrentHashMap<String, String>()
    private fun dimsOf(name: String): String {
        if (isVideoName(name)) return ""
        return dimCache.getOrPut(name) {
            val f = library.fileFor(name) ?: return@getOrPut ""
            runCatching {
                val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(f.path, o)
                if (o.outWidth > 0 && o.outHeight > 0) "${o.outWidth}×${o.outHeight}" else ""
            }.getOrDefault("")
        }
    }

    override fun listJson(): String {
        val names = albums.activePhotoNames()
        val cur = if (names.isEmpty()) 0 else currentIndex.value.coerceIn(0, names.size - 1)
        val items = names.joinToString(",") { "\"${esc(it)}\"" }
        val pinned = library.pinnedNames().joinToString(",") { "\"${esc(it)}\"" }
        val durs = names.joinToString(",") { durations.get(it).toString() }
        val bytes = names.joinToString(",") { (library.fileFor(it)?.length() ?: 0L).toString() }
        val dims = names.joinToString(",") { "\"${dimsOf(it)}\"" }
        // Per-item display rotation so the Remote's previews turn the same way the wall does.
        val rots = names.joinToString(",") { transforms.get(it).rotNorm.toString() }
        return """{"current":$cur,"mode":"${mode.value.name}","album":"${esc(albums.activeName())}",""" +
            """"albumId":"${esc(albums.activeId)}","pinned":[$pinned],"durs":[$durs],"bytes":[$bytes],"dims":[$dims],"rots":[$rots],"items":[$items]}"""
    }

    override fun thumbnail(name: String): ByteArray? {
        val f = library.fileFor(name) ?: return null
        // Serve a cached thumbnail if we've generated one (photos are immutable once added),
        // so a grid of thousands doesn't re-decode full images on every scroll.
        val cached = File(thumbDir, "$name.jpg")
        if (cached.exists()) runCatching { cached.readBytes() }.getOrNull()?.let { return it }
        if (isVideoName(name)) return videoThumbnail(f, cached)
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(f.path, bounds)
            var sample = 1
            val target = 400
            while (bounds.outWidth / sample > target || bounds.outHeight / sample > target) sample *= 2
            val bmp = BitmapFactory.decodeFile(f.path, BitmapFactory.Options().apply { inSampleSize = sample })
                ?: return null
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos)
            bmp.recycle()
            val bytes = bos.toByteArray()
            runCatching { cached.writeBytes(bytes) }
            bytes
        }.getOrNull()
    }

    /** Grab a representative frame from a video clip and cache it as the thumbnail. */
    private fun videoThumbnail(f: File, cached: File): ByteArray? = runCatching {
        val mmr = android.media.MediaMetadataRetriever()
        try {
            mmr.setDataSource(f.path)
            val durMs = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val vw = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val vh = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            // Aspect-correct target so getScaledFrameAtTime doesn't distort.
            val (tw, th) = if (vw > 0 && vh > 0) {
                val s = 400f / maxOf(vw, vh)
                maxOf(1, (vw * s).toInt()) to maxOf(1, (vh * s).toInt())
            } else 400 to 400
            // Sample ~1s in (or 10% for short clips) to skip black/green intro frames. Hardware
            // decoders often render the very first frame green, so never start at 0.
            val sample = if (durMs > 0) minOf(1_200_000L, durMs * 1000L / 10).coerceAtLeast(300_000L) else 1_000_000L
            val times = listOf(sample, 2_000_000L, 500_000L, 0L)
            var frame: Bitmap? = null
            for (t in times) {
                frame = if (android.os.Build.VERSION.SDK_INT >= 27)
                    runCatching { mmr.getScaledFrameAtTime(t, android.media.MediaMetadataRetriever.OPTION_CLOSEST, tw, th) }.getOrNull()
                else runCatching { mmr.getFrameAtTime(t, android.media.MediaMetadataRetriever.OPTION_CLOSEST) }.getOrNull()
                if (frame != null) break
            }
            frame = frame ?: mmr.getFrameAtTime() ?: return null
            val scale = 400f / maxOf(frame.width, frame.height).coerceAtLeast(1)
            val bmp = if (scale < 1f)
                Bitmap.createScaledBitmap(frame, (frame.width * scale).toInt().coerceAtLeast(1), (frame.height * scale).toInt().coerceAtLeast(1), true)
            else frame
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 82, bos)
            if (bmp !== frame) bmp.recycle()
            frame.recycle()
            val bytes = bos.toByteArray()
            runCatching { cached.writeBytes(bytes) }
            bytes
        } finally { runCatching { mmr.release() } }
    }.getOrNull()

    override fun deletePhoto(name: String) {
        runCatching {
            library.delete(name)
            albums.onPhotoDeleted(name)
            transforms.remove(name)
            durations.remove(name)
            focus.remove(name)
            sources.remove(name)
            runCatching { File(thumbDir, "$name.jpg").delete() }
            val names = albums.activePhotoNames()
            if (names.isEmpty() && mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
            currentIndex.value = if (names.isEmpty()) 0 else currentIndex.value.coerceIn(0, names.size - 1)
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    override fun showNow(name: String) {
        runCatching {
            val idx = albums.activePhotoNames().indexOf(name)
            if (idx >= 0) {
                if (mode.value != DisplayMode.SLIDESHOW) mode.value = DisplayMode.SLIDESHOW
                currentIndex.value = idx
            }
        }
    }

    override fun reorder(names: List<String>) {
        runCatching {
            if (albums.isAllActive()) library.reorder(names) else albums.reorderAlbum(albums.activeId, names)
            currentIndex.value = currentIndex.value.coerceIn(0, maxOf(0, albums.activePhotoNames().size - 1))
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    // ---- Albums ----

    override fun albumsJson(): String = albums.albumsJson()

    override fun createAlbum(name: String): String {
        val id = albums.createAlbum(name)
        return id
    }

    override fun renameAlbum(id: String, name: String) { albums.renameAlbum(id, name) }

    override fun deleteAlbum(id: String) {
        // Deleting an album also deletes the photos that were in it (not move them to All).
        val members = albums.photosIn(id)
        albums.deleteAlbum(id)
        members.forEach { name ->
            runCatching {
                library.delete(name)
                albums.onPhotoDeleted(name)
                transforms.remove(name); durations.remove(name); focus.remove(name); sources.remove(name)
                File(thumbDir, "$name.jpg").delete()
            }
        }
        val names = albums.activePhotoNames()
        if (names.isEmpty() && mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
        currentIndex.value = if (names.isEmpty()) 0 else currentIndex.value.coerceIn(0, maxOf(0, names.size - 1))
        libraryVersion.value = System.currentTimeMillis()
    }

    override fun setActiveAlbum(id: String) {
        albums.setActive(id)
        currentIndex.value = 0
        if (activeFiles().isNotEmpty()) mode.value = DisplayMode.SLIDESHOW
        else if (mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
        libraryVersion.value = System.currentTimeMillis()
    }

    override fun addToAlbum(id: String, photo: String) {
        albums.addToAlbum(id, photo)
        libraryVersion.value = System.currentTimeMillis()
    }

    override fun removeFromAlbum(id: String, photo: String) {
        albums.removeFromAlbum(id, photo)
        currentIndex.value = currentIndex.value.coerceIn(0, maxOf(0, albums.activePhotoNames().size - 1))
        libraryVersion.value = System.currentTimeMillis()
    }

    // ---- Studio (per-photo crop) ----

    override fun fullPhoto(name: String): ByteArray? =
        library.fileFor(name)?.let { runCatching { it.readBytes() }.getOrNull() }

    override fun onTransform(photo: String, scale: Float, x: Float, y: Float) {
        transforms.set(photo, PhotoTransform(scale.coerceIn(1f, 5f), x.coerceIn(-0.5f, 0.5f), y.coerceIn(-0.5f, 0.5f)))
        libraryVersion.value = System.currentTimeMillis()
    }

    override fun transformJson(photo: String): String {
        val t = transforms.get(photo)
        return """{"s":${t.scale},"x":${t.offX},"y":${t.offY},"r":${t.rotNorm}}"""
    }

    override fun onRotate(photo: String, by: Int) {
        val t = transforms.get(photo)
        transforms.set(photo, t.copy(rot = t.rotNorm + by))
        libraryVersion.value = System.currentTimeMillis()
    }

    /**
     * Face-aware default orientation (and crop focus): if the people in a photo only stand upright after a turn
     * (a quarter turn or upside down), store that turn as the photo's rotation. Runs on one
     * low-priority background thread so uploads stay snappy; the user's manual rotate always wins.
     * Each photo is checked once (remembered in orient_checked_v4.txt), so photos that were already in
     * the library before this existed get straightened too, by [sweepOrientation] at start.
     */
    private val orientExec = java.util.concurrent.Executors.newSingleThreadExecutor { r ->
        Thread(r, "salon-orient").apply { isDaemon = true; priority = Thread.MIN_PRIORITY }
    }
    private val orientFile = File(app.filesDir, "orient_checked_v4.txt")
    private val orientChecked: MutableSet<String> = java.util.Collections.synchronizedSet(
        runCatching { orientFile.readLines().filter { it.isNotBlank() }.toMutableSet() }.getOrDefault(mutableSetOf())
    )
    // The rotation WE auto-applied to each photo, so a re-check with a better detector can correct its
    // own earlier mistake — but never a rotation the user set by hand in the app.
    private val autoRotFile = File(app.filesDir, "auto_oriented.json")
    private val autoRot: MutableMap<String, Int> = java.util.Collections.synchronizedMap(
        runCatching {
            val o = org.json.JSONObject(autoRotFile.readText())
            HashMap<String, Int>().apply { o.keys().forEach { put(it, o.getInt(it)) } }
        }.getOrDefault(HashMap())
    )
    private fun saveAutoRot() {
        runCatching { autoRotFile.writeText(org.json.JSONObject(synchronized(autoRot) { HashMap(autoRot) } as Map<*, *>).toString()) }
    }

    private fun autoOrient(f: File) {
        if (isVideoName(f.name)) return
        runCatching { orientExec.execute { orientOne(f) } }
    }

    /** Queues every not-yet-checked photo in the library for the face-orientation check. */
    private fun sweepOrientation() {
        runCatching {
            orientExec.execute {
                val photos = library.list().filter { !isVideoName(it.name) }
                // Forget deleted photos so the checked list doesn't grow forever.
                val names = photos.map { it.name }.toSet()
                if (orientChecked.retainAll(names)) saveOrientChecked()
                if (autoRot.keys.retainAll(names)) saveAutoRot()
                photos.filter { it.name !in orientChecked }.forEach { orientOne(it) }
            }
        }
    }

    private fun orientOne(f: File) {
        if (f.name in orientChecked || !f.exists()) return
        val res = FaceOrient.analyze(f)
        res.focus?.let { focus.set(f.name, it) }
        val t = transforms.get(f.name)
        val cur = t.rotNorm
        // The user owns the rotation only if it isn't the one we last applied automatically. That lets
        // this better detector fix a photo an earlier version turned the wrong way, without ever
        // undoing a hand rotation.
        val userSet = cur != 0 && cur != autoRot[f.name]
        if (!userSet && res.rot != cur) {
            transforms.set(f.name, t.copy(rot = res.rot))
        }
        if (!userSet) { autoRot[f.name] = res.rot; saveAutoRot() }
        libraryVersion.value = System.currentTimeMillis()
        orientChecked.add(f.name)
        runCatching { orientFile.appendText(f.name + "\n") }
    }

    private fun saveOrientChecked() {
        runCatching { orientFile.writeText(synchronized(orientChecked) { orientChecked.joinToString("\n", postfix = "\n") }) }
    }

    override fun onDuration(photo: String, seconds: Int) {
        durations.set(photo, seconds)
        libraryVersion.value = System.currentTimeMillis()
    }

    override fun durationJson(photo: String): String = """{"sec":${durations.get(photo)}}"""

    override fun onPin(photo: String, pinned: Boolean) {
        library.setPinned(photo, pinned)
        currentIndex.value = 0
        libraryVersion.value = System.currentTimeMillis()
    }
}

/**
 * Process-wide holder for the single Display session, so it survives the activity
 * being backgrounded/recreated and keeps serving while a foreground service is up.
 */
object ScreenSessionHolder {
    @Volatile
    var session: ScreenSession? = null
        private set

    @Synchronized
    fun getOrCreate(context: Context, name: String, version: String): ScreenSession =
        session ?: ScreenSession(context.applicationContext, name, version).also {
            it.start(); session = it
        }

    @Synchronized
    fun stopAll() {
        session?.stop()
        session = null
    }
}

/** Runs on the Remote device: discovers Display devices on the network. */
class RemoteSession(context: Context) {
    private val nsd = NsdController(context.applicationContext)
    val screens = MutableStateFlow<List<DiscoveredScreen>>(emptyList())

    fun start() {
        nsd.startDiscovery(
            onFound = { d ->
                screens.update { list ->
                    if (list.any { it.host == d.host && it.port == d.port }) list else list + d
                }
            },
            onLost = { key -> screens.update { it.filterNot { s -> s.key == key } } },
        )
    }

    fun stop() {
        nsd.stopDiscovery()
        screens.value = emptyList()
    }
}
