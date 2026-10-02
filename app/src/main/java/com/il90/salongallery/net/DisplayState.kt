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
enum class ClockStyle { DIGITAL, ANALOG, MINIMAL, MONO;
    companion object { fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: DIGITAL }
}

/** The clock / date overlay configuration. */
data class ClockConfig(
    val on: Boolean = false,
    val pos: ClockPos = ClockPos.TOP_START,
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
)

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

    @Synchronized
    fun add(bytes: ByteArray): File = addNamed("p_${stamp()}.jpg", bytes)

    @Synchronized
    fun addVideo(bytes: ByteArray): File = addNamed("v_${stamp()}.mp4", bytes)

    private fun addNamed(name: String, bytes: ByteArray): File {
        val f = File(dir, name)
        f.writeBytes(bytes)
        runCatching { orderFile.appendText(f.name + "\n") }
        return f
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

    @Synchronized
    fun delete(name: String) {
        runCatching { File(dir, name).delete() }
        writeOrder(readOrder().filter { it != name })
        writePins(readPins().filter { it != name })
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
    }

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
