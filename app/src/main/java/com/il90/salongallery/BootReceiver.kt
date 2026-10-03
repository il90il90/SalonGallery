package com.il90.salongallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.il90.salongallery.data.DeviceRole
import com.il90.salongallery.data.RolePreferences

/**
 * Relaunches the app after the device powers back on — but ONLY when this device is set up as the
 * SCREEN and the owner opted in (auto-start on boot). A salon display should come back by itself
 * after a power cut; a phone acting as the Remote should not.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            action != "android.intent.action.QUICKBOOT_POWERON" &&
            action != "com.htc.intent.action.QUICKBOOT_POWERON"
        ) return

        val prefs = RolePreferences(context.applicationContext)
        val shouldStart = runCatching {
            prefs.roleBlocking() == DeviceRole.SCREEN && prefs.autoStartOnBootBlocking()
        }.getOrDefault(false)
        if (!shouldStart) return

        runCatching {
            val launch = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launch)
        }
    }
}
