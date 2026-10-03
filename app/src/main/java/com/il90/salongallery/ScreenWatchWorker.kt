package com.il90.salongallery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.il90.salongallery.data.RolePreferences
import com.il90.salongallery.net.PhotoSender
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Periodically checks whether the paired screen is reachable and, when it drops offline, posts a
 * local notification so the owner hears about it remotely instead of discovering it at the salon.
 * Only notifies on an online→offline transition (not every run), and again when it comes back.
 */
class ScreenWatchWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val screen = runCatching { RolePreferences(ctx).lastScreen.first() }.getOrNull() ?: return Result.success()
        val online = PhotoSender.getInfo(screen.host, screen.port, timeoutMs = 4000) != null

        val sp = ctx.getSharedPreferences("salon_remote", Context.MODE_PRIVATE)
        val wasOnline = sp.getBoolean("screen_online", true)
        sp.edit().putBoolean("screen_online", online).apply()

        if (wasOnline && !online) notify(ctx, offline = true, name = screen.name)
        else if (!wasOnline && online) notify(ctx, offline = false, name = screen.name)
        return Result.success()
    }

    private fun notify(ctx: Context, offline: Boolean, name: String) {
        ensureChannel(ctx)
        val title = if (offline) ctx.getString(R.string.notif_screen_offline_title)
        else ctx.getString(R.string.notif_screen_online_title)
        val body = ctx.getString(if (offline) R.string.notif_screen_offline_body else R.string.notif_screen_online_body, name)
        val open = android.content.Intent(ctx, MainActivity::class.java).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = android.app.PendingIntent.getActivity(
            ctx, 0, open,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true).setContentIntent(pi)
            .build()
        // On Android 13+ posting needs the runtime POST_NOTIFICATIONS grant; check before notifying.
        val canPost = android.os.Build.VERSION.SDK_INT < 33 ||
            androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        if (canPost) runCatching { NotificationManagerCompat.from(ctx).notify(NOTIF_ID, n) }
    }

    private fun ensureChannel(ctx: Context) {
        val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (mgr.getNotificationChannel(CHANNEL) == null) {
            mgr.createNotificationChannel(
                NotificationChannel(CHANNEL, ctx.getString(R.string.notif_channel_screen), NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    companion object {
        private const val CHANNEL = "screen_status"
        private const val NOTIF_ID = 4201
        private const val WORK = "screen_watch"

        /** Start (or keep) the periodic screen-health check. Safe to call repeatedly. */
        fun ensureScheduled(context: Context) {
            val req = PeriodicWorkRequestBuilder<ScreenWatchWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context.applicationContext)
                .enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.KEEP, req)
        }
    }
}
