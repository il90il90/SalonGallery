package com.meylon.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** One public-domain / freely usable image from a browse source. */
data class ArtPiece(
    val title: String,
    val artist: String,
    val thumbUrl: String,
    val fullUrl: String,
)

/** A free, key-less image collection the Remote can browse and send to the wall. */
enum class ArtSource(val label: String) {
    ARTIC("Fine art"),
    MET("Museum"),
    PHOTOS("Photography"),
    NASA("Space"),
    OPENVERSE("Open"),
}

/**
 * Browses free images from several key-less sources: the Art Institute of Chicago and
 * the Met Museum open APIs (public-domain fine art) and Lorem Picsum (curated photography).
 * IIIF art images need a browser User-Agent + Referer to pass Cloudflare ([BROWSE_UA]/[REFERER]).
 */
object ArtGallery {

    const val BROWSE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"
    const val REFERER = "https://www.artic.edu/"
    private const val IIIF = "https://www.artic.edu/iiif/2"

    private val artCategories = listOf("Landscape", "Portrait", "Impressionism", "Nature", "Still life", "Cityscape", "Abstract", "Japanese")
    private val photoCategories = listOf("Nature", "City", "Mountains", "Ocean", "Minimal")
    private val spaceCategories = listOf("Galaxy", "Nebula", "Earth", "Mars", "Moon", "Aurora", "Jupiter", "Saturn")
    private val openCategories = listOf("Nature", "Sunset", "Flowers", "Forest", "Ocean", "Architecture", "Street", "Minimal")

    fun categoriesFor(source: ArtSource): List<String> = when (source) {
        ArtSource.PHOTOS -> photoCategories
        ArtSource.NASA -> spaceCategories
        ArtSource.OPENVERSE -> openCategories
        else -> artCategories
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    /** Returns matching images, an empty list for no results, or null on a network error. */
    suspend fun search(source: ArtSource, query: String, limit: Int = 40): List<ArtPiece>? = when (source) {
        ArtSource.ARTIC -> searchArtic(query, limit)
        ArtSource.MET -> searchMet(query, limit.coerceAtMost(18))
        ArtSource.PHOTOS -> searchPhotos(limit.coerceAtMost(30))
        ArtSource.NASA -> searchNasa(query, limit)
        ArtSource.OPENVERSE -> searchOpenverse(query, limit)
    }

    private fun openGet(url: String, artHeaders: Boolean = false): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"; connectTimeout = 10000; readTimeout = 15000
            setRequestProperty("User-Agent", BROWSE_UA)
            if (artHeaders) setRequestProperty("AIC-User-Agent", "SalonGallery (israel@m-eylon.com)")
        }

    private suspend fun searchArtic(query: String, limit: Int): List<ArtPiece>? = withContext(Dispatchers.IO) {
        val q = enc(query.ifBlank { "landscape" })
        val url = "https://api.artic.edu/api/v1/artworks/search?q=$q" +
            "&query%5Bterm%5D%5Bis_public_domain%5D=true" +
            "&fields=id,title,image_id,artist_title&limit=$limit"
        try {
            val conn = openGet(url, artHeaders = true)
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val data = JSONObject(body).optJSONArray("data") ?: return@withContext emptyList<ArtPiece>()
            buildList {
                for (i in 0 until data.length()) {
                    val o = data.getJSONObject(i)
                    val img = o.optString("image_id", "")
                    if (img.isBlank() || img == "null") continue
                    add(
                        ArtPiece(
                            title = o.optString("title", "Untitled"),
                            artist = o.optString("artist_title", "").ifBlank { "Unknown artist" },
                            thumbUrl = "$IIIF/$img/full/400,/0/default.jpg",
                            fullUrl = "$IIIF/$img/full/1686,/0/default.jpg",
                        )
                    )
                }
            }
        } catch (e: Exception) { null }
    }

