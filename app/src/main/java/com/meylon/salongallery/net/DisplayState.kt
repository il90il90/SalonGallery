package com.meylon.salongallery.net

import java.io.File

/** What the Display is currently showing. */
enum class DisplayMode { WAITING, SLIDESHOW, VIDEO }

enum class SlideOrder { SEQUENTIAL, SHUFFLE }

enum class ScreenOrientation { AUTO, PORTRAIT, LANDSCAPE }

enum class SlideEffect { NONE, FADE, SLIDE, ZOOM, KENBURNS;
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

enum class TextPos { TOP, CENTER, BOTTOM;
    companion object { fun from(s: String) = entries.firstOrNull { it.name.equals(s, true) } ?: BOTTOM }
}

/** A text overlay shown on the display over the media. */
data class TextOverlay(
    val content: String = "",
    val pos: TextPos = TextPos.BOTTOM,
    val size: String = "m",     // s | m | l
    val color: String = "white",
)

/**
 * A growing, ORDERED library of photos on the Display device. The order is kept in
 * `order.txt` so the Remote can reorder, delete and jump between photos.
 */
class LibraryStore(private val dir: File) {

    private val orderFile = File(dir, "order.txt")

    init { runCatching { dir.mkdirs() } }

    private fun photoFiles(): List<File> =
        dir.listFiles()?.filter { it.isFile && it.name.startsWith("p_") } ?: emptyList()

    @Synchronized
    fun add(bytes: ByteArray): File {
        val f = File(dir, "p_${System.currentTimeMillis()}_${(0..99999).random()}.jpg")
        f.writeBytes(bytes)
        runCatching { orderFile.appendText(f.name + "\n") }
        return f
    }

    /** Files in the saved order; any not yet in the order file are appended. */
    @Synchronized
    fun list(): List<File> {
        val byName = photoFiles().associateBy { it.name }
        val order = readOrder().filter { byName.containsKey(it) }
        val ordered = order.mapNotNull { byName[it] }
        val missing = byName.keys - order.toSet()
        val result = ordered + missing.mapNotNull { byName[it] }
        if (missing.isNotEmpty()) writeOrder(result.map { it.name })
        return result
    }

    fun names(): List<String> = list().map { it.name }

    fun fileFor(name: String): File? {
        val f = File(dir, name)
        return if (f.exists() && f.name.startsWith("p_")) f else null
    }

    fun count(): Int = photoFiles().size

    @Synchronized
    fun delete(name: String) {
        runCatching { File(dir, name).delete() }
        writeOrder(readOrder().filter { it != name })
    }

    @Synchronized
    fun reorder(names: List<String>) {
        val existing = photoFiles().map { it.name }.toSet()
        writeOrder(names.filter { existing.contains(it) })
    }

    @Synchronized
    fun clear() {
        runCatching { photoFiles().forEach { it.delete() } }
        runCatching { orderFile.delete() }
    }

    private fun readOrder(): List<String> =
        if (orderFile.exists()) runCatching { orderFile.readLines().filter { it.isNotBlank() } }.getOrDefault(emptyList())
        else emptyList()

    private fun writeOrder(names: List<String>) {
        runCatching { orderFile.writeText(names.joinToString("\n")) }
    }
}
