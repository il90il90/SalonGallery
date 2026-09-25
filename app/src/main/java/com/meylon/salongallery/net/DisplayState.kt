package com.meylon.salongallery.net

import java.io.File

/** What the Display is currently showing. */
enum class DisplayMode { WAITING, SLIDESHOW, VIDEO }

enum class SlideOrder { SEQUENTIAL, SHUFFLE }

enum class ScreenOrientation { AUTO, PORTRAIT, LANDSCAPE }

/** A growing library of photos stored on the Display device. */
class LibraryStore(private val dir: File) {

    init { runCatching { dir.mkdirs() } }

    fun add(bytes: ByteArray): File {
        val f = File(dir, "p_${System.currentTimeMillis()}_${(0..99999).random()}.jpg")
        f.writeBytes(bytes)
        return f
    }

    /** Files sorted by name — names embed a timestamp, so this is chronological. */
    fun list(): List<File> =
        dir.listFiles()?.filter { it.isFile }?.sortedBy { it.name } ?: emptyList()

    fun count(): Int = dir.listFiles()?.count { it.isFile } ?: 0

    fun clear() {
        runCatching { dir.listFiles()?.forEach { it.delete() } }
    }
}
