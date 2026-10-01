package com.meylon.salongallery.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.meylon.salongallery.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Result of checking GitHub Releases for a newer build. */
sealed interface UpdateStatus {
    data class UpToDate(val current: String) : UpdateStatus
    data class Available(
        val current: String,
        val latest: String,
        val apkUrl: String,
        val notes: String,
    ) : UpdateStatus

    data class Error(val message: String) : UpdateStatus
}

/**
 * Self-update against GitHub Releases (used while the app is distributed outside
 * Google Play). When the app later ships on Play, this is replaced by the
 * Play In-App Update API.
 */
object UpdateManager {

    private const val TIMEOUT_MS = 10_000

    val currentVersion: String get() = BuildConfig.VERSION_NAME

    private val owner get() = BuildConfig.GITHUB_OWNER
    private val repo get() = BuildConfig.GITHUB_REPO

    // github.com Atom feed — served without the api.github.com hourly rate limit.
    private val atomUrl: String get() = "https://github.com/$owner/$repo/releases.atom"

    @Volatile private var lastCheckMs = 0L

    /**
     * Checks for a newer release via the rate-limit-free Atom feed. When [force] is
     * false, a network call is skipped if we checked within the last 10 minutes.
     */
    suspend fun checkForUpdate(force: Boolean = true): UpdateStatus = withContext(Dispatchers.IO) {
        if (!force && System.currentTimeMillis() - lastCheckMs < 10 * 60_000L) {
            return@withContext UpdateStatus.UpToDate(currentVersion)
        }
        try {
            val xml = httpGet(atomUrl) ?: return@withContext UpdateStatus.Error("no response")
            lastCheckMs = System.currentTimeMillis()
            // First <entry> is the newest release; grab its tag from the release URL.
            val tag = Regex("/releases/tag/([^\"<]+)").find(xml)?.groupValues?.get(1)?.trim()
                ?: return@withContext UpdateStatus.UpToDate(currentVersion)
            // "What's new": the first entry's release body, else its title.
            val entry = Regex("<entry>[\\s\\S]*?</entry>").find(xml)?.value ?: xml
            val title = Regex("<title>([^<]+)</title>").find(entry)?.groupValues?.get(1)?.trim().orEmpty()
            val bodyHtml = Regex("<content[^>]*>([\\s\\S]*?)</content>").find(entry)?.groupValues?.get(1).orEmpty()
            val notes = cleanNotes(bodyHtml).ifBlank { title }
            val latest = normalizeVersion(tag)
            // Asset name follows a fixed convention we control.
            val apkUrl = "https://github.com/$owner/$repo/releases/download/$tag/SalonGallery-$latest.apk"
            if (isNewer(latest, currentVersion)) {
                UpdateStatus.Available(currentVersion, latest, apkUrl, notes)
            } else {
                UpdateStatus.UpToDate(currentVersion)
            }
        } catch (e: Exception) {
            UpdateStatus.Error("${e.javaClass.simpleName}: ${e.message}")
        }
    }

    /**
     * Candidate asset URLs for a release, tolerant of the "v" in the filename
     * (we've shipped both `SalonGallery-1.2.3.apk` and `SalonGallery-v1.2.3.apk`).
     */
    private fun candidateUrls(apkUrl: String): List<String> {
        val slash = apkUrl.lastIndexOf('/')
        if (slash < 0) return listOf(apkUrl)
        val base = apkUrl.substring(0, slash + 1)
        val file = apkUrl.substring(slash + 1)
        val set = LinkedHashSet<String>()
        set.add(apkUrl)
        when {
            file.startsWith("SalonGallery-v") -> set.add(base + "SalonGallery-" + file.removePrefix("SalonGallery-v"))
            file.startsWith("SalonGallery-") -> set.add(base + "SalonGallery-v" + file.removePrefix("SalonGallery-"))
        }
        set.add(base + "app-release.apk")
        return set.toList()
    }

    /** Downloads the APK to app-private storage and launches the system installer. */
    suspend fun downloadAndInstall(context: Context, apkUrl: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(context.getExternalFilesDir(null), "updates").apply { mkdirs() }
                val apk = File(dir, "salongallery-update.apk")
                if (apk.exists()) apk.delete()

                // Try each candidate until one actually returns a file (handles the
                // asset-naming mismatch so a tap on "Update" never fails silently).
                var ok = false
                var lastCode = -1
                for (url in candidateUrls(apkUrl)) {
                    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = TIMEOUT_MS
                        readTimeout = TIMEOUT_MS
                        setRequestProperty("User-Agent", "SalonGallery-Updater")
                    }
                    lastCode = try { conn.responseCode } catch (e: Exception) { -1 }
                    if (lastCode in 200..299) {
                        conn.inputStream.use { input -> apk.outputStream().use { output -> input.copyTo(output) } }
                        conn.disconnect()
                        ok = apk.length() > 0
                        if (ok) break
                    } else {
                        conn.disconnect()
                    }
                }
                if (!ok) return@withContext Result.failure(Exception("download failed (HTTP $lastCode)"))

                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", apk
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun httpGet(urlStr: String): String? {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            instanceFollowRedirects = true
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "SalonGallery-Updater")
        }
        return try {
            val code = conn.responseCode
            if (code in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() }
                android.util.Log.w("SalonUpdate", "HTTP $code from $urlStr: ${err?.take(200)}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.w("SalonUpdate", "httpGet failed: ${e.javaClass.simpleName}: ${e.message}")
            throw e
        } finally {
            conn.disconnect()
        }
    }

    /** Turns the release-body HTML from the Atom feed into a few readable lines. */
    private fun cleanNotes(html: String): String = html
        .replace(Regex("<li[^>]*>", RegexOption.IGNORE_CASE), "• ")
        .replace(Regex("</(p|li|h\\d|ul|ol|div)>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]+>"), " ")
        .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
        .replace("&quot;", "\"").replace("&#39;", "'").replace("&apos;", "'")
        .replace(Regex("[ \\t]+"), " ")
        .replace(Regex("\\n{2,}"), "\n")
        .lines().map { it.trim() }.filter { it.isNotBlank() }
        .joinToString("\n").take(600).trim()

    /** "v1.2.3" -> "1.2.3" */
    private fun normalizeVersion(raw: String): String =
        raw.trim().removePrefix("v").removePrefix("V").trim()

    /** Returns true when [latest] is a strictly higher semantic version than [current]. */
    private fun isNewer(latest: String, current: String): Boolean {
        val a = latest.split(".", "-").mapNotNull { it.toIntOrNull() }
        val b = normalizeVersion(current).split(".", "-").mapNotNull { it.toIntOrNull() }
        val n = maxOf(a.size, b.size)
        for (i in 0 until n) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }
}
