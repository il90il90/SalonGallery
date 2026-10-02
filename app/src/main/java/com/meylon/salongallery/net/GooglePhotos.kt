package com.meylon.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Imports photos from a Google Photos **shared-album link** without any API / sign-in: it fetches
 * the public share page and scrapes the image URLs embedded in it. The user creates a shared album
 * in Google Photos, copies its link (photos.app.goo.gl/… or photos.google.com/share/…), and pastes
 * it here. Each image is then handed to the Display to download like any other photo URL.
 */
object GooglePhotos {
    private const val UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"

    // Shared-album photos are embedded as "https://lh3.googleusercontent.com/pw/<id>" (the /pw/
    // prefix distinguishes real album media from avatars/UI icons like /a/ and /ogw/).
    private val PHOTO = Regex("\"(https://lh3\\.googleusercontent\\.com/pw/[A-Za-z0-9_\\-]+)\"")

    fun looksLikeShareLink(s: String): Boolean {
        val t = s.trim()
        return t.contains("photos.app.goo.gl", true) || t.contains("photos.google.com/share", true)
    }

    /**
     * Returns display-ready image URLs from the shared album, newest-first as the page lists them,
     * or null on a network error, or an empty list if the link has no visible photos (private/wrong).
     */
    suspend fun fetchSharedAlbum(link: String, maxLongEdge: Int = 1920): List<String>? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL(link.trim()).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 20000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", UA)
                setRequestProperty("Accept-Language", "en-US,en;q=0.9")
            }
            if (conn.responseCode !in 200..299) { conn.disconnect(); return@withContext null }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            // De-duplicate (each photo appears several times in the page data), keep order, and
            // request a sized copy (=w..-h..) so the Display downloads a reasonable file, not the original.
            val out = LinkedHashSet<String>()
            PHOTO.findAll(body).forEach { out.add(it.groupValues[1] + "=w$maxLongEdge-h$maxLongEdge-no") }
            out.toList()
        } catch (e: Exception) {
            null
        }
    }
}
