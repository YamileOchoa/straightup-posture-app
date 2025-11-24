// ═══════════════════════════════════════════════════════════════════
// 📁 viewmodel/MainViewModel.kt
// ═══════════════════════════════════════════════════════════════════
package com.proyecto.straightupapp.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.straightupapp.bluetooth.BleManager
import com.proyecto.straightupapp.data.*
import com.proyecto.straightupapp.repository.PostureRepository
import com.proyecto.straightupapp.ui.components.PostureState
import com.proyecto.straightupapp.util.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostureRepository(application)
    val bleManager = BleManager(application)
    private val notificationHelper = NotificationHelper(application)
    private val settingsRepository = SettingsRepository(application)

    // Settings
    val settings = settingsRepository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PostureSettings()
    )

    // Estado de la aplicación
    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring

    private val _postureState = MutableStateFlow(PostureState.WAITING)
    val postureState: StateFlow<PostureState> = _postureState

    private val _todayStats = MutableStateFlow(DailyStats())
    val todayStats: StateFlow<DailyStats> = _todayStats

    private val _weeklyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyStats>> = _weeklyStats

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        setupBleCallbacks()
        loadTodayStats()
        loadWeeklyStats()
        updateAchievements()

        // Actualizar estado del avatar según conexión
        viewModelScope.launch {
            bleManager.isConnected.collect { connected ->
                if (!connected) {
                    _postureState.value = PostureState.WAITING
                } else if (_postureState.value == PostureState.WAITING) {
                    _postureState.value = PostureState.GOOD
                }
            }
        }
    }

    private fun setupBleCallbacks() {
        bleManager.onAlert = {
            handleBadPosture()
        }

        bleManager.onOk = {
            handleGoodPosture()
        }
    }

    private fun handleBadPosture() {
        _postureState.value = PostureState.BAD

        viewModelScope.launch {
            val event = PostureEvent(
                timestamp = Date(),
                eventType = "BAD_POSTURE",
                duration = 0
            )
            repository.insertPostureEvent(event)

            _todayStats.value = _todayStats.value.copy(
                badPostureCount = _todayStats.value.badPostureCount + 1
            )

            val currentSettings = settings.value

            // Vibración y notificación según configuración
            if (currentSettings.vibrateOnDevice) {
                // Enviar intensidad al ESP32
                val intensity = currentSettings.vibrationIntensity
                bleManager.writeString("VIBRATE:$intensity")
            } else {
                // Vibrar el teléfono
                vibratePhone(currentSettings.vibrationIntensity)
            }

            if (currentSettings.notificationsEnabled) {
                notificationHelper.showPostureAlert()
            }

            loadTodayEvents()
            updateAchievements()
        }
    }

    private fun handleGoodPosture() {
        _postureState.value = PostureState.GOOD

        viewModelScope.launch {
            val event = PostureEvent(
                timestamp = Date(),
                eventType = "GOOD_POSTURE",
                duration = 0
            )
            repository.insertPostureEvent(event)

            _todayStats.value = _todayStats.value.copy(
                goodPostureCount = _todayStats.value.goodPostureCount + 1
            )

            loadTodayEvents()
            updateAchievements()
        }
    }

    private fun vibratePhone(intensity: Int) {
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                val duration = (intensity * 5L).coerceIn(100L, 500L) // 100-500ms según intensidad

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(duration)
                }
            }
        } catch (e: Exception) {
            // Si no hay permiso de vibración, simplemente no vibra
            android.util.Log.w("MainViewModel", "No se pudo vibrar: ${e.message}")
        }
    }

    private fun loadTodayStats() {
        viewModelScope.launch {
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val events = repository.getEventsSince(startOfDay)
            updateStatsFromEvents(events)
        }
    }

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val weekStats = mutableListOf<DailyStats>()

            for (i in 6 downTo 0) {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.DAY_OF_YEAR, -i)

                val startOfDay = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val endOfDay = calendar.time

                val events = repository.getEventsInRange(startOfDay, endOfDay)

                val badCount = events.count { it.eventType == "BAD_POSTURE" }
                val goodCount = events.count { it.eventType == "GOOD_POSTURE" }

                weekStats.add(DailyStats(
                    badPostureCount = badCount,
                    goodPostureCount = goodCount,
                    date = startOfDay
                ))

                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            _weeklyStats.value = weekStats
        }
    }

    private fun updateStatsFromEvents(events: List<PostureEvent>) {
        val badCount = events.count { it.eventType == "BAD_POSTURE" }
        val goodCount = events.count { it.eventType == "GOOD_POSTURE" }

        _todayStats.value = DailyStats(
            badPostureCount = badCount,
            goodPostureCount = goodCount
        )
    }

    private fun loadTodayEvents() {
        viewModelScope.launch {
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val events = repository.getEventsSince(startOfDay)
            updateStatsFromEvents(events)
        }
    }

    private fun updateAchievements() {
        viewModelScope.launch {
            val stats = _todayStats.value
            val newAchievements = mutableListOf<Achievement>()

            // Logro: Primera sesión
            if (stats.goodPostureCount + stats.badPostureCount >= 1) {
                newAchievements.add(Achievement(
                    id = "first_session",
                    title = "Primera Sesión",
                    description = "Completaste tu primera sesión de monitoreo",
                    icon = "🎉",
                    isUnlocked = true
                ))
            }

            // Logro: Postura perfecta
            if (stats.goodPostureCount >= 10 && stats.badPostureCount == 0) {
                newAchievements.add(Achievement(
                    id = "perfect_posture",
                    title = "Postura Perfecta",
                    description = "10 detecciones correctas sin alertas",
                    icon = "⭐",
                    isUnlocked = true
                ))
            }

            // Logro: Guerrero de la postura
            if (stats.goodPostureCount >= 50) {
                newAchievements.add(Achievement(
                    id = "posture_warrior",
                    title = "Guerrero de la Postura",
                    description = "50 posturas correctas en un día",
                    icon = "🏆",
                    isUnlocked = true
                ))
            }

            _achievements.value = newAchievements
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Settings functions - TODAS LAS FUNCIONES NECESARIAS
    // ═══════════════════════════════════════════════════════════════

    fun updateVibrationIntensity(intensity: Int) {
        viewModelScope.launch {
            settingsRepository.updateVibrationIntensity(intensity)
        }
    }

    fun updateVibrateOnDevice(onDevice: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVibrateOnDevice(onDevice)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationsEnabled(enabled)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSoundEnabled(enabled)
        }
    }

    fun updatePostureGoalHours(hours: Int) {
        viewModelScope.launch {
            settingsRepository.updatePostureGoalHours(hours)
        }
    }

    // ═══════════════════════════════════════════════════════════════

    fun startMonitoring() {
        if (bleManager.isConnected.value) {
            _isMonitoring.value = true
            // Enviar comando al ESP32 para activar monitoreo
            bleManager.writeString("START_MONITORING")
            notificationHelper.showMonitoringNotification()
        }
    }

    fun stopMonitoring() {
        _isMonitoring.value = false
        // Enviar comando al ESP32 para desactivar monitoreo
        bleManager.writeString("STOP_MONITORING")
        notificationHelper.cancelMonitoringNotification()
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.disconnect()
        bleManager.stopScan()
    }
}

data class DailyStats(
    val badPostureCount: Int = 0,
    val goodPostureCount: Int = 0,
    val date: Date = Date()
) {
    val postureScore: Int
        get() {
            val total = badPostureCount + goodPostureCount
            return if (total > 0) {
                ((goodPostureCount.toFloat() / total) * 100).toInt()
            } else 100
        }
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false
)