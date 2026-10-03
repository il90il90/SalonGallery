package com.il90.salongallery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.il90.salongallery.net.PhotoSender
import com.il90.salongallery.upload.UploadManager
import com.il90.salongallery.upload.UploadQuality
import com.il90.salongallery.upload.optimizePhotoForUpload
import com.il90.salongallery.upload.uriLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Foreground service that drains [UploadManager]'s queue, so a transfer of many photos keeps running
 * to completion even after the phone's screen turns off or the app is closed/backgrounded. It shows a
 * progress notification (required for a foreground service, and useful: the owner sees how far the
 * upload got). It stops itself the moment the queue is empty.
 */
class UploadService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var drain: Job? = null
    private var lastActivatedAlbum: String? = null
    private var sentThisRun = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Must post the foreground notification promptly after being started.
        startForeground(NOTIF_ID, buildNotification(UploadManager.progress.value.done, UploadManager.progress.value.total))
        // One drain loop at a time; if items were added while it runs, the shared queue picks them up.
        if (drain?.isActive != true) {
            drain = scope.launch {
                UploadManager.onStarted()
                try {
                    while (true) {
                        val unit = UploadManager.poll() ?: break
                        runCatching { uploadOne(unit) }
                        UploadManager.onOneDone()
                        sentThisRun++
                        val p = UploadManager.progress.value
                        notify(buildNotification(p.done, p.total))
                    }
                } finally {
                    UploadManager.onFinished()
                    runCatching { stopForeground(Service.STOP_FOREGROUND_REMOVE) }
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun uploadOne(u: UploadManager.Item) {
        val uri = Uri.parse(u.uri)
        val cr = contentResolver
        // Activate the destination album once (older Displays ignore ?album=).
        if (u.album != null && u.album != lastActivatedAlbum) {
            PhotoSender.setActiveAlbum(u.host, u.port, u.album)
            lastActivatedAlbum = u.album
        }
        if (u.isVideo) {
            val len = uriLength(cr, uri)
            if (len > 0) {
                PhotoSender.sendVideoStream(u.host, u.port, len, u.album) { cr.openInputStream(uri) }
            } else {
                val vb = runCatching { cr.openInputStream(uri)?.use { it.readBytes() } }.getOrNull()
                if (vb != null) PhotoSender.sendVideo(u.host, u.port, vb, u.album)
            }
        } else {
            val raw = runCatching { cr.openInputStream(uri)?.use { it.readBytes() } }.getOrNull() ?: return
            // Signature of the ORIGINAL file, so the Display can reject the same source photo even when
            // it was sent before at a different quality (which changes the bytes it finally stores).
            val srcSig = md5Hex(raw)
            val b = if (u.quality == UploadQuality.OPTIMIZED) optimizePhotoForUpload(raw) else raw
            PhotoSender.sendPhoto(u.host, u.port, b, u.album, srcSig)
        }
    }

    private fun md5Hex(bytes: ByteArray): String? = runCatching {
        java.security.MessageDigest.getInstance("MD5").digest(bytes).joinToString("") { "%02x".format(it) }
    }.getOrNull()

    private fun buildNotification(done: Int, total: Int): android.app.Notification {
        ensureChannel()
        val open = android.app.PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        val text = if (total > 0) getString(R.string.upload_notif_progress, done, total)
        else getString(R.string.upload_notif_working)
        val b = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_frame)
            .setContentTitle(getString(R.string.upload_notif_title))
            .setContentText(text)
            .setOngoing(true)
            .setContentIntent(open)
            .setOnlyAlertOnce(true)
        if (total > 0) b.setProgress(total, done, false) else b.setProgress(0, 0, true)
        return b.build()
    }

    private fun notify(n: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= 33 &&
            androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        runCatching { (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIF_ID, n) }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(NotificationManager::class.java)
            if (mgr.getNotificationChannel(CHANNEL) == null) {
                mgr.createNotificationChannel(
                    NotificationChannel(CHANNEL, getString(R.string.upload_notif_channel), NotificationManager.IMPORTANCE_LOW),
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { drain?.cancel() }
    }

    companion object {
        private const val CHANNEL = "uploads"
        private const val NOTIF_ID = 4301
    }
}
