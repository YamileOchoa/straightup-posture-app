package com.proyecto.straightupapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "posture_settings")

data class PostureSettings(
    val vibrationIntensity: Int = 100,        // 0-100
    val vibrateOnDevice: Boolean = true,      // true = ESP32, false = Phone
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val postureGoalHours: Int = 8
)

class SettingsRepository(private val context: Context) {

    companion object {
        private val VIBRATION_INTENSITY = intPreferencesKey("vibration_intensity")
        private val VIBRATE_ON_DEVICE = booleanPreferencesKey("vibrate_on_device")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val POSTURE_GOAL_HOURS = intPreferencesKey("posture_goal_hours")
    }

    val settingsFlow: Flow<PostureSettings> = context.dataStore.data.map { preferences ->
        PostureSettings(
            vibrationIntensity = preferences[VIBRATION_INTENSITY] ?: 100,
            vibrateOnDevice = preferences[VIBRATE_ON_DEVICE] ?: true,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            postureGoalHours = preferences[POSTURE_GOAL_HOURS] ?: 8
        )
    }

    suspend fun updateVibrationIntensity(intensity: Int) {
        context.dataStore.edit { it[VIBRATION_INTENSITY] = intensity }
    }

    suspend fun updateVibrateOnDevice(onDevice: Boolean) {
        context.dataStore.edit { it[VIBRATE_ON_DEVICE] = onDevice }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun updatePostureGoalHours(hours: Int) {
        context.dataStore.edit { it[POSTURE_GOAL_HOURS] = hours }
    }
}