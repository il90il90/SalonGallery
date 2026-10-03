package com.il90.salongallery.net

import java.io.File

/** What the Display is currently showing. */
enum class DisplayMode { WAITING, SLIDESHOW, VIDEO }

enum class SlideOrder { SEQUENTIAL, SHUFFLE }

enum class ScreenOrientation { AUTO, PORTRAIT, LANDSCAPE }

enum class SlideEffect {
    NONE, FADE, DISSOLVE, SLIDE, SLIDERIGHT, SLIDEUP, SLIDEDOWN,
    ZOOM, ZOOMOUT, REVEAL, GROW, SWAP, DRIFT, CARDSTACK, KENBURNS, RANDOM;
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: FADE
    }
}

/**
 * How slides are composed: one photo per slide, or a "spread" of 4–5 photos — a MOSAIC grid on a
 * mat, a SCATTER of tilted prints on a table, and the planned layouts (GRID … COLUMNS, see
 * ui/SpreadLayouts.kt) — or RANDOM, a seeded per-slide mix of single photos and every spread.
 */
enum class LayoutMode { SINGLE, AUTO, MOSAIC, SCATTER, GRID, WINDOW, QUILT, TRIPTYCH, ROWS, COLUMNS, MASONRY, POLAROID, FILMSTRIP, STACK, FAN, GALLERY,
    CLOTHESLINE, BUBBLES, MAGAZINE, CAROUSEL, PYRAMID, DIAMOND, CROSS, CORNERS, SPIRAL, WAVE, SPOTLIGHT, POSTCARDS,
    COLLAGE, FRAMES, PATCHWORK, OVERLAP, DIAGONAL, RANDOM;
    // AUTO and RANDOM are meta-modes (they pick spreads per slide), not spreads themselves.
    val isSpread get() = this != SINGLE && this != RANDOM && this != AUTO
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: SINGLE
        val SPREADS = entries.filter { it.isSpread }
    }
}

/**
 * A subtle "living photo" motion played while a still waits on screen: a slow ZOOM (breathing in
 * and out), a gentle DRIFT (pan across a slightly enlarged photo), a soft BREATHE (fade down and back
 * up), or MIX — a seeded pick of the three per slide. OFF keeps the photo perfectly still.
 */
enum class MotionMode { OFF, ZOOM, DRIFT, BREATHE, SWAY, GLIDE,
    FLOAT, SWING, PULSE, TILT, TILTY, RISE, FALL, SLIDEX, WOBBLE, PARALLAX, GLOW, ROCK, MIX;
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: OFF
    }
}

/** How fast the [MotionMode] runs — the length of one sweep (there and back is twice this). */
enum class MotionSpeed(val sweepMs: Int) { SLOW(24000), MEDIUM(12000), FAST(6000);
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: MEDIUM
    }
}

/**
 * How often a multi-photo spread appears when a spread layout (or Random) is chosen — the rest of
 * the slides show one photo. [chance] is the per-slide probability (seeded, so stable per slide).
 */
enum class SpreadMix(val chance: Float) { ALWAYS(1f), OFTEN(0.66f), SOMETIMES(0.4f), RARELY(0.2f);
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: ALWAYS
    }
}

/** The slide currently on the wall: its [style] (a LayoutMode name lower-cased, or "single"), the
 * per-slide [seed] the layout was drawn with, and the photo file [members] in order. */
data class NowSlide(val style: String, val members: List<String>, val seed: Int)

/** How a photo is scaled to the screen. */
enum class PhotoFit { FILL, FIT, BLUR;
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: FILL
    }
}

/** A colour "look" applied over the media. */
enum class PhotoFilter {
    NONE, MONO, SEPIA, WARM, COOL, VIGNETTE,
    VIVID, NOIR, FADE, CINEMA, GOLDEN, DUSK, FROST, POP, MATTE, ROSE,
    RANDOM;
    companion object {
        fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: NONE
    }
}

enum class TextPos { TOP, CENTER, BOTTOM;
    companion object { fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: BOTTOM }
}

/** Screen zone for the clock / date overlay. */
enum class ClockPos { TOP_START, TOP_END, BOTTOM_START, BOTTOM_END, CENTER;
    companion object { fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: TOP_START }
}

/** The look of the clock on the screen and the idle/sleep view. */
enum class ClockStyle { DIGITAL, ANALOG, MINIMAL, MONO, BOLD, LED, CARD;
    companion object { fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: DIGITAL }
}

/** The clock / date overlay configuration. */
data class ClockConfig(
    val on: Boolean = false,
    val pos: ClockPos = ClockPos.BOTTOM_START,
    val showDate: Boolean = true,
    val style: ClockStyle = ClockStyle.DIGITAL,
    val size: String = "m",   // s | m | l — scales the clock
)

/** A text overlay shown on the display over the media. */
data class TextOverlay(
    val content: String = "",
    val pos: TextPos = TextPos.BOTTOM,
    val size: String = "m",     // s | m | l
    val color: String = "white",
    val font: String = "classic",   // classic | modern | mono | elegant | rounded
)

/** On-screen weather (via Open-Meteo — no API key). [place] is the shown name; [lat]/[lon] drive the
 *  fetch; [units] is c|f. [temp] / [code] are the latest reading (code is a WMO weather code). */
data class WeatherConfig(
    val on: Boolean = false,
    val place: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val units: String = "c",
    val pos: ClockPos = ClockPos.BOTTOM_START,
    val style: String = "pill",   // pill | minimal | card | stacked
)
data class WeatherNow(val temp: Int = 0, val code: Int = -1, val ok: Boolean = false)

/** True when a library item name refers to a video clip (vs a still photo). */
fun isVideoName(name: String): Boolean = name.startsWith("v_")

/**
 * A growing, ORDERED library of mixed MEDIA (photos `p_*.jpg` and videos `v_*.mp4`)
 * on the Display device. The order is kept in `order.txt` so the Remote can reorder,
 * pin, delete and jump between items. Pinned items are listed first (in `pins.txt`).
 */
class LibraryStore(private val dir: File) {

    private val orderFile = File(dir, "order.txt")
    private val pinFile = File(dir, "pins.txt")

    init { runCatching { dir.mkdirs() } }

    private fun mediaFiles(): List<File> =
        dir.listFiles()?.filter { it.isFile && (it.name.startsWith("p_") || it.name.startsWith("v_")) } ?: emptyList()

    // Content signatures (size + MD5) of every stored file, so an identical photo/video is only kept
    // once — adding a duplicate (or re-syncing a folder) returns null instead of a second copy.
    private val sigFile = File(dir, "sigs.txt")
    private val sigs: HashMap<String, String> by lazy { loadSigs() }   // name -> signature
    private fun loadSigs(): HashMap<String, String> {
        val map = HashMap<String, String>()
        val have = mediaFiles().map { it.name }.toSet()
        runCatching { if (sigFile.exists()) sigFile.readLines().forEach { ln ->
            val i = ln.indexOf('\t'); if (i > 0) { val n = ln.substring(0, i); if (n in have) map[n] = ln.substring(i + 1) }
        } }
        return map
    }
    private fun sigOf(bytes: ByteArray): String = runCatching {
        val md = java.security.MessageDigest.getInstance("MD5").digest(bytes)
        bytes.size.toString() + ":" + md.joinToString("") { "%02x".format(it) }
    }.getOrDefault(bytes.size.toString())

    /** True if a file with the same content is already in the library. */
    @Synchronized
    fun isDuplicate(bytes: ByteArray): Boolean = sigOf(bytes) in sigs.values

