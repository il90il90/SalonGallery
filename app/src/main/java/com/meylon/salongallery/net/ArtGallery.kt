package com.meylon.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** One public-domain artwork from the Art Institute of Chicago. */
data class ArtPiece(
    val title: String,
    val artist: String,
    val thumbUrl: String,
    val fullUrl: String,
)

/**
 * Browses free, public-domain fine art from the Art Institute of Chicago open API
 * (no API key required). Images are served over IIIF and need a browser User-Agent
 * + Referer to pass Cloudflare — see [BROWSE_UA] / [REFERER].
 */
object ArtGallery {

    const val BROWSE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"
    const val REFERER = "https://www.artic.edu/"
    private const val IIIF = "https://www.artic.edu/iiif/2"

    /** Curated starter categories shown as chips in the Remote. */
    val categories = listOf("Landscape", "Portrait", "Impressionism", "Nature", "Still life", "Cityscape", "Abstract", "Japanese")

    suspend fun search(query: String, limit: Int = 40): List<ArtPiece> = withContext(Dispatchers.IO) {
        val q = URLEncoder.encode(query.ifBlank { "landscape" }, "UTF-8")
        val url = "https://api.artic.edu/api/v1/artworks/search?q=$q" +
            "&query%5Bterm%5D%5Bis_public_domain%5D=true" +
            "&fields=id,title,image_id,artist_title&limit=$limit"
        try {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"; connectTimeout = 10000; readTimeout = 15000
                setRequestProperty("User-Agent", BROWSE_UA)
                setRequestProperty("AIC-User-Agent", "SalonGallery (israel@m-eylon.com)")
            }
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext emptyList() }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val data = JSONObject(body).optJSONArray("data") ?: return@withContext emptyList()
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
        } catch (e: Exception) { emptyList() }
    }
}
