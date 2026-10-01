package com.meylon.salongallery.net

import org.json.JSONObject
import java.io.File

/**
 * Per-item display duration in seconds. 0 (absent) means "use the slideshow default"
 * for a photo, or "play the whole clip" for a video.
 */
class DurationStore(private val file: File) {

    private val map = LinkedHashMap<String, Int>()

    init { load() }

    @Synchronized
    fun get(name: String): Int = map[name] ?: 0

    @Synchronized
    fun set(name: String, seconds: Int) {
        if (seconds <= 0) map.remove(name) else map[name] = seconds
        save()
    }

    @Synchronized
    fun remove(name: String) { map.remove(name); save() }

    private fun load() {
        runCatching {
            if (file.exists()) {
                val o = JSONObject(file.readText())
                o.keys().forEach { k -> map[k] = o.optInt(k) }
            }
        }
    }

    private fun save() {
        runCatching {
            val o = JSONObject()
            map.forEach { (k, v) -> o.put(k, v) }
            file.writeText(o.toString())
        }
    }
}