    // Source signatures (MD5 of the ORIGINAL file, computed on the sender before any optimisation) for
    // photos uploaded with one. This catches the one case the content signature can't: the SAME source
    // photo sent twice at a different quality — the stored bytes differ, but the source is the same, so
    // it must not land twice.
    private val srcFile = File(dir, "srcsigs.txt")
    private val srcSigs: HashMap<String, String> by lazy { loadSrcSigs() }   // name -> source signature
    private fun loadSrcSigs(): HashMap<String, String> {
        val map = HashMap<String, String>()
        val have = mediaFiles().map { it.name }.toSet()
        runCatching { if (srcFile.exists()) srcFile.readLines().forEach { ln ->
            val i = ln.indexOf('\t'); if (i > 0) { val n = ln.substring(0, i); if (n in have) map[n] = ln.substring(i + 1) }
        } }
        return map
    }

    /** True if a photo with this source signature is already in the library. */
    @Synchronized
    fun isDuplicateSource(srcSig: String): Boolean = srcSig in srcSigs.values

    /** Record the source signature of an already-stored file, so a later re-send of the same source
     *  (even at a different quality) is recognised as a duplicate. */
    @Synchronized
    fun rememberSource(name: String, srcSig: String) {
        srcSigs[name] = srcSig
        runCatching { srcFile.appendText("$name\t$srcSig\n") }
    }

    /** Adds a photo; returns null (no copy written) if the exact content is already present. */
    @Synchronized
    fun add(bytes: ByteArray): File? = addNamed("p_${stamp()}.jpg", bytes)

    /** Adds a video; returns null if the exact content is already present. */
    @Synchronized
    fun addVideo(bytes: ByteArray): File? = addNamed("v_${stamp()}.mp4", bytes)

    private fun addNamed(name: String, bytes: ByteArray): File? {
        val sig = sigOf(bytes)
        if (sig in sigs.values) return null        // duplicate — skip
        val f = File(dir, name)
        f.writeBytes(bytes)
        runCatching { orderFile.appendText(f.name + "\n") }
        sigs[f.name] = sig
        runCatching { sigFile.appendText("${f.name}\t$sig\n") }
        return f
    }

    /** Content signature of a file, computed by streaming it (so a huge video is never held in memory).
     *  Same format as [sigOf]: "size:md5". */
    private fun sigOfFile(f: File): String = runCatching {
        val md = java.security.MessageDigest.getInstance("MD5")
        f.inputStream().use { ins ->
            val buf = ByteArray(1 shl 16)
            while (true) { val r = ins.read(buf); if (r <= 0) break; md.update(buf, 0, r) }
        }
        f.length().toString() + ":" + md.digest().joinToString("") { "%02x".format(it) }
    }.getOrDefault(f.length().toString())

    /**
     * Adds a video that has already been streamed to [src] on disk (so the clip was never held whole
     * in memory). Dedups by content — a duplicate deletes [src] and returns null — otherwise moves it
     * into the library as a `v_*.mp4`. Returns the stored file, or null on duplicate/failure.
     */
    @Synchronized
    fun addVideoFile(src: File): File? {
        if (!src.exists() || src.length() <= 0L) { runCatching { src.delete() }; return null }
        val sig = sigOfFile(src)
        if (sig in sigs.values) { runCatching { src.delete() }; return null }   // duplicate — skip
        val dest = File(dir, "v_${stamp()}.mp4")
        val moved = runCatching { src.renameTo(dest) }.getOrDefault(false) ||
            runCatching { src.copyTo(dest, overwrite = true); src.delete(); true }.getOrDefault(false)
        if (!moved || !dest.exists()) { runCatching { src.delete() }; return null }
        runCatching { orderFile.appendText(dest.name + "\n") }
        sigs[dest.name] = sig
        runCatching { sigFile.appendText("${dest.name}\t$sig\n") }
        return dest
    }

    private fun stamp() = "${System.currentTimeMillis()}_${(0..99999).random()}"

