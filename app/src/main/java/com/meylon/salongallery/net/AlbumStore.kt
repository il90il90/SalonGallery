package com.meylon.salongallery.net

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Named albums (categories) layered over the flat [LibraryStore]. Each album is an
 * ordered subset of photo names. One album (or the implicit "all") is "active" and
 * drives the slideshow. Persisted as albums.json.
 */
class AlbumStore(private val file: File, private val library: LibraryStore) {

    class Album(val id: String, var name: String, val photos: MutableList<String>)

    private val albums = mutableListOf<Album>()
    var activeId: String = ALL
        private set

    init { load() }

    @Synchronized
    private fun load() {
        albums.clear()
        activeId = ALL
        if (!file.exists()) return
        runCatching {
            val o = JSONObject(file.readText())
            activeId = o.optString("active", ALL)
            val arr = o.optJSONArray("albums") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val a = arr.getJSONObject(i)
                val photos = mutableListOf<String>()
                val p = a.optJSONArray("photos") ?: JSONArray()
                for (j in 0 until p.length()) photos.add(p.getString(j))
                albums.add(Album(a.getString("id"), a.optString("name", "Album"), photos))
            }
            if (activeId != ALL && albums.none { it.id == activeId }) activeId = ALL
        }
    }

    @Synchronized
    private fun save() {
        runCatching {
            val arr = JSONArray()
            albums.forEach { a ->
                arr.put(JSONObject().apply {
                    put("id", a.id); put("name", a.name)
                    put("photos", JSONArray(a.photos))
                })
            }
            file.writeText(JSONObject().apply { put("active", activeId); put("albums", arr) }.toString())
        }
    }

    @Synchronized fun createAlbum(name: String): String {
        val id = "alb_${System.currentTimeMillis()}"
        albums.add(Album(id, name.ifBlank { "Album" }, mutableListOf())); save(); return id
    }

    @Synchronized fun renameAlbum(id: String, name: String) {
        albums.find { it.id == id }?.name = name.ifBlank { "Album" }; save()
    }

    @Synchronized fun deleteAlbum(id: String) {
        albums.removeAll { it.id == id }
        if (activeId == id) activeId = ALL
        save()
    }

    @Synchronized fun setActive(id: String) {
        activeId = if (id == ALL || albums.any { it.id == id }) id else ALL; save()
    }

    @Synchronized fun addToAlbum(id: String, photo: String) {
        albums.find { it.id == id }?.let { if (!it.photos.contains(photo)) it.photos.add(photo) }; save()
    }

    @Synchronized fun removeFromAlbum(id: String, photo: String) {
        albums.find { it.id == id }?.photos?.remove(photo); save()
    }

    @Synchronized fun reorderAlbum(id: String, names: List<String>) {
        val lib = library.names().toSet()
        albums.find { it.id == id }?.let {
            it.photos.clear(); it.photos.addAll(names.filter { n -> lib.contains(n) })
        }; save()
    }

    @Synchronized fun onPhotoDeleted(photo: String) {
        var changed = false
        albums.forEach { if (it.photos.remove(photo)) changed = true }
        if (changed) save()
    }

    @Synchronized fun onPhotosCleared() {
        albums.forEach { it.photos.clear() }; save()
    }

    /** Ordered photo names for the active view (falls back to the whole library). */
    @Synchronized fun activePhotoNames(): List<String> {
        val lib = library.names()
        if (activeId == ALL) return lib
        val a = albums.find { it.id == activeId } ?: return lib
        val set = lib.toSet()
        return a.photos.filter { set.contains(it) }
    }

    @Synchronized fun activeName(): String =
        if (activeId == ALL) "All" else albums.find { it.id == activeId }?.name ?: "All"

    @Synchronized fun isAllActive(): Boolean = activeId == ALL

    /** {"active":id,"activeName":..,"albums":[{"id","name","count"}]} */
    @Synchronized fun albumsJson(): String {
        val arr = JSONArray()
        albums.forEach { a ->
            arr.put(JSONObject().apply { put("id", a.id); put("name", a.name); put("count", a.photos.size) })
        }
        return JSONObject().apply {
            put("active", activeId); put("activeName", activeName()); put("albums", arr)
        }.toString()
    }

    companion object { const val ALL = "all" }
}
