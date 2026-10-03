package com.il90.salongallery.net

import fi.iki.elonen.NanoHTTPD

/** Commands the Display device reacts to when the server receives a request. */
interface ScreenCommands {
    fun onPhoto(bytes: ByteArray, album: String? = null)   // append to the library (into [album] when given)
    fun onPhotoUrl(url: String)            // download an image and append to the library
    fun removeByUrl(url: String)           // remove library items that came from this url
    fun sourcesJson(): String              // {"urls":[...]} of source urls currently on the wall
    fun onVideo(bytes: ByteArray, album: String? = null)   // add this clip (into [album] when given)
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
    fun onDefaultDuration(seconds: Int)    // the slideshow default, in seconds, for all "Default" photos
    fun onEffect(effect: String)
    fun onEffectPool(names: List<String>)
    fun onFilterPool(names: List<String>)
    fun onFit(fit: String)
    fun onBackground(color: String)   // black | charcoal | slate | warm | white
    fun onFilter(filter: String)
    fun onCollage(on: Boolean)
    fun onLayout(mode: String)             // single | mosaic | scatter | random
    fun onStagger(on: Boolean)             // spread photos appear one by one
    fun onSmartGroup(on: Boolean)          // group visually-similar photos into each spread
    fun onSpreadMix(level: String)         // always | often | sometimes | rarely
    fun onMotion(mode: String, speed: String) // off | zoom | drift | breathe | mix ; slow | medium | fast
    fun onText(content: String, pos: String, size: String, color: String, font: String)
    fun onClock(on: Boolean, pos: String, showDate: Boolean, style: String, size: String)
    fun onWeather(on: Boolean, place: String, lat: Double, lon: Double, units: String, pos: String, style: String = "pill")
    fun overlaysJson(): String             // current clock/weather/text state, so the Remote reflects reality
    fun onOpenScreensaver()
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
    /** Turn a photo by [by] degrees (±90 steps) on top of its current rotation. */
    fun onRotate(photo: String, by: Int)
    // Per-item duration + pin
    fun onDuration(photo: String, seconds: Int)
    fun durationJson(photo: String): String
    fun onPin(photo: String, pinned: Boolean)
    // Screen admin (controlled from the Remote): name, auto-sleep schedule, screen PIN, change role
    fun onRename(name: String)
    fun settingsJson(): String
    fun onSchedule(on: Boolean, start: Int, end: Int)
    fun onSetScreenPin(code: String)
    fun screenPinCheckJson(code: String): String
    fun onResetRole()
}

/**
 * HTTP server on the Display device. POST /photo (append), /video, /music take raw
 * bytes; GET commands (/brightness /volume /frame /slideshow /orientation /clear)
 * carry parameters. /ping returns device + screen + storage info.
 */
