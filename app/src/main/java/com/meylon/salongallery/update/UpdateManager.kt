package com.meylon.salongallery.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.meylon.salongallery.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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

    private val latestReleaseUrl: String
        get() = "https://api.github.com/repos/${BuildConfig.GITHUB_OWNER}/" +
            "${BuildConfig.GITHUB_REPO}/releases/latest"

    suspend fun checkForUpdate(): UpdateStatus = withContext(Dispatchers.IO) {
        try {
            val json = httpGet(latestReleaseUrl) ?: return@withContext UpdateStatus.Error("no response")
            val obj = JSONObject(json)
            val tag = obj.optString("tag_name").trim()
            val notes = obj.optString("body").trim()
            if (tag.isEmpty()) return@withContext UpdateStatus.UpToDate(currentVersion)

            val apkUrl = findApkAsset(obj)
            val latest = normalizeVersion(tag)
            if (apkUrl != null && isNewer(latest, currentVersion)) {
                UpdateStatus.Available(currentVersion, latest, apkUrl, notes)
            } else {
                UpdateStatus.UpToDate(currentVersion)
            }
        } catch (e: Exception) {
            UpdateStatus.Error(e.message ?: "unknown error")
        }
    }

    /** Downloads the APK to app-private storage and launches the system installer. */
    suspend fun downloadAndInstall(context: Context, apkUrl: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(context.getExternalFilesDir(null), "updates").apply { mkdirs() }
                val apk = File(dir, "salongallery-update.apk")
                if (apk.exists()) apk.delete()

                (URL(apkUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("User-Agent", "SalonGallery-Updater")
                }.inputStream.use { input ->
                    apk.outputStream().use { output -> input.copyTo(output) }
                }

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

    private fun findApkAsset(release: JSONObject): String? {
        val assets = release.optJSONArray("assets") ?: return null
        for (i in 0 until assets.length()) {
            val asset = assets.optJSONObject(i) ?: continue
            val name = asset.optString("name")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return asset.optString("browser_download_url").ifEmpty { null }
            }
        }
        return null
    }

    private fun httpGet(urlStr: String): String? {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "SalonGallery-Updater")
        }
        return try {
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else null
        } finally {
            conn.disconnect()
        }
    }

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
