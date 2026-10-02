package com.il90.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** One headline pulled from an RSS/Atom feed. */
data class RssItem(
    val title: String,
    val source: String,
    val summary: String = "",
    val imageUrl: String = "",
)

/** How the news ticker is shown on the wall. */
data class RssConfig(
    val pos: String = "bottom",       // bottom | top
    val showImage: Boolean = false,
    val showSource: Boolean = true,
    val showSummary: Boolean = false,
)

/** Fetches and loosely parses RSS/Atom feeds into a flat list of headlines. */
object RssFeed {

    private val ITEM = Regex("<(item|entry)\\b[\\s\\S]*?</\\1>", RegexOption.IGNORE_CASE)
    private val TITLE = Regex("<title\\b[^>]*>([\\s\\S]*?)</title>", RegexOption.IGNORE_CASE)
    private val DESC = Regex("<(description|summary|content:encoded)\\b[^>]*>([\\s\\S]*?)</\\1>", RegexOption.IGNORE_CASE)
    // Image candidates, in priority order.
    private val MEDIA = Regex("<media:(content|thumbnail)\\b[^>]*\\burl=\"([^\"]+)\"", RegexOption.IGNORE_CASE)
    private val ENCLOSURE = Regex("<enclosure\\b[^>]*\\burl=\"([^\"]+)\"[^>]*type=\"image", RegexOption.IGNORE_CASE)
    private val IMG = Regex("<img\\b[^>]*\\bsrc=\"([^\"]+)\"", RegexOption.IGNORE_CASE)

    suspend fun fetch(feeds: List<String>, perFeed: Int = 8): List<RssItem> = withContext(Dispatchers.IO) {
        buildList {
            feeds.forEach { url ->
                runCatching {
                    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"; connectTimeout = 10000; readTimeout = 15000
                        instanceFollowRedirects = true
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android) SalonGallery")
                    }
                    if (conn.responseCode in 200..299) {
                        val xml = conn.inputStream.bufferedReader().use { it.readText() }
                        conn.disconnect()
                        val source = TITLE.find(xml)?.groupValues?.get(1)?.let { clean(it) }?.take(40) ?: "News"
                        var n = 0
                        for (m in ITEM.findAll(xml)) {
                            if (n >= perFeed) break
                            val block = m.value
                            val title = TITLE.find(block)?.groupValues?.get(1)?.let { clean(it) } ?: continue
                            if (title.isBlank()) continue
                            val rawDesc = DESC.find(block)?.groupValues?.get(2).orEmpty()
                            val summary = clean(rawDesc).take(240)
                            val image = MEDIA.find(block)?.groupValues?.get(2)
                                ?: ENCLOSURE.find(block)?.groupValues?.get(1)
                                ?: IMG.find(rawDesc)?.groupValues?.get(1)
                                ?: IMG.find(block)?.groupValues?.get(1)
                                ?: ""
                            add(RssItem(title, source, summary, image.trim()))
                            n++
                        }
                    } else conn.disconnect()
                }
            }
        }
    }

    private val TAG = Regex("<[^>]+>")

    private fun clean(raw: String): String {
        var s = raw
            .replace(Regex("<!\\[CDATA\\[", RegexOption.IGNORE_CASE), "")
            .replace("]]>", "")
        // Strip real tags, then decode entities (which may reveal entity-encoded tags like
        // &lt;img&gt;), then strip again so that markup never shows up as literal text.
        s = TAG.replace(s, "")
        s = decodeEntities(s)
        s = TAG.replace(s, "")
        return s.replace(Regex("\\s+"), " ").trim()
    }

    private fun decodeEntities(s: String): String = s
        .replace("&lt;", "<").replace("&gt;", ">")
        .replace("&quot;", "\"").replace("&#39;", "'").replace("&apos;", "'")
        .replace("&#8217;", "’").replace("&#8216;", "‘").replace("&nbsp;", " ")
        .replace("&amp;", "&")
}
