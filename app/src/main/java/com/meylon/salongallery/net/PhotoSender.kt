package com.meylon.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** HTTP client used by the Remote to talk to a Display device. */
object PhotoSender {

    private const val TIMEOUT = 8_000

    /** Returns the screen's name if reachable, else null. */
    suspend fun ping(host: String, port: Int): String? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL("http://$host:$port/ping").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
            }
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

    /** POSTs raw image bytes to the Display. Returns true on 2xx. */
    suspend fun sendPhoto(host: String, port: Int, bytes: ByteArray): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val conn = (URL("http://$host:$port/photo").openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = TIMEOUT
                    readTimeout = 20_000
                    setRequestProperty("Content-Type", "image/jpeg")
                    setFixedLengthStreamingMode(bytes.size)
                }
                conn.outputStream.use { it.write(bytes) }
                val ok = conn.responseCode in 200..299
                conn.disconnect()
                ok
            } catch (e: Exception) {
                false
            }
        }
}
