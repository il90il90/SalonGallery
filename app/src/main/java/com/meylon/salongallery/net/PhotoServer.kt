package com.meylon.salongallery.net

import fi.iki.elonen.NanoHTTPD

/** Commands the Display device reacts to when the server receives a request. */
interface ScreenCommands {
    fun onMedia(bytes: ByteArray, isVideo: Boolean)
    fun onBrightness(value: Float)
    fun onVolume(value: Float)
}

/**
 * Tiny HTTP server that runs on the Display device. Answers /ping, accepts a raw
 * image on POST /photo or a video on POST /video, and simple GET control commands
 * (/brightness, /volume). Construct with port 0 to let the OS pick a free port.
 */
class PhotoServer(
    private val pingBody: () -> String,
    private val commands: ScreenCommands,
) : NanoHTTPD(0) {

    override fun serve(session: IHTTPSession): Response = try {
        val uri = session.uri
        when {
            session.method == Method.GET && uri == "/ping" -> json(pingBody())

            session.method == Method.POST && uri == "/photo" -> {
                readBody(session)?.let { commands.onMedia(it, isVideo = false) }
                json("""{"ok":true}""")
            }

            session.method == Method.POST && uri == "/video" -> {
                readBody(session)?.let { commands.onMedia(it, isVideo = true) }
                json("""{"ok":true}""")
            }

            session.method == Method.GET && uri == "/brightness" -> {
                floatParam(session)?.let { commands.onBrightness(it) }
                json("""{"ok":true}""")
            }

            session.method == Method.GET && uri == "/volume" -> {
                floatParam(session)?.let { commands.onVolume(it) }
                json("""{"ok":true}""")
            }

            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "not found")
        }
    } catch (e: Exception) {
        newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.message ?: "error")
    }

    private fun readBody(session: IHTTPSession): ByteArray? {
        val len = session.headers["content-length"]?.toIntOrNull() ?: return null
        if (len <= 0) return null
        val buf = ByteArray(len)
        var read = 0
        while (read < len) {
            val r = session.inputStream.read(buf, read, len - read)
            if (r <= 0) break
            read += r
        }
        return if (read == len) buf else buf.copyOf(read)
    }

    private fun floatParam(session: IHTTPSession): Float? =
        session.parameters["v"]?.firstOrNull()?.toFloatOrNull()?.coerceIn(0f, 1f)

    private fun json(body: String) = newFixedLengthResponse(Response.Status.OK, "application/json", body)

    private fun jsonStr(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