    /** Files in the saved order, with pinned items floated to the front. */
    @Synchronized
    fun list(): List<File> {
        val byName = mediaFiles().associateBy { it.name }
        val order = readOrder().filter { byName.containsKey(it) }
        val ordered = order.mapNotNull { byName[it] }
        val missing = byName.keys - order.toSet()
        var result = ordered + missing.mapNotNull { byName[it] }
        if (missing.isNotEmpty()) writeOrder(result.map { it.name })
        val pins = readPins().filter { byName.containsKey(it) }
        if (pins.isNotEmpty()) {
            val pinned = pins.mapNotNull { byName[it] }
            result = pinned + result.filter { it.name !in pins.toSet() }
        }
        return result
    }

    fun names(): List<String> = list().map { it.name }

    fun fileFor(name: String): File? {
        val f = File(dir, name)
        return if (f.exists() && (f.name.startsWith("p_") || f.name.startsWith("v_"))) f else null
    }

    fun count(): Int = mediaFiles().size

    /**
     * Byte-identical duplicates already sitting in the library (e.g. added before content-dedup
     * existed, or an optimised copy of an original): the FIRST file of each content signature in
     * library order is kept, the rest are returned as names to delete. Signatures are computed by
     * streaming, so a big video is never held in memory, and the [sigs] cache is backfilled for any
     * legacy file that never had one. Caller deletes the returned names (so album/transform/etc. are
     * cleaned up too).
     */
    @Synchronized
    fun duplicateNames(): List<String> {
        val seen = HashSet<String>()
        val dups = ArrayList<String>()
        for (f in list()) {                       // library order, pinned first → the kept copy
            if (!f.exists()) continue
            val sig = sigs[f.name] ?: sigOfFile(f).also { sigs[f.name] = it }
            if (!seen.add(sig)) dups.add(f.name)  // this content was already seen → a duplicate
        }
        if (dups.isNotEmpty()) runCatching { saveSigs() }
        return dups
    }

    @Synchronized
    fun delete(name: String) {
        runCatching { File(dir, name).delete() }
        writeOrder(readOrder().filter { it != name })
        writePins(readPins().filter { it != name })
        if (sigs.remove(name) != null) runCatching { saveSigs() }
        if (srcSigs.remove(name) != null) runCatching { saveSrcSigs() }
    }

    @Synchronized
    fun reorder(names: List<String>) {
        val existing = mediaFiles().map { it.name }.toSet()
        writeOrder(names.filter { existing.contains(it) })
    }

    /** Toggle (or set) whether an item is pinned to the front of the library. */
    @Synchronized
    fun setPinned(name: String, pinned: Boolean) {
        val current = readPins().filter { it != name }
        writePins(if (pinned) listOf(name) + current else current)
    }

    fun pinnedNames(): List<String> = readPins().filter { File(dir, it).exists() }

    @Synchronized
    fun clear() {
        runCatching { mediaFiles().forEach { it.delete() } }
        runCatching { orderFile.delete() }
        runCatching { pinFile.delete() }
        sigs.clear(); runCatching { sigFile.delete() }
        srcSigs.clear(); runCatching { srcFile.delete() }
    }

    private fun saveSigs() = runCatching { sigFile.writeText(sigs.entries.joinToString("\n") { "${it.key}\t${it.value}" }) }
    private fun saveSrcSigs() = runCatching { srcFile.writeText(srcSigs.entries.joinToString("\n") { "${it.key}\t${it.value}" }) }

    private fun readOrder(): List<String> =
        if (orderFile.exists()) runCatching { orderFile.readLines().filter { it.isNotBlank() } }.getOrDefault(emptyList())
        else emptyList()

    private fun writeOrder(names: List<String>) {
        runCatching { orderFile.writeText(names.joinToString("\n")) }
    }

    private fun readPins(): List<String> =
        if (pinFile.exists()) runCatching { pinFile.readLines().filter { it.isNotBlank() } }.getOrDefault(emptyList())
        else emptyList()

    private fun writePins(names: List<String>) {
        runCatching { pinFile.writeText(names.joinToString("\n")) }
    }
}
