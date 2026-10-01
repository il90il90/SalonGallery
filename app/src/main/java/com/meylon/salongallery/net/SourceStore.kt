package com.meylon.salongallery.net

import org.json.JSONObject
import java.io.File

/**
 * Remembers which source URL each downloaded library item came from, so the Remote can
 * toggle an art piece off the wall (remove it) as well as on.
 */
class SourceStore(private val file: File) {

    private val map = LinkedHashMap<String, String>()  // library name -> source url

    init { load() }

    @Synchronized
    fun set(name: String, url: String) { map[name] = url; save() }

    @Synchronized
    fun remove(name: String) { map.remove(name); save() }

    /** Library item names that came from [url]. */
    @Synchronized
    fun namesForUrl(url: String): List<String> = map.filterValues { it == url }.keys.toList()

    /** All source URLs currently recorded (restricted to names that still exist). */
    @Synchronized
    fun presentUrls(existing: Set<String>): Set<String> =
        map.filterKeys { it in existing }.values.toSet()

    @Synchronized
    fun clear() { map.clear(); save() }

    private fun load() {
        runCatching {
            if (file.exists()) {
                val o = JSONObject(file.readText())
                o.keys().forEach { k -> map[k] = o.optString(k) }
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
