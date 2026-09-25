package com.meylon.salongallery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
}
