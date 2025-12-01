// ═══════════════════════════════════════════════════════════════════
// 📁 ui/components/devicecontrol/MonitoringControlCard.kt - CON LOGS
// ═══════════════════════════════════════════════════════════════════
package com.proyecto.straightupapp.ui.components.devicecontrol

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private const val TAG = "MonitoringControlCard"

@Composable
fun MonitoringControlCard(
    isMonitoring: Boolean,
    onStartMonitoring: () -> Unit,
    onStopMonitoring: () -> Unit
) {
    Log.d(TAG, "🎨 MonitoringControlCard recompuesta - isMonitoring: $isMonitoring")

    val cardColor by animateColorAsState(
        targetValue = if (isMonitoring)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "cardColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Control de Monitoreo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMonitoring)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                ) {
                    Text(
                        text = if (isMonitoring) "ACTIVO" else "INACTIVO",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isMonitoring)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.surface
                    )
                }
            }

            Divider()

            // Descripción del estado
            Text(
                text = if (isMonitoring)
                    "El dispositivo está monitoreando tu postura activamente. El motor vibrará cuando detecte mala postura."
                else
                    "El dispositivo está conectado pero en modo espera. Presiona ENCENDER para iniciar el monitoreo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Botón principal (cambia según estado)
            if (!isMonitoring) {
                Button(
                    onClick = {
                        Log.d(TAG, "🔘 BOTÓN ENCENDER PRESIONADO")
                        onStartMonitoring()
                        Log.d(TAG, "✅ onStartMonitoring() ejecutado")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENCENDER MONITOREO", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        Log.d(TAG, "🔘 BOTÓN APAGAR PRESIONADO")
                        onStopMonitoring()
                        Log.d(TAG, "✅ onStopMonitoring() ejecutado")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APAGAR MONITOREO", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}