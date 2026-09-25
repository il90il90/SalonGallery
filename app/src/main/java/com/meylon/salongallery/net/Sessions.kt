package com.meylon.salongallery.net

import android.content.Context
import android.media.AudioManager
import android.os.StatFs
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

enum class MediaKind { NONE, PHOTO, VIDEO }
data class DisplayMedia(val kind: MediaKind, val version: Long)

/** Runs on the Display device: HTTP server + NSD advertisement + current media & controls. */
class ScreenSession(
    context: Context,
    val displayName: String,
    private val versionName: String,
) : ScreenCommands {

    private val app = context.applicationContext
    private val nsd = NsdController(app)
    private val audio = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var server: PhotoServer? = null

    private val screenW = app.resources.displayMetrics.widthPixels
    private val screenH = app.resources.displayMetrics.heightPixels

    private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")

    private fun pingBody(): String {
        val stat = runCatching { StatFs(app.filesDir.path) }.getOrNull()
        val free = stat?.availableBytes ?: 0L
        val total = stat?.totalBytes ?: 0L
        return """{"name":"${esc(displayName)}","version":"${esc(versionName)}",""" +
            """"w":$screenW,"h":$screenH,"free":$free,"total":$total}"""
    }

    val photoFile = File(app.filesDir, "display_current.jpg")
    val videoFile = File(app.filesDir, "display_current.mp4")

    val media = MutableStateFlow(
        when {
            photoFile.exists() -> DisplayMedia(MediaKind.PHOTO, photoFile.lastModified())
            else -> DisplayMedia(MediaKind.NONE, 0L)
        }
    )
    /** -1f means "leave the system brightness"; 0..1 means an explicit level. */
    val brightness = MutableStateFlow(-1f)
    val running = MutableStateFlow(false)
    var port: Int = 0
        private set

    fun start() {
        if (server != null) return
        val s = PhotoServer({ pingBody() }, this)
        runCatching {
            s.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            server = s
            port = s.listeningPort
            Log.i("SalonScreen", "PhotoServer listening on port $port as '$displayName'")
            nsd.register(displayName, port)
            running.value = true
        }
    }

    fun stop() {
        runCatching { nsd.unregister() }
        runCatching { server?.stop() }
        server = null
        running.value = false
    }

    // ---- ScreenCommands (called on server threads) ----

    override fun onMedia(bytes: ByteArray, isVideo: Boolean) {
        runCatching {
            val target = if (isVideo) videoFile else photoFile
            target.writeBytes(bytes)
            media.value = DisplayMedia(if (isVideo) MediaKind.VIDEO else MediaKind.PHOTO, System.currentTimeMillis())
        }
    }

    override fun onBrightness(value: Float) {
        brightness.value = value.coerceIn(0f, 1f)
    }

    override fun onVolume(value: Float) {
        runCatching {
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (value.coerceIn(0f, 1f) * max).toInt(), 0)
        }
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
