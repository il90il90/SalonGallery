package com.il90.salongallery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.il90.salongallery.net.DiscoveredScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/** The role this device plays. */
enum class DeviceRole { UNSET, SCREEN, REMOTE }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salon_prefs")

class RolePreferences(private val context: Context) {

    private val roleKey = stringPreferencesKey("device_role")

    val role: Flow<DeviceRole> = context.dataStore.data.map { prefs ->
        when (prefs[roleKey]) {
            DeviceRole.SCREEN.name -> DeviceRole.SCREEN
            DeviceRole.REMOTE.name -> DeviceRole.REMOTE
            else -> DeviceRole.UNSET
        }
    }

    suspend fun setRole(role: DeviceRole) {
        context.dataStore.edit { prefs ->
            prefs[roleKey] = role.name
        }
    }

    // Whether a SCREEN device should relaunch itself when the TV box powers back on.
    private val autoStartKey = booleanPreferencesKey("auto_start_on_boot")

    val autoStartOnBoot: Flow<Boolean> = context.dataStore.data.map { it[autoStartKey] ?: false }

    suspend fun setAutoStartOnBoot(enabled: Boolean) {
        context.dataStore.edit { it[autoStartKey] = enabled }
    }

    /** Synchronous reads for the boot receiver (runs with no coroutine scope). */
    fun roleBlocking(): DeviceRole = runBlocking { role.first() }
    fun autoStartOnBootBlocking(): Boolean = runBlocking { autoStartOnBoot.first() }

    // The screen the Remote last controlled, so reopening the app rejoins it instead of making
    // the user pick (or re-type an IP) every time.
    private val lastKeyKey = stringPreferencesKey("last_screen_key")
    private val lastNameKey = stringPreferencesKey("last_screen_name")
    private val lastHostKey = stringPreferencesKey("last_screen_host")
    private val lastPortKey = intPreferencesKey("last_screen_port")

    val lastScreen: Flow<DiscoveredScreen?> = context.dataStore.data.map { prefs ->
        val host = prefs[lastHostKey]
        val port = prefs[lastPortKey]
        if (host.isNullOrBlank() || port == null) null
        else DiscoveredScreen(key = prefs[lastKeyKey] ?: "manual", name = prefs[lastNameKey] ?: host, host = host, port = port)
    }

    /** Pass null to forget (the user explicitly left the screen). */
    suspend fun setLastScreen(screen: DiscoveredScreen?) {
        context.dataStore.edit { prefs ->
            if (screen == null) {
                prefs.remove(lastKeyKey); prefs.remove(lastNameKey); prefs.remove(lastHostKey); prefs.remove(lastPortKey)
            } else {
                prefs[lastKeyKey] = screen.key; prefs[lastNameKey] = screen.name
                prefs[lastHostKey] = screen.host; prefs[lastPortKey] = screen.port
            }
        }
    }
}
