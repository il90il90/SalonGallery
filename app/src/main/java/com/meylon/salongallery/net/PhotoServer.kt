package com.meylon.salongallery.net

import fi.iki.elonen.NanoHTTPD

/** Commands the Display device reacts to when the server receives a request. */
interface ScreenCommands {
    fun onPhoto(bytes: ByteArray)          // append to the library
    fun onPhotoUrl(url: String)            // download an image and append to the library
    fun removeByUrl(url: String)           // remove library items that came from this url
    fun sourcesJson(): String              // {"urls":[...]} of source urls currently on the wall
    fun onVideo(bytes: ByteArray)          // play this video
    fun onMusic(bytes: ByteArray, title: String) // add to the music library
    // Music library
    fun musicListJson(): String
    fun musicDelete(name: String)
    fun musicControl(action: String)       // play / pause / next / prev / shuffle
    fun musicDownload(url: String, title: String)
    fun onBrightness(value: Float)
    fun onVolume(value: Float)
    fun onFrame(id: Int)
    fun onFrameRandom(on: Boolean, pool: List<Int>)
    fun onFrameWidth(value: Float)
    fun onSlideshow(intervalMs: Long, shuffle: Boolean)
    fun onEffect(effect: String)
    fun onEffectPool(names: List<String>)
    fun onFit(fit: String)
    fun onFilter(filter: String)
    fun onCollage(on: Boolean)
    fun onText(content: String, pos: String, size: String, color: String)
    fun onClock(on: Boolean, pos: String, showDate: Boolean)
    fun onRss(on: Boolean, feeds: List<String>, pos: String, showImage: Boolean, showSource: Boolean, showSummary: Boolean)
    fun rssJson(): String
    fun onOrientation(o: String)
    fun onClear()
    // Library management
    fun listJson(): String                 // {"current":i,"items":[name,...]}
    fun thumbnail(name: String): ByteArray? // small JPEG of one photo
    fun deletePhoto(name: String)
    fun showNow(name: String)
    fun reorder(names: List<String>)
    // Albums
    fun albumsJson(): String
    fun createAlbum(name: String): String   // returns new album id
    fun renameAlbum(id: String, name: String)
    fun deleteAlbum(id: String)
    fun setActiveAlbum(id: String)
    fun addToAlbum(id: String, photo: String)
    fun removeFromAlbum(id: String, photo: String)
    // Studio (per-photo crop)
    fun fullPhoto(name: String): ByteArray?
    fun onTransform(photo: String, scale: Float, x: Float, y: Float)
    fun transformJson(photo: String): String
    // Per-item duration + pin
    fun onDuration(photo: String, seconds: Int)
    fun durationJson(photo: String): String
    fun onPin(photo: String, pinned: Boolean)
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
            session.method == Method.GET && uri == "/photo/download" -> {
                session.parameters["url"]?.firstOrNull()?.let { commands.onPhotoUrl(it) }; ok()
            }
            session.method == Method.GET && uri == "/photo/removeurl" -> {
                session.parameters["url"]?.firstOrNull()?.let { commands.removeByUrl(it) }; ok()
            }
            session.method == Method.GET && uri == "/sources" -> json(commands.sourcesJson())
            session.method == Method.POST && uri == "/video" -> {
                readBody(session)?.let { commands.onVideo(it) }; ok()
            }
            session.method == Method.POST && uri == "/music" -> {
                val title = session.parameters["name"]?.firstOrNull().orEmpty()
                readBody(session)?.let { commands.onMusic(it, title) }; ok()
            }
            session.method == Method.GET && uri == "/music/list" -> json(commands.musicListJson())
            session.method == Method.GET && uri == "/music/delete" -> {
                session.parameters["id"]?.firstOrNull()?.let { commands.musicDelete(it) }; ok()
            }
            session.method == Method.GET && uri == "/music/control" -> {
                session.parameters["a"]?.firstOrNull()?.let { commands.musicControl(it) }; ok()
            }
            session.method == Method.GET && uri == "/music/download" -> {
                val url = session.parameters["url"]?.firstOrNull()
                val title = session.parameters["name"]?.firstOrNull().orEmpty()
                if (url != null) commands.musicDownload(url, title); ok()
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
            session.method == Method.GET && uri == "/framewidth" -> {
                session.parameters["v"]?.firstOrNull()?.toFloatOrNull()?.let { commands.onFrameWidth(it) }; ok()
            }
            session.method == Method.GET && uri == "/framerandom" -> {
                val on = session.parameters["on"]?.firstOrNull() == "1"
                val pool = session.parameters["pool"]?.firstOrNull()?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
                commands.onFrameRandom(on, pool); ok()
            }
            session.method == Method.GET && uri == "/slideshow" -> {
                val interval = session.parameters["interval"]?.firstOrNull()?.toLongOrNull() ?: 8000L
                val shuffle = session.parameters["shuffle"]?.firstOrNull() == "1"
                commands.onSlideshow(interval, shuffle); ok()
            }
            session.method == Method.GET && uri == "/effect" -> {
                session.parameters["e"]?.firstOrNull()?.let { commands.onEffect(it) }; ok()
            }
            session.method == Method.GET && uri == "/effectpool" -> {
                val names = session.parameters["names"]?.firstOrNull()?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                commands.onEffectPool(names); ok()
            }
            session.method == Method.GET && uri == "/fit" -> {
                session.parameters["f"]?.firstOrNull()?.let { commands.onFit(it) }; ok()
            }
            session.method == Method.GET && uri == "/filter" -> {
                session.parameters["f"]?.firstOrNull()?.let { commands.onFilter(it) }; ok()
            }
            session.method == Method.GET && uri == "/collage" -> {
                commands.onCollage(session.parameters["on"]?.firstOrNull() == "1"); ok()
            }
            session.method == Method.GET && uri == "/text" -> {
                commands.onText(
                    session.parameters["content"]?.firstOrNull().orEmpty(),
                    session.parameters["pos"]?.firstOrNull() ?: "bottom",
                    session.parameters["size"]?.firstOrNull() ?: "m",
                    session.parameters["color"]?.firstOrNull() ?: "white",
                ); ok()
            }
            session.method == Method.GET && uri == "/clock" -> {
                commands.onClock(
                    session.parameters["on"]?.firstOrNull() == "1",
                    session.parameters["pos"]?.firstOrNull() ?: "top_start",
                    session.parameters["date"]?.firstOrNull() != "0",
                ); ok()
            }
            session.method == Method.GET && uri == "/orientation" -> {
                session.parameters["o"]?.firstOrNull()?.let { commands.onOrientation(it) }; ok()
            }
            session.method == Method.GET && uri == "/rss" -> {
                val on = session.parameters["on"]?.firstOrNull() == "1"
                val feeds = session.parameters["feeds"]?.firstOrNull()?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
                commands.onRss(
                    on, feeds,
                    session.parameters["pos"]?.firstOrNull() ?: "bottom",
                    session.parameters["image"]?.firstOrNull() == "1",
                    session.parameters["source"]?.firstOrNull() != "0",
                    session.parameters["summary"]?.firstOrNull() == "1",
                ); ok()
            }
            session.method == Method.GET && uri == "/rss/get" -> json(commands.rssJson())
            session.method == Method.GET && uri == "/clear" -> { commands.onClear(); ok() }

            session.method == Method.GET && uri == "/list" -> json(commands.listJson())
            session.method == Method.GET && uri == "/thumb" -> {
                val name = session.parameters["id"]?.firstOrNull()
                val bytes = name?.let { commands.thumbnail(it) }
                if (bytes == null) newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "no thumb")
                else cached(newFixedLengthResponse(
                    Response.Status.OK, "image/jpeg",
                    java.io.ByteArrayInputStream(bytes), bytes.size.toLong(),
                ))
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

            session.method == Method.GET && uri == "/albums" -> json(commands.albumsJson())
            session.method == Method.GET && uri == "/album/create" -> {
                val id = commands.createAlbum(session.parameters["name"]?.firstOrNull().orEmpty())
                json("""{"id":"$id"}""")
            }
            session.method == Method.GET && uri == "/album/rename" -> {
                val id = session.parameters["id"]?.firstOrNull()
                val name = session.parameters["name"]?.firstOrNull()
                if (id != null && name != null) commands.renameAlbum(id, name); ok()
            }
            session.method == Method.GET && uri == "/album/delete" -> {
                session.parameters["id"]?.firstOrNull()?.let { commands.deleteAlbum(it) }; ok()
            }
            session.method == Method.GET && uri == "/album/active" -> {
                session.parameters["id"]?.firstOrNull()?.let { commands.setActiveAlbum(it) }; ok()
            }
            session.method == Method.GET && uri == "/album/add" -> {
                val id = session.parameters["id"]?.firstOrNull()
                val photo = session.parameters["photo"]?.firstOrNull()
                if (id != null && photo != null) commands.addToAlbum(id, photo); ok()
            }
            session.method == Method.GET && uri == "/album/remove" -> {
                val id = session.parameters["id"]?.firstOrNull()
                val photo = session.parameters["photo"]?.firstOrNull()
                if (id != null && photo != null) commands.removeFromAlbum(id, photo); ok()
            }

            session.method == Method.GET && uri == "/full" -> {
                val name = session.parameters["id"]?.firstOrNull()
                val bytes = name?.let { commands.fullPhoto(it) }
                if (bytes == null) newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "no photo")
                else cached(newFixedLengthResponse(
                    Response.Status.OK, "image/jpeg",
                    java.io.ByteArrayInputStream(bytes), bytes.size.toLong(),
                ))
            }
            session.method == Method.GET && uri == "/transform" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                val s = session.parameters["scale"]?.firstOrNull()?.toFloatOrNull()
                val x = session.parameters["x"]?.firstOrNull()?.toFloatOrNull()
                val y = session.parameters["y"]?.firstOrNull()?.toFloatOrNull()
                if (photo != null && s != null && x != null && y != null) commands.onTransform(photo, s, x, y)
                ok()
            }
            session.method == Method.GET && uri == "/transform/get" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                json(if (photo != null) commands.transformJson(photo) else """{"s":1,"x":0,"y":0}""")
            }
            session.method == Method.GET && uri == "/duration" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                val sec = session.parameters["sec"]?.firstOrNull()?.toIntOrNull()
                if (photo != null && sec != null) commands.onDuration(photo, sec); ok()
            }
            session.method == Method.GET && uri == "/duration/get" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                json(if (photo != null) commands.durationJson(photo) else """{"sec":0}""")
            }
            session.method == Method.GET && uri == "/pin" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                val on = session.parameters["on"]?.firstOrNull() == "1"
                if (photo != null) commands.onPin(photo, on); ok()
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

    /** Marks an image response as immutable so clients (Coil) cache it aggressively. */
    private fun cached(r: Response): Response {
        r.addHeader("Cache-Control", "public, max-age=31536000, immutable")
        return r
    }
}
