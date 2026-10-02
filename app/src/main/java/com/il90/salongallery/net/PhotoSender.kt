package com.il90.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Screen details reported by a Display device on /ping. */
data class ScreenInfo(
    val name: String,
    val widthPx: Int,
    val heightPx: Int,
    val freeBytes: Long,
    val totalBytes: Long,
    val photoCount: Int,
    val brightness: Float = 1f,
    val volume: Float = 1f,
    /** Bumped by the Display on any library change; the Remote refreshes its lists when it moves. */
    val libVersion: Long = 0L,
    /** The Display app's versionName — shown in the screen list so an old install is obvious. */
    val version: String = "",
)

/** The Display's current (active-album) library, as seen by the Remote. */
data class LibraryList(
    val current: Int,
    val mode: String,
    val albumId: String,
    val albumName: String,
    val items: List<String>,
    val pinned: Set<String> = emptySet(),
    val durations: Map<String, Int> = emptyMap(),
    val bytes: Map<String, Long> = emptyMap(),   // file size per item
    val dims: Map<String, String> = emptyMap(),  // "W×H" per item (photos only)
    val rots: Map<String, Int> = emptyMap(),     // display rotation (0/90/180/270) per item
)

data class AlbumInfo(val id: String, val name: String, val count: Int)
data class AlbumList(val activeId: String, val activeName: String, val albums: List<AlbumInfo>)
data class RemoteTransform(val scale: Float, val x: Float, val y: Float, val rot: Int = 0)

data class RssState(
    val on: Boolean, val feeds: List<String>, val pos: String,
    val showImage: Boolean, val showSource: Boolean, val showSummary: Boolean,
)

/** The Display's admin settings, fetched by the Remote so it can edit them remotely. */
data class ScreenSettings(
    val name: String, val hasPin: Boolean,
    val schedOn: Boolean, val sleepStart: Int, val sleepEnd: Int,
)

data class MusicTrack(val name: String, val title: String)
data class MusicState(val playing: Boolean, val shuffle: Boolean, val current: Int, val tracks: List<MusicTrack>)

/** A royalty-free track the Display can download directly over the network. */
data class FreeTrack(val title: String, val artist: String, val url: String)

/** HTTP client used by the Remote to talk to a Display device. */
object PhotoSender {

    private const val TIMEOUT = 8_000

    fun thumbUrl(host: String, port: Int, name: String) = "http://$host:$port/thumb?id=$name"
    fun fullUrl(host: String, port: Int, name: String) = "http://$host:$port/full?id=$name"

