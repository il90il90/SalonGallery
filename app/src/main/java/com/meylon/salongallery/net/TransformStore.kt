package com.meylon.salongallery.net

import org.json.JSONObject
import java.io.File

/** A per-photo crop: [scale] (>=1 zoom) and normalized pan offsets (fraction of the screen). */
data class PhotoTransform(val scale: Float, val offX: Float, val offY: Float) {
    companion object { val NONE = PhotoTransform(1f, 0f, 0f) }
    fun isIdentity() = scale <= 1.001f && kotlin.math.abs(offX) < 0.001f && kotlin.math.abs(offY) < 0.001f
}

/** Persists per-photo transforms (studio edits) in transforms.json. */
class TransformStore(private val file: File) {

    private val map = HashMap<String, PhotoTransform>()

    init { load() }

    @Synchronized
    private fun load() {
        map.clear()
        if (!file.exists()) return
        runCatching {
            val o = JSONObject(file.readText())
            o.keys().forEach { k ->
                val t = o.getJSONObject(k)
                map[k] = PhotoTransform(
                    t.optDouble("s", 1.0).toFloat(),
                    t.optDouble("x", 0.0).toFloat(),
                    t.optDouble("y", 0.0).toFloat(),
                )
            }
        }
    }

    @Synchronized
    private fun save() {
        runCatching {
            val o = JSONObject()
            map.forEach { (k, t) ->
                o.put(k, JSONObject().apply { put("s", t.scale); put("x", t.offX); put("y", t.offY) })
            }
            file.writeText(o.toString())
        }
    }

    @Synchronized fun get(name: String): PhotoTransform = map[name] ?: PhotoTransform.NONE

    @Synchronized fun set(name: String, t: PhotoTransform) {
        if (t.isIdentity()) map.remove(name) else map[name] = t
        save()
    }

    @Synchronized fun remove(name: String) { if (map.remove(name) != null) save() }
}
