package com.meylon.salongallery.net

import org.json.JSONObject
import java.io.File

/** A library of audio tracks on the Display device, each with a friendly title. */
class MusicStore(private val dir: File) {

    private val titlesFile = File(dir, "titles.json")
    private val titles = HashMap<String, String>()

    init {
        runCatching { dir.mkdirs() }
        loadTitles()
    }

    private fun loadTitles() {
        titles.clear()
        if (!titlesFile.exists()) return
        runCatching {
            val o = JSONObject(titlesFile.readText())
            o.keys().forEach { titles[it] = o.getString(it) }
        }
    }

    private fun saveTitles() {
        runCatching { titlesFile.writeText(JSONObject(titles as Map<*, *>).toString()) }
    }

    private fun audioFiles(): List<File> =
        dir.listFiles()?.filter { it.isFile && it.name.startsWith("m_") }?.sortedBy { it.name } ?: emptyList()

    @Synchronized
    fun add(bytes: ByteArray, title: String): File {
        val f = File(dir, "m_${System.currentTimeMillis()}_${(0..99999).random()}.aud")
        f.writeBytes(bytes)
        titles[f.name] = title.ifBlank { "Track ${audioFiles().size}" }
        saveTitles()
        return f
    }

    @Synchronized fun list(): List<File> = audioFiles()
    @Synchronized fun count(): Int = audioFiles().size
    @Synchronized fun titleOf(name: String): String = titles[name] ?: name
    @Synchronized fun fileFor(name: String): File? = File(dir, name).takeIf { it.exists() && it.name.startsWith("m_") }

    @Synchronized
    fun delete(name: String) {
        runCatching { File(dir, name).delete() }
        titles.remove(name); saveTitles()
    }

    /** {"items":[{"name","title"}]} */
    @Synchronized
    fun listJson(): String {
        val arr = audioFiles().joinToString(",") { f ->
            """{"name":"${esc(f.name)}","title":"${esc(titleOf(f.name))}"}"""
        }
        return """{"items":[$arr]}"""
    }

    private fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
}
