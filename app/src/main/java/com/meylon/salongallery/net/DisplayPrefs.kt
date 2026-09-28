package com.meylon.salongallery.net

import android.content.Context
import java.util.Calendar

/**
 * Display-local settings kept in SharedPreferences (read synchronously on server &
 * UI threads): a custom device name, an optional PIN that gates the on-screen
 * settings, and an auto-sleep schedule that blanks the frame during quiet hours.
 */
class DisplayPrefs(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences("salon_display", Context.MODE_PRIVATE)

    var customName: String
        get() = sp.getString("name", "") ?: ""
        set(v) = sp.edit().putString("name", v.trim()).apply()

    /** Empty = no PIN. */
    var pin: String
        get() = sp.getString("pin", "") ?: ""
        set(v) = sp.edit().putString("pin", v.trim()).apply()

    val hasPin: Boolean get() = pin.isNotEmpty()

    var scheduleEnabled: Boolean
        get() = sp.getBoolean("sched", false)
        set(v) = sp.edit().putBoolean("sched", v).apply()

    /** Minutes-of-day the screen goes to sleep (default 23:00). */
    var sleepStartMin: Int
        get() = sp.getInt("sleepStart", 23 * 60)
        set(v) = sp.edit().putInt("sleepStart", v).apply()

    /** Minutes-of-day the screen wakes (default 07:00). */
    var sleepEndMin: Int
        get() = sp.getInt("sleepEnd", 7 * 60)
        set(v) = sp.edit().putInt("sleepEnd", v).apply()

    /** True if, right now, the schedule says the frame should be asleep. */
    fun isSleepingNow(nowMinOfDay: Int = currentMinOfDay()): Boolean {
        if (!scheduleEnabled) return false
        val start = sleepStartMin
        val end = sleepEndMin
        if (start == end) return false
        return if (start < end) nowMinOfDay in start until end   // same-day window
        else nowMinOfDay >= start || nowMinOfDay < end            // overnight window
    }

    companion object {
        fun currentMinOfDay(): Int {
            val c = Calendar.getInstance()
            return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
        }
        fun fmt(min: Int): String = "%02d:%02d".format((min / 60) % 24, min % 60)
    }
}
