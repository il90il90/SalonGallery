package com.meylon.salongallery.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** One headline pulled from an RSS/Atom feed. */
data class RssItem(val title: String, val source: String)

/** Fetches and loosely parses RSS/Atom feeds into a flat list of headlines. */
object RssFeed {

    private val ITEM = Regex("<(item|entry)\\b[\\s\\S]*?</\\1>", RegexOption.IGNORE_CASE)
    private val TITLE = Regex("<title\\b[^>]*>([\\s\\S]*?)</title>", RegexOption.IGNORE_CASE)

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
                        // The feed's own <title> (the first one) is the source label.
                        val source = TITLE.find(xml)?.groupValues?.get(1)?.let { clean(it) }?.take(40) ?: "News"
                        var n = 0
                        for (m in ITEM.findAll(xml)) {
                            if (n >= perFeed) break
                            val title = TITLE.find(m.value)?.groupValues?.get(1)?.let { clean(it) } ?: continue
                            if (title.isNotBlank()) { add(RssItem(title, source)); n++ }
                        }
                    } else conn.disconnect()
                }
            }
        }
    }

    private fun clean(raw: String): String = raw
        .replace(Regex("<!\\[CDATA\\[", RegexOption.IGNORE_CASE), "")
        .replace("]]>", "")
        .replace(Regex("<[^>]+>"), "")
        .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
        .replace("&quot;", "\"").replace("&#39;", "'").replace("&apos;", "'")
        .replace("&#8217;", "’").replace("&#8216;", "‘")
        .replace(Regex("\\s+"), " ")
        .trim()
}
