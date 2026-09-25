package com.meylon.salongallery.net

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

/** Runs on the Display device: HTTP server + NSD advertisement + latest photo. */
class ScreenSession(
    context: Context,
    val displayName: String,
    private val versionName: String,
) {
    private val app = context.applicationContext
    private val nsd = NsdController(app)
    private var server: PhotoServer? = null

    val photoFile = File(app.filesDir, "display_current.jpg")
    /** Bumps whenever a new photo arrives, so the UI reloads. */
    val photoVersion = MutableStateFlow(if (photoFile.exists()) photoFile.lastModified() else 0L)
    val running = MutableStateFlow(false)
    var port: Int = 0
        private set

    fun start() {
        if (server != null) return
        val s = PhotoServer(displayName, versionName) { bytes ->
            runCatching {
                photoFile.writeBytes(bytes)
                photoVersion.value = System.currentTimeMillis()
            }
        }
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
