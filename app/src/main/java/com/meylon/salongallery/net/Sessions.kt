package com.meylon.salongallery.net

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
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
    private var musicPlayer: MediaPlayer? = null

    val library = LibraryStore(File(app.filesDir, "library"))
    val videoFile = File(app.filesDir, "display_current.mp4")
    val musicFile = File(app.filesDir, "display_music.m4a")

    val mode = MutableStateFlow(if (library.count() > 0) DisplayMode.SLIDESHOW else DisplayMode.WAITING)
    val libraryVersion = MutableStateFlow(0L)
    val videoVersion = MutableStateFlow(0L)
    val musicVersion = MutableStateFlow(0L)
    val frameId = MutableStateFlow(0)
    val intervalMs = MutableStateFlow(8000L)
    val shuffle = MutableStateFlow(false)
    val effect = MutableStateFlow(SlideEffect.FADE)
    val orientation = MutableStateFlow(ScreenOrientation.AUTO)
    val brightness = MutableStateFlow(-1f)
    val running = MutableStateFlow(false)
    /** Index into the ordered library that the slideshow is currently showing. */
    val currentIndex = MutableStateFlow(0)

    private val screenW = app.resources.displayMetrics.widthPixels
    private val screenH = app.resources.displayMetrics.heightPixels

    var port: Int = 0
        private set

    fun start() {
        if (server != null) return
        val s = PhotoServer({ pingBody() }, this)
        runCatching {
            s.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            server = s
            port = s.listeningPort
            Log.i("SalonScreen", "PhotoServer on port $port as '$displayName'")
            nsd.register(displayName, port)
            running.value = true
        }
    }

    fun stop() {
        runCatching { nsd.unregister() }
        runCatching { server?.stop() }
        runCatching { musicPlayer?.release() }
        musicPlayer = null
        server = null
        running.value = false
    }

    private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")

    private fun pingBody(): String {
        val stat = runCatching { StatFs(app.filesDir.path) }.getOrNull()
        val free = stat?.availableBytes ?: 0L
        val total = stat?.totalBytes ?: 0L
        return """{"name":"${esc(displayName)}","version":"${esc(versionName)}",""" +
            """"w":$screenW,"h":$screenH,"free":$free,"total":$total,"count":${library.count()}}"""
    }

    // ---- ScreenCommands (called on server threads) ----

    override fun onPhoto(bytes: ByteArray) {
        runCatching {
            library.add(bytes)
            mode.value = DisplayMode.SLIDESHOW
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    override fun onVideo(bytes: ByteArray) {
        runCatching {
            videoFile.writeBytes(bytes)
            mode.value = DisplayMode.VIDEO
            videoVersion.value = System.currentTimeMillis()
        }
    }

    override fun onMusic(bytes: ByteArray) {
        runCatching {
            musicFile.writeBytes(bytes)
            musicPlayer?.release()
            musicPlayer = MediaPlayer().apply {
                setDataSource(musicFile.path)
                isLooping = true
                setOnPreparedListener { start() }
                prepareAsync()
            }
            musicVersion.value = System.currentTimeMillis()
        }
    }

    override fun onBrightness(value: Float) { brightness.value = value.coerceIn(0f, 1f) }

    override fun onVolume(value: Float) {
        runCatching {
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (value.coerceIn(0f, 1f) * max).toInt(), 0)
        }
    }

    override fun onFrame(id: Int) { frameId.value = id }

    override fun onSlideshow(intervalMs: Long, shuffle: Boolean) {
        this.intervalMs.value = intervalMs.coerceIn(2000L, 120000L)
        this.shuffle.value = shuffle
    }

    override fun onEffect(effect: String) {
        this.effect.value = SlideEffect.from(effect)
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
            library.clear()
            if (mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
            currentIndex.value = 0
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    override fun listJson(): String {
        val names = library.names()
        val cur = if (names.isEmpty()) 0 else currentIndex.value.coerceIn(0, names.size - 1)
        val items = names.joinToString(",") { "\"${esc(it)}\"" }
        return """{"current":$cur,"mode":"${mode.value.name}","items":[$items]}"""
    }

    override fun thumbnail(name: String): ByteArray? {
        val f = library.fileFor(name) ?: return null
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
            bos.toByteArray()
        }.getOrNull()
    }

    override fun deletePhoto(name: String) {
        runCatching {
            library.delete(name)
            val names = library.names()
            if (names.isEmpty() && mode.value == DisplayMode.SLIDESHOW) mode.value = DisplayMode.WAITING
            currentIndex.value = if (names.isEmpty()) 0 else currentIndex.value.coerceIn(0, names.size - 1)
            libraryVersion.value = System.currentTimeMillis()
        }
    }

    override fun showNow(name: String) {
        runCatching {
            val idx = library.names().indexOf(name)
            if (idx >= 0) {
                if (mode.value != DisplayMode.SLIDESHOW) mode.value = DisplayMode.SLIDESHOW
                currentIndex.value = idx
            }
        }
    }

    override fun reorder(names: List<String>) {
        runCatching {
            library.reorder(names)
            currentIndex.value = currentIndex.value.coerceIn(0, maxOf(0, library.count() - 1))
            libraryVersion.value = System.currentTimeMillis()
        }
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