    suspend fun getTransform(host: String, port: Int, photo: String): RemoteTransform? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/transform/get?photo=$photo", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            RemoteTransform(o.optDouble("s", 1.0).toFloat(), o.optDouble("x", 0.0).toFloat(), o.optDouble("y", 0.0).toFloat(), o.optInt("r", 0))
        } catch (e: Exception) { null }
    }

    suspend fun setTransform(host: String, port: Int, photo: String, scale: Float, x: Float, y: Float) =
        get(host, port, "/transform?photo=$photo&scale=$scale&x=$x&y=$y")
    /** Turn a photo a quarter turn clockwise (or by any ±90 multiple). */
    suspend fun rotatePhoto(host: String, port: Int, photo: String, by: Int = 90) =
        get(host, port, "/rotate?photo=$photo&by=$by")

    suspend fun getList(host: String, port: Int): LibraryList? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/list", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            val arr = o.optJSONArray("items")
            val items = buildList { if (arr != null) for (i in 0 until arr.length()) add(arr.optString(i)) }
            val parr = o.optJSONArray("pinned")
            val pins = buildSet { if (parr != null) for (i in 0 until parr.length()) add(parr.optString(i)) }
            val darr = o.optJSONArray("durs")
            val durs = buildMap {
                if (darr != null) for (i in 0 until minOf(darr.length(), items.size)) put(items[i], darr.optInt(i))
            }
            val barr = o.optJSONArray("bytes")
            val bmap = buildMap {
                if (barr != null) for (i in 0 until minOf(barr.length(), items.size)) put(items[i], barr.optLong(i))
            }
            val diarr = o.optJSONArray("dims")
            val dmap = buildMap {
                if (diarr != null) for (i in 0 until minOf(diarr.length(), items.size)) {
                    val d = diarr.optString(i); if (d.isNotBlank()) put(items[i], d)
                }
            }
            val rarr = o.optJSONArray("rots")
            val rmap = buildMap {
                if (rarr != null) for (i in 0 until minOf(rarr.length(), items.size)) {
                    val r = rarr.optInt(i); if (r != 0) put(items[i], r)
                }
            }
            LibraryList(
                o.optInt("current", 0), o.optString("mode", ""),
                o.optString("albumId", "all"), o.optString("album", "All"), items, pins, durs, bmap, dmap, rmap,
            )
        } catch (e: Exception) { null }
    }

    suspend fun getAlbums(host: String, port: Int): AlbumList? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/albums", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            val arr = o.optJSONArray("albums")
            val list = buildList {
                if (arr != null) for (i in 0 until arr.length()) {
                    val a = arr.getJSONObject(i)
                    add(AlbumInfo(a.optString("id"), a.optString("name"), a.optInt("count")))
                }
            }
            AlbumList(o.optString("active", "all"), o.optString("activeName", "All"), list)
        } catch (e: Exception) { null }
    }

    private fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
    suspend fun createAlbum(host: String, port: Int, name: String): String? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/album/create?name=${enc(name)}", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            JSONObject(body).optString("id").ifEmpty { null }
        } catch (e: Exception) { null }
    }
    suspend fun renameAlbum(host: String, port: Int, id: String, name: String) = get(host, port, "/album/rename?id=$id&name=${enc(name)}")
    suspend fun deleteAlbum(host: String, port: Int, id: String) = get(host, port, "/album/delete?id=$id")
    suspend fun setActiveAlbum(host: String, port: Int, id: String) = get(host, port, "/album/active?id=$id")
    suspend fun addToAlbum(host: String, port: Int, id: String, photo: String) = get(host, port, "/album/add?id=$id&photo=$photo")
    suspend fun removeFromAlbum(host: String, port: Int, id: String, photo: String) = get(host, port, "/album/remove?id=$id&photo=$photo")

    suspend fun deletePhoto(host: String, port: Int, name: String) = get(host, port, "/delete?id=$name")
    /** Delete several photos in one request (multi-select). */
    suspend fun deletePhotos(host: String, port: Int, names: Collection<String>) =
        if (names.isEmpty()) true else get(host, port, "/delete?id=${names.joinToString(",")}")
    suspend fun showNow(host: String, port: Int, name: String) = get(host, port, "/shownow?id=$name")
    suspend fun setDuration(host: String, port: Int, name: String, seconds: Int) = get(host, port, "/duration?photo=$name&sec=$seconds")
    suspend fun setPinned(host: String, port: Int, name: String, pinned: Boolean) = get(host, port, "/pin?photo=$name&on=${if (pinned) 1 else 0}")
    suspend fun getDuration(host: String, port: Int, name: String): Int = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/duration/get?photo=$name", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext 0 }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            JSONObject(body).optInt("sec", 0)
        } catch (e: Exception) { 0 }
    }
    suspend fun reorder(host: String, port: Int, names: List<String>) =
        get(host, port, "/reorder?names=${names.joinToString(",")}")

    /**
     * Fetches resolution + storage from the screen, or null if unreachable. [timeoutMs] lets the
     * connection watchdog probe with a short LAN-appropriate timeout instead of the 8s transfer
     * default — otherwise each missed ping blocks 8s and an outage takes ~20s to show.
     */
    suspend fun getInfo(host: String, port: Int, timeoutMs: Int = TIMEOUT): ScreenInfo? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/ping", "GET", timeoutMs)
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            ScreenInfo(
                name = o.optString("name", "Screen"),
                widthPx = o.optInt("w", 0),
                heightPx = o.optInt("h", 0),
                freeBytes = o.optLong("free", 0L),
                totalBytes = o.optLong("total", 0L),
                photoCount = o.optInt("count", 0),
                brightness = o.optDouble("brightness", 1.0).toFloat(),
                volume = o.optDouble("volume", 1.0).toFloat(),
                libVersion = o.optLong("lib", 0L),
                version = o.optString("version", ""),
            )
        } catch (e: Exception) {
            null
        }
    }

    /** Returns the screen's name if reachable, else null. */
    suspend fun ping(host: String, port: Int): String? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/ping", "GET")
            if (conn.responseCode in 200..299) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                Regex("\"name\"\\s*:\\s*\"([^\"]*)\"").find(body)?.groupValues?.get(1) ?: "Screen"
            } else {
                conn.disconnect(); null
            }
        } catch (e: Exception) {
            null
        }
    }

    /** POSTs a photo. Returns null on success, or a short error string. */
    suspend fun sendPhoto(host: String, port: Int, bytes: ByteArray, album: String? = null) =
        sendMedia(host, port, "/photo" + albumQuery(album), bytes, "image/jpeg")

    private fun albumQuery(album: String?) = if (album.isNullOrBlank()) "" else "?album=$album"

    /** Tells the Display to download an image (e.g. a gallery artwork) into its library. */
    suspend fun downloadPhoto(host: String, port: Int, url: String) =
        get(host, port, "/photo/download?url=${enc(url)}")

    /** Removes the library items that came from this source url (art un-select). */
    suspend fun removeArt(host: String, port: Int, url: String) =
        get(host, port, "/photo/removeurl?url=${enc(url)}")

    /** Set of source urls currently on the wall, so the gallery can show checkmarks. */
    suspend fun getArtSources(host: String, port: Int): Set<String> = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/sources", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext emptySet() }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val arr = JSONObject(body).optJSONArray("urls")
            buildSet { if (arr != null) for (i in 0 until arr.length()) add(arr.optString(i)) }
        } catch (e: Exception) { emptySet() }
    }

    /** POSTs a video. Returns null on success, or a short error string. */
    suspend fun sendVideo(host: String, port: Int, bytes: ByteArray, album: String? = null) =
        sendMedia(host, port, "/video" + albumQuery(album), bytes, "video/mp4")

    /** POSTs a track to the music library. Returns null on success, or a short error string. */
    suspend fun sendMusic(host: String, port: Int, bytes: ByteArray, title: String) =
        sendMedia(host, port, "/music?name=${enc(title)}", bytes, "audio/mpeg")

    suspend fun getMusic(host: String, port: Int): MusicState? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/music/list", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            val arr = o.optJSONArray("items")
            val tracks = buildList {
                if (arr != null) for (i in 0 until arr.length()) {
                    val t = arr.getJSONObject(i)
                    add(MusicTrack(t.optString("name"), t.optString("title")))
                }
            }
            MusicState(o.optBoolean("playing"), o.optBoolean("shuffle"), o.optInt("current", 0), tracks)
        } catch (e: Exception) { null }
    }

    suspend fun musicControl(host: String, port: Int, action: String) = get(host, port, "/music/control?a=$action")
    suspend fun musicDelete(host: String, port: Int, name: String) = get(host, port, "/music/delete?id=$name")
    suspend fun musicDownload(host: String, port: Int, url: String, title: String) =
        get(host, port, "/music/download?url=${enc(url)}&name=${enc(title)}")

    /** Curated royalty-free music (Kevin MacLeod, CC-BY) with stable direct URLs, plus a few CC0 tracks. */
    val freeMusic: List<FreeTrack> = run {
        val km = "https://incompetech.com/music/royalty-free/mp3-royaltyfree/"
        fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8").replace("+", "%20")
        fun kmTrack(title: String) = FreeTrack(title, "Kevin MacLeod · CC-BY", "$km${enc(title)}.mp3")
        listOf(
            // Calm / ambient — good for a gallery
            kmTrack("Canon in D Major"),
            kmTrack("Gymnopedie No 1"),
            kmTrack("Dreamy Flashback"),
            kmTrack("Easy Lemon"),
            kmTrack("Local Forecast - Elevator"),
            kmTrack("Wallpaper"),
            kmTrack("Clenched Teeth"),
            kmTrack("Deliberate Thought"),
            kmTrack("Enchanted Valley"),
            kmTrack("Peaceful Desolation"),
            kmTrack("Thinking Music"),
            kmTrack("Wholesome"),
            kmTrack("Carefree"),
            kmTrack("Sardana"),
            kmTrack("The Builder"),
            // Playful
            kmTrack("Monkeys Spinning Monkeys"),
            kmTrack("Fluffing a Duck"),
            kmTrack("Sneaky Snitch"),
            kmTrack("Scheming Weasel faster"),
            // Public-domain classical (archive.org)
            FreeTrack("Moonlight Sonata", "Beethoven · Public Domain", "https://archive.org/download/MoonlightSonata_755/Beethoven-MoonlightSonata.mp3"),
            FreeTrack("Prelude in C Major", "Bach · Public Domain", "https://archive.org/download/CMajorPreludeBachClassicalCalmSad/C_Major_Prelude-Bach%20Classical%20CalmSad.mp3"),
        )
    }

    suspend fun setFrame(host: String, port: Int, id: Int) =
        get(host, port, "/frame?id=$id")

    suspend fun setFrameWidth(host: String, port: Int, value: Float) =
        get(host, port, "/framewidth?v=$value")

    suspend fun setFrameRandom(host: String, port: Int, on: Boolean, pool: List<Int>) =
        get(host, port, "/framerandom?on=${if (on) 1 else 0}&pool=${pool.joinToString(",")}")

    suspend fun setSlideshow(host: String, port: Int, intervalMs: Long, shuffle: Boolean) =
        get(host, port, "/slideshow?interval=$intervalMs&shuffle=${if (shuffle) 1 else 0}")

    suspend fun setEffect(host: String, port: Int, effect: String) =
        get(host, port, "/effect?e=$effect")

    suspend fun setEffectPool(host: String, port: Int, names: List<String>) =
        get(host, port, "/effectpool?names=${names.joinToString(",")}")

    suspend fun setFilterPool(host: String, port: Int, names: List<String>) =
        get(host, port, "/filterpool?names=${names.joinToString(",")}")

    suspend fun setFit(host: String, port: Int, fit: String) =
        get(host, port, "/fit?f=$fit")

    suspend fun setFilter(host: String, port: Int, filter: String) =
        get(host, port, "/filter?f=$filter")

    suspend fun setCollage(host: String, port: Int, on: Boolean) =
        get(host, port, "/collage?on=${if (on) 1 else 0}")
    /** Slide composition: single | mosaic | scatter | random. */
    suspend fun setLayout(host: String, port: Int, mode: String) =
        get(host, port, "/layout?mode=$mode")
    /** How often a spread replaces a single photo: always | often | sometimes | rarely. */
    suspend fun setSpreadMix(host: String, port: Int, level: String) =
        get(host, port, "/spreadmix?level=$level")
    /** Whether a spread's photos appear one after another instead of all at once. */
    suspend fun setStagger(host: String, port: Int, on: Boolean) =
        get(host, port, "/stagger?on=${if (on) 1 else 0}")
    /** Gentle motion while a photo waits: off | zoom | drift | breathe | mix, at slow | medium | fast. */
    suspend fun setMotion(host: String, port: Int, mode: String, speed: String) =
        get(host, port, "/motion?mode=$mode&speed=$speed")

    suspend fun setText(host: String, port: Int, content: String, pos: String, size: String, color: String) =
        get(host, port, "/text?content=${enc(content)}&pos=$pos&size=$size&color=$color")

    suspend fun setClock(host: String, port: Int, on: Boolean, pos: String = "top_start", showDate: Boolean = true, style: String = "digital", size: String = "m") =
        get(host, port, "/clock?on=${if (on) 1 else 0}&pos=$pos&date=${if (showDate) 1 else 0}&style=$style&size=$size")

    suspend fun setOrientation(host: String, port: Int, o: String) =
        get(host, port, "/orientation?o=$o")

    suspend fun setRss(
        host: String, port: Int, on: Boolean, feeds: List<String>,
        pos: String = "bottom", showImage: Boolean = false, showSource: Boolean = true, showSummary: Boolean = false,
    ) = get(
        host, port,
        "/rss?on=${if (on) 1 else 0}&feeds=${enc(feeds.joinToString("\n"))}" +
            "&pos=$pos&image=${if (showImage) 1 else 0}&source=${if (showSource) 1 else 0}&summary=${if (showSummary) 1 else 0}",
    )

    /** Returns (enabled, feeds, pos, showImage, showSource, showSummary) or null. */
    suspend fun getRss(host: String, port: Int): RssState? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/rss/get", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            val arr = o.optJSONArray("feeds")
            val feeds = buildList { if (arr != null) for (i in 0 until arr.length()) add(arr.optString(i)) }
            RssState(
                o.optBoolean("on"), feeds, o.optString("pos", "bottom"),
                o.optBoolean("image", false), o.optBoolean("source", true), o.optBoolean("summary", false),
            )
        } catch (e: Exception) { null }
    }


    suspend fun clearLibrary(host: String, port: Int) =
        get(host, port, "/clear")

    /** Ask the Display to open Android's screensaver settings on its own screen. */
    suspend fun openScreensaver(host: String, port: Int) =
        get(host, port, "/screensaver")

    // ---- Screen admin, controlled from the Remote ----

    suspend fun getSettings(host: String, port: Int): ScreenSettings? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/settings/get", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            ScreenSettings(
                name = o.optString("name", ""),
                hasPin = o.optBoolean("hasPin", false),
                schedOn = o.optBoolean("schedOn", false),
                sleepStart = o.optInt("sleepStart", 1380),
                sleepEnd = o.optInt("sleepEnd", 420),
            )
        } catch (e: Exception) { null }
    }

    suspend fun setName(host: String, port: Int, name: String) =
        get(host, port, "/rename?name=${enc(name)}")

    suspend fun setSchedule(host: String, port: Int, on: Boolean, start: Int, end: Int) =
        get(host, port, "/schedule?on=${if (on) 1 else 0}&start=$start&end=$end")

    suspend fun setScreenPin(host: String, port: Int, code: String) =
        get(host, port, "/screenpin?code=${enc(code)}")

    /** Verify a screen PIN. Returns true when the code is right (or no PIN is set). */
    suspend fun checkScreenPin(host: String, port: Int, code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/screenpin/check?code=${enc(code)}", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext false }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            JSONObject(body).optBoolean("ok", false)
        } catch (e: Exception) { false }
    }

    /** Ask the Display to drop back to role selection (become a Remote, or re-pair). */
    suspend fun resetRole(host: String, port: Int) =
        get(host, port, "/resetrole")

    private suspend fun get(host: String, port: Int, path: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val conn = open("http://$host:$port$path", "GET")
                val ok = conn.responseCode in 200..299
                conn.disconnect()
                ok
            } catch (e: Exception) { false }
        }

    private suspend fun sendMedia(
        host: String, port: Int, path: String, bytes: ByteArray, contentType: String,
    ): String? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port$path", "POST").apply {
                doOutput = true
                readTimeout = 30_000
                setRequestProperty("Content-Type", contentType)
                setFixedLengthStreamingMode(bytes.size)
            }
            conn.outputStream.use { it.write(bytes) }
            val code = conn.responseCode
            conn.disconnect()
            if (code in 200..299) null else "HTTP $code"
        } catch (e: Exception) {
            e.message ?: e.javaClass.simpleName
        }
    }

    suspend fun setBrightness(host: String, port: Int, value: Float) =
        sendCommand(host, port, "/brightness", value)

    suspend fun setVolume(host: String, port: Int, value: Float) =
        sendCommand(host, port, "/volume", value)

    private suspend fun sendCommand(host: String, port: Int, path: String, value: Float): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val conn = open("http://$host:$port$path?v=$value", "GET")
                val ok = conn.responseCode in 200..299
                conn.disconnect()
                ok
            } catch (e: Exception) {
                false
            }
        }

    private fun open(url: String, method: String, timeoutMs: Int = TIMEOUT): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = timeoutMs
            readTimeout = timeoutMs
        }
}
