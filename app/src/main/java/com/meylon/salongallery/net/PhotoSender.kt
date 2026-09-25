package com.meylon.salongallery.net

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
)

/** The Display's current library, as seen by the Remote. */
data class LibraryList(val current: Int, val mode: String, val items: List<String>)

/** HTTP client used by the Remote to talk to a Display device. */
object PhotoSender {

    private const val TIMEOUT = 8_000

    fun thumbUrl(host: String, port: Int, name: String) = "http://$host:$port/thumb?id=$name"

    suspend fun getList(host: String, port: Int): LibraryList? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/list", "GET")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            val arr = o.optJSONArray("items")
            val items = buildList { if (arr != null) for (i in 0 until arr.length()) add(arr.optString(i)) }
            LibraryList(o.optInt("current", 0), o.optString("mode", ""), items)
        } catch (e: Exception) { null }
    }

    suspend fun deletePhoto(host: String, port: Int, name: String) = get(host, port, "/delete?id=$name")
    suspend fun showNow(host: String, port: Int, name: String) = get(host, port, "/shownow?id=$name")
    suspend fun reorder(host: String, port: Int, names: List<String>) =
        get(host, port, "/reorder?names=${names.joinToString(",")}")

    /** Fetches resolution + storage from the screen, or null if unreachable. */
    suspend fun getInfo(host: String, port: Int): ScreenInfo? = withContext(Dispatchers.IO) {
        try {
            val conn = open("http://$host:$port/ping", "GET")
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
    suspend fun sendPhoto(host: String, port: Int, bytes: ByteArray) =
        sendMedia(host, port, "/photo", bytes, "image/jpeg")

    /** POSTs a video. Returns null on success, or a short error string. */
    suspend fun sendVideo(host: String, port: Int, bytes: ByteArray) =
        sendMedia(host, port, "/video", bytes, "video/mp4")

    /** POSTs background music. Returns null on success, or a short error string. */
    suspend fun sendMusic(host: String, port: Int, bytes: ByteArray) =
        sendMedia(host, port, "/music", bytes, "audio/mp4")

    suspend fun setFrame(host: String, port: Int, id: Int) =
        get(host, port, "/frame?id=$id")

    suspend fun setSlideshow(host: String, port: Int, intervalMs: Long, shuffle: Boolean) =
        get(host, port, "/slideshow?interval=$intervalMs&shuffle=${if (shuffle) 1 else 0}")

    suspend fun setEffect(host: String, port: Int, effect: String) =
        get(host, port, "/effect?e=$effect")

    suspend fun setFit(host: String, port: Int, fit: String) =
        get(host, port, "/fit?f=$fit")

    suspend fun setOrientation(host: String, port: Int, o: String) =
        get(host, port, "/orientation?o=$o")

    suspend fun clearLibrary(host: String, port: Int) =
        get(host, port, "/clear")

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

    private fun open(url: String, method: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = TIMEOUT
            readTimeout = TIMEOUT
        }
}
