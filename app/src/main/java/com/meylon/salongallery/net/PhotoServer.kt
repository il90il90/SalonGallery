package com.meylon.salongallery.net

import fi.iki.elonen.NanoHTTPD

/** Commands the Display device reacts to when the server receives a request. */
interface ScreenCommands {
    fun onPhoto(bytes: ByteArray)          // append to the library
    fun onVideo(bytes: ByteArray)          // play this video
    fun onMusic(bytes: ByteArray)          // loop this as background music
    fun onBrightness(value: Float)
    fun onVolume(value: Float)
    fun onFrame(id: Int)
    fun onSlideshow(intervalMs: Long, shuffle: Boolean)
    fun onEffect(effect: String)
    fun onOrientation(o: String)
    fun onClear()
    // Library management
    fun listJson(): String                 // {"current":i,"items":[name,...]}
    fun thumbnail(name: String): ByteArray? // small JPEG of one photo
    fun deletePhoto(name: String)
    fun showNow(name: String)
    fun reorder(names: List<String>)
}

/**
 * HTTP server on the Display device. POST /photo (append), /video, /music take raw
 * bytes; GET commands (/brightness /volume /frame /slideshow /orientation /clear)
 * carry parameters. /ping returns device + screen + storage info.
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
                readBody(session)?.let { commands.onPhoto(it) }; ok()
            }
            session.method == Method.POST && uri == "/video" -> {
                readBody(session)?.let { commands.onVideo(it) }; ok()
            }
            session.method == Method.POST && uri == "/music" -> {
                readBody(session)?.let { commands.onMusic(it) }; ok()
            }

            session.method == Method.GET && uri == "/brightness" -> {
                floatParam(session)?.let { commands.onBrightness(it) }; ok()
            }
            session.method == Method.GET && uri == "/volume" -> {
                floatParam(session)?.let { commands.onVolume(it) }; ok()
            }
            session.method == Method.GET && uri == "/frame" -> {
                intParam(session, "id")?.let { commands.onFrame(it) }; ok()
            }
            session.method == Method.GET && uri == "/slideshow" -> {
                val interval = session.parameters["interval"]?.firstOrNull()?.toLongOrNull() ?: 8000L
                val shuffle = session.parameters["shuffle"]?.firstOrNull() == "1"
                commands.onSlideshow(interval, shuffle); ok()
            }
            session.method == Method.GET && uri == "/effect" -> {
                session.parameters["e"]?.firstOrNull()?.let { commands.onEffect(it) }; ok()
            }
            session.method == Method.GET && uri == "/orientation" -> {
                session.parameters["o"]?.firstOrNull()?.let { commands.onOrientation(it) }; ok()
            }
            session.method == Method.GET && uri == "/clear" -> { commands.onClear(); ok() }

            session.method == Method.GET && uri == "/list" -> json(commands.listJson())
            session.method == Method.GET && uri == "/thumb" -> {
                val name = session.parameters["id"]?.firstOrNull()
                val bytes = name?.let { commands.thumbnail(it) }
                if (bytes == null) newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "no thumb")
                else newFixedLengthResponse(
                    Response.Status.OK, "image/jpeg",
                    java.io.ByteArrayInputStream(bytes), bytes.size.toLong(),
                )
            }
            session.method == Method.GET && uri == "/delete" -> {
                session.parameters["id"]?.firstOrNull()?.let { commands.deletePhoto(it) }; ok()
            }
            session.method == Method.GET && uri == "/shownow" -> {
                session.parameters["id"]?.firstOrNull()?.let { commands.showNow(it) }; ok()
            }
            session.method == Method.GET && uri == "/reorder" -> {
                val names = session.parameters["names"]?.firstOrNull()?.split(",")?.filter { it.isNotBlank() }
                if (names != null) commands.reorder(names); ok()
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

    private fun intParam(session: IHTTPSession, key: String): Int? =
        session.parameters[key]?.firstOrNull()?.toIntOrNull()

    private fun ok() = json("""{"ok":true}""")
    private fun json(body: String) = newFixedLengthResponse(Response.Status.OK, "application/json", body)
}
