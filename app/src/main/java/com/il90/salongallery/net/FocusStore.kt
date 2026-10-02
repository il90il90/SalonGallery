package com.il90.salongallery.net

import org.json.JSONObject
import java.io.File

/**
 * Where the interesting part of a photo is — the centre of its faces, normalised 0..1 in the
 * picture as drawn (EXIF applied, before any display rotation) — and that picture's [aspect]
 * (width / height). Used to aim a cropped photo at the faces rather than its middle.
 */
data class PhotoFocus(val x: Float, val y: Float, val aspect: Float)

/** Persists per-photo [PhotoFocus] (found by face detection) in focus.json. */
class FocusStore(private val file: File) {

    private val map = HashMap<String, PhotoFocus>()

    init { load() }

    @Synchronized
    private fun load() {
        if (!file.exists()) return
        runCatching {
            val o = JSONObject(file.readText())
            o.keys().forEach { k ->
                val f = o.getJSONObject(k)
                map[k] = PhotoFocus(f.optDouble("x", 0.5).toFloat(), f.optDouble("y", 0.5).toFloat(), f.optDouble("a", 1.0).toFloat())
            }
        }
    }

    @Synchronized
    private fun save() {
        runCatching {
            val o = JSONObject()
            map.forEach { (k, f) -> o.put(k, JSONObject().apply { put("x", f.x.toDouble()); put("y", f.y.toDouble()); put("a", f.aspect.toDouble()) }) }
            file.writeText(o.toString())
        }
    }

    @Synchronized fun get(name: String): PhotoFocus? = map[name]

    @Synchronized fun set(name: String, f: PhotoFocus) { map[name] = f; save() }

    @Synchronized fun remove(name: String) { if (map.remove(name) != null) save() }
}
