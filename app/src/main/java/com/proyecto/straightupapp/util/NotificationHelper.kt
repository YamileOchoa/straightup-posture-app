package com.proyecto.straightupapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.proyecto.straightupapp.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "posture_monitor_channel"
        private const val ALERT_NOTIFICATION_ID = 1
        private const val MONITORING_NOTIFICATION_ID = 2
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Monitor de Postura"
            val descriptionText = "Notificaciones sobre tu postura"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPostureAlert() {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Mala Postura Detectada")
            .setContentText("Por favor, endereza tu espalda")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(ALERT_NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Permiso de notificación no concedido
        }
    }

    fun showMonitoringNotification() {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Monitoreando Postura")
            .setContentText("StraightUp está activo")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(MONITORING_NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Permiso de notificación no concedido
        }
    }

    fun cancelMonitoringNotification() {
        try {
            with(NotificationManagerCompat.from(context)) {
                cancel(MONITORING_NOTIFICATION_ID)
            }
        } catch (e: SecurityException) {
            // Permiso de notificación no concedido
        }
    }
}
