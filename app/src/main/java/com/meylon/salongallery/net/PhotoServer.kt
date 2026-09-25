package com.meylon.salongallery.net

import fi.iki.elonen.NanoHTTPD

/**
 * Tiny HTTP server that runs on the Display device. It answers /ping (so a
 * Remote can confirm the connection) and accepts a raw image on POST /photo.
 * Construct with port 0 to let the OS pick a free port, then read [listeningPort].
 */
class PhotoServer(
    private val deviceName: String,
    private val versionName: String,
    private val onPhoto: (ByteArray) -> Unit,
) : NanoHTTPD(0) {

    override fun serve(session: IHTTPSession): Response = try {
        when {
            session.method == Method.GET && session.uri == "/ping" ->
                newFixedLengthResponse(
                    Response.Status.OK, "application/json",
                    """{"name":${jsonStr(deviceName)},"version":${jsonStr(versionName)}}""",
                )

            session.method == Method.POST && session.uri == "/photo" -> {
                val len = session.headers["content-length"]?.toIntOrNull() ?: 0
                if (len <= 0) {
                    newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "empty")
                } else {
                    val buf = ByteArray(len)
                    var read = 0
                    while (read < len) {
                        val r = session.inputStream.read(buf, read, len - read)
                        if (r <= 0) break
                        read += r
                    }
                    if (read > 0) onPhoto(if (read == len) buf else buf.copyOf(read))
                    newFixedLengthResponse(Response.Status.OK, "application/json", """{"ok":true}""")
                }
            }

            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "not found")
        }
    } catch (e: Exception) {
        newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.message ?: "error")
    }

    private fun jsonStr(s: String) =
        "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