class PhotoServer(
    private val pingBody: () -> String,
    private val commands: ScreenCommands,
    port: Int = FIXED_PORT,
    private val nowBody: () -> String = { "{}" },
) : NanoHTTPD(port) {

    companion object {
        /** A fixed, well-known port so the Remote can connect by IP alone (no port needed). */
        const val FIXED_PORT = 50505
    }

    override fun serve(session: IHTTPSession): Response = try {
        val uri = session.uri
        when {
            session.method == Method.GET && uri == "/ping" -> json(pingBody())
            // The exact composition of the slide on the wall right now, so the Remote can mirror it.
            session.method == Method.GET && uri == "/now" -> json(nowBody())

            session.method == Method.POST && uri == "/photo" -> {
                // Optional ?album=<id> files the upload into that album instead of the active one.
                val album = session.parameters["album"]?.firstOrNull()?.takeIf { it.isNotBlank() }
                readBody(session)?.let { commands.onPhoto(it, album) }; ok()
            }
            session.method == Method.GET && uri == "/photo/download" -> {
                session.parameters["url"]?.firstOrNull()?.let { commands.onPhotoUrl(it) }; ok()
            }
            session.method == Method.GET && uri == "/photo/removeurl" -> {
                session.parameters["url"]?.firstOrNull()?.let { commands.removeByUrl(it) }; ok()
            }
            session.method == Method.GET && uri == "/sources" -> json(commands.sourcesJson())
            session.method == Method.POST && uri == "/video" -> {
                val album = session.parameters["album"]?.firstOrNull()?.takeIf { it.isNotBlank() }
                readBody(session)?.let { commands.onVideo(it, album) }; ok()
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
            session.method == Method.GET && uri == "/defaultdur" -> {
                session.parameters["sec"]?.firstOrNull()?.toIntOrNull()?.let { commands.onDefaultDuration(it) }; ok()
            }
            session.method == Method.GET && uri == "/effect" -> {
                session.parameters["e"]?.firstOrNull()?.let { commands.onEffect(it) }; ok()
            }
            session.method == Method.GET && uri == "/effectpool" -> {
                val names = session.parameters["names"]?.firstOrNull()?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                commands.onEffectPool(names); ok()
            }
            session.method == Method.GET && uri == "/filterpool" -> {
                val names = session.parameters["names"]?.firstOrNull()?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                commands.onFilterPool(names); ok()
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
            session.method == Method.GET && uri == "/layout" -> {
                session.parameters["mode"]?.firstOrNull()?.let { commands.onLayout(it) }; ok()
            }
            session.method == Method.GET && uri == "/spreadmix" -> {
                session.parameters["level"]?.firstOrNull()?.let { commands.onSpreadMix(it) }; ok()
            }
            session.method == Method.GET && uri == "/stagger" -> {
                commands.onStagger(session.parameters["on"]?.firstOrNull() == "1"); ok()
            }
            session.method == Method.GET && uri == "/smartgroup" -> {
                commands.onSmartGroup(session.parameters["on"]?.firstOrNull() == "1"); ok()
            }
            session.method == Method.GET && uri == "/motion" -> {
                commands.onMotion(
                    session.parameters["mode"]?.firstOrNull().orEmpty(),
                    session.parameters["speed"]?.firstOrNull().orEmpty(),
                ); ok()
            }
            session.method == Method.GET && uri == "/text" -> {
                commands.onText(
                    session.parameters["content"]?.firstOrNull().orEmpty(),
                    session.parameters["pos"]?.firstOrNull() ?: "bottom",
                    session.parameters["size"]?.firstOrNull() ?: "m",
                    session.parameters["color"]?.firstOrNull() ?: "white",
                    session.parameters["font"]?.firstOrNull() ?: "classic",
                ); ok()
            }
            session.method == Method.GET && uri == "/clock" -> {
                commands.onClock(
                    session.parameters["on"]?.firstOrNull() == "1",
                    session.parameters["pos"]?.firstOrNull() ?: "top_start",
                    session.parameters["date"]?.firstOrNull() != "0",
                    session.parameters["style"]?.firstOrNull() ?: "digital",
                    session.parameters["size"]?.firstOrNull() ?: "m",
                ); ok()
            }
            session.method == Method.GET && uri == "/bg" -> {
                session.parameters["color"]?.firstOrNull()?.let { commands.onBackground(it) }; ok()
            }
            session.method == Method.GET && uri == "/weather" -> {
                commands.onWeather(
                    session.parameters["on"]?.firstOrNull() == "1",
                    session.parameters["place"]?.firstOrNull().orEmpty(),
                    session.parameters["lat"]?.firstOrNull()?.toDoubleOrNull() ?: 0.0,
                    session.parameters["lon"]?.firstOrNull()?.toDoubleOrNull() ?: 0.0,
                    session.parameters["units"]?.firstOrNull() ?: "c",
                    session.parameters["pos"]?.firstOrNull() ?: "bottom_start",
                    session.parameters["style"]?.firstOrNull() ?: "pill",
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
            session.method == Method.GET && uri == "/overlays" -> json(commands.overlaysJson())
            session.method == Method.GET && uri == "/rss/get" -> json(commands.rssJson())
            session.method == Method.GET && uri == "/screensaver" -> { commands.onOpenScreensaver(); ok() }
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
                // Accepts one id or a comma-separated batch (multi-select delete from the Remote).
                session.parameters["id"]?.firstOrNull()?.split(",")?.filter { it.isNotBlank() }
                    ?.forEach { commands.deletePhoto(it) }
                ok()
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
                json(if (photo != null) commands.transformJson(photo) else """{"s":1,"x":0,"y":0,"r":0}""")
            }
            session.method == Method.GET && uri == "/rotate" -> {
                val photo = session.parameters["photo"]?.firstOrNull()
                val by = session.parameters["by"]?.firstOrNull()?.toIntOrNull() ?: 90
                if (photo != null) commands.onRotate(photo, by)
                ok()
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

            session.method == Method.GET && uri == "/rename" -> {
                session.parameters["name"]?.firstOrNull()?.let { commands.onRename(it) }; ok()
            }
            session.method == Method.GET && uri == "/settings/get" -> json(commands.settingsJson())
            session.method == Method.GET && uri == "/schedule" -> {
                val on = session.parameters["on"]?.firstOrNull() == "1"
                val start = session.parameters["start"]?.firstOrNull()?.toIntOrNull() ?: 1380
                val end = session.parameters["end"]?.firstOrNull()?.toIntOrNull() ?: 420
                commands.onSchedule(on, start, end); ok()
            }
            session.method == Method.GET && uri == "/screenpin" -> {
                commands.onSetScreenPin(session.parameters["code"]?.firstOrNull() ?: ""); ok()
            }
            session.method == Method.GET && uri == "/screenpin/check" ->
                json(commands.screenPinCheckJson(session.parameters["code"]?.firstOrNull() ?: ""))
            session.method == Method.GET && uri == "/resetrole" -> { commands.onResetRole(); ok() }

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