    // "Museum" = the Cleveland Museum of Art open-access API (key-free AND searchable;
    // the Met's open search endpoint was retired / returns 410).
    private suspend fun searchMet(query: String, limit: Int): List<ArtPiece>? = withContext(Dispatchers.IO) {
        val q = enc(query.ifBlank { "landscape" })
        val url = "https://openaccess-api.clevelandart.org/api/artworks/?q=$q&has_image=1&limit=$limit"
        try {
            val conn = openGet(url)
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val data = JSONObject(body).optJSONArray("data") ?: return@withContext emptyList<ArtPiece>()
            buildList {
                for (i in 0 until data.length()) {
                    val o = data.getJSONObject(i)
                    val images = o.optJSONObject("images") ?: continue
                    val web = images.optJSONObject("web")?.optString("url").orEmpty()
                    if (web.isBlank()) continue
                    val print = images.optJSONObject("print")?.optString("url").orEmpty()
                    val creators = o.optJSONArray("creators")
                    val artist = if (creators != null && creators.length() > 0)
                        creators.getJSONObject(0).optString("description", "") else ""
                    add(
                        ArtPiece(
                            title = o.optString("title", "Untitled").ifBlank { "Untitled" },
                            artist = artist.ifBlank { "Unknown artist" },
                            thumbUrl = web,
                            fullUrl = print.ifBlank { web },
                        )
                    )
                }
            }
        } catch (e: Exception) { null }
    }

    private suspend fun searchNasa(query: String, limit: Int): List<ArtPiece>? = withContext(Dispatchers.IO) {
        val q = enc(query.ifBlank { "galaxy" })
        try {
            val conn = openGet("https://images-api.nasa.gov/search?q=$q&media_type=image")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val items = JSONObject(body).optJSONObject("collection")?.optJSONArray("items") ?: return@withContext emptyList<ArtPiece>()
            buildList {
                var i = 0
                while (i < items.length() && size < limit) {
                    val it = items.getJSONObject(i); i++
                    val thumb = it.optJSONArray("links")?.optJSONObject(0)?.optString("href").orEmpty()
                    if (thumb.isBlank()) continue
                    val title = it.optJSONArray("data")?.optJSONObject(0)?.optString("title", "Untitled") ?: "Untitled"
                    val full = thumb.replace(Regex("~(thumb|small|medium|orig)\\.jpg$"), "~large.jpg")
                    add(ArtPiece(title.ifBlank { "Untitled" }, "NASA", thumb, full))
                }
            }
        } catch (e: Exception) { null }
    }

    private suspend fun searchOpenverse(query: String, limit: Int): List<ArtPiece>? = withContext(Dispatchers.IO) {
        val q = enc(query.ifBlank { "nature" })
        try {
            val conn = openGet("https://api.openverse.org/v1/images/?q=$q&page_size=$limit")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val res = JSONObject(body).optJSONArray("results") ?: return@withContext emptyList<ArtPiece>()
            buildList {
                for (i in 0 until res.length()) {
                    val o = res.getJSONObject(i)
                    val thumb = o.optString("thumbnail", "")
                    val full = o.optString("url", "")
                    if (thumb.isBlank() && full.isBlank()) continue
                    add(
                        ArtPiece(
                            title = o.optString("title", "Untitled").ifBlank { "Untitled" },
                            artist = o.optString("creator", "").ifBlank { "Open" },
                            thumbUrl = thumb.ifBlank { full },
                            fullUrl = full.ifBlank { thumb },
                        )
                    )
                }
            }
        } catch (e: Exception) { null }
    }

    private suspend fun searchPhotos(limit: Int): List<ArtPiece>? = withContext(Dispatchers.IO) {
        val page = (1..10).random()
        try {
            val conn = openGet("https://picsum.photos/v2/list?page=$page&limit=$limit")
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val arr = org.json.JSONArray(body)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val id = o.optString("id", "")
                    if (id.isBlank()) continue
                    add(
                        ArtPiece(
                            title = "Photograph",
                            artist = o.optString("author", "").ifBlank { "Photographer" },
                            thumbUrl = "https://picsum.photos/id/$id/400/300",
                            fullUrl = "https://picsum.photos/id/$id/1600/1000",
                        )
                    )
                }
            }
        } catch (e: Exception) { null }
    }
}
