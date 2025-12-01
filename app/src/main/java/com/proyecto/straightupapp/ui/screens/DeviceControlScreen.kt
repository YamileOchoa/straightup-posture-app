// ═══════════════════════════════════════════════════════════════════
// 📁 ui/screens/DeviceControlScreen.kt - CON LOGS DE DEPURACIÓN
// ═══════════════════════════════════════════════════════════════════
package com.proyecto.straightupapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.proyecto.straightupapp.bluetooth.BleManager
import com.proyecto.straightupapp.viewmodel.MainViewModel
import com.proyecto.straightupapp.ui.components.devicecontrol.*

private const val TAG = "DeviceControlScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlScreen(
    bleManager: BleManager,
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    Log.d(TAG, "🎨 DeviceControlScreen recompuesta")

    // 1. Estados Reactivos (Observables)
    val isScanning by bleManager.isScanning.collectAsState()
    val isConnected by bleManager.isConnected.collectAsState()
    val connectedDeviceName by bleManager.connectedDeviceName.collectAsState()
    val lastPayload by bleManager.lastPayload.collectAsState()
    val scanLog by bleManager.scanLog.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()

    // Log de estados actuales
    LaunchedEffect(isConnected, isMonitoring) {
        Log.d(TAG, "📊 Estados: isConnected=$isConnected, isMonitoring=$isMonitoring")
    }

    // 2. Estados Locales de la Pantalla
    var showDebugLog by remember { mutableStateOf(false) }
    var scanMessage by remember { mutableStateOf("") }
    var showPowerDialog by remember { mutableStateOf(false) }
    var powerAction by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Dispositivo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado de conexión
            ConnectionStatusCard(
                isConnected = isConnected,
                deviceName = connectedDeviceName,
                lastPayload = lastPayload
            )

            // ═══════════════════════════════════════════════════════════
            // LÓGICA CONDICIONAL
            // ═══════════════════════════════════════════════════════════

            if (!isConnected) {
                Log.d(TAG, "🔴 Mostrando ControlButtonsSection (NO conectado)")

                ControlButtonsSection(
                    isScanning = isScanning,
                    bleManager = bleManager,
                    onScanResult = { _, message ->
                        scanMessage = message
                        Log.d(TAG, "📝 scanMessage actualizado: $message")
                    }
                )
            } else {
                Log.d(TAG, "🟢 Mostrando MonitoringControlCard y ConnectedControlsCard (SÍ conectado)")

                // Card de control de monitoreo
                MonitoringControlCard(
                    isMonitoring = isMonitoring,
                    onStartMonitoring = {
                        Log.d(TAG, "🚀 LAMBDA onStartMonitoring llamada")
                        viewModel.startMonitoring()
                        Log.d(TAG, "✅ viewModel.startMonitoring() ejecutado")
                    },
                    onStopMonitoring = {
                        Log.d(TAG, "⏸️ LAMBDA onStopMonitoring llamada")
                        viewModel.stopMonitoring()
                        Log.d(TAG, "✅ viewModel.stopMonitoring() ejecutado")
                    }
                )

                // Card de controles del dispositivo
                ConnectedControlsCard(
                    bleManager = bleManager,
                    onShutdownClicked = {
                        Log.d(TAG, "🔴 LAMBDA onShutdownClicked llamada")
                        powerAction = "shutdown"
                        showPowerDialog = true
                        Log.d(TAG, "✅ Diálogo de apagado mostrado")
                    },
                    onRestartClicked = {
                        Log.d(TAG, "🔄 LAMBDA onRestartClicked llamada")
                        powerAction = "restart"
                        showPowerDialog = true
                        Log.d(TAG, "✅ Diálogo de reinicio mostrado")
                    }
                )
            }

            // Mensaje de estado del escaneo
            if (scanMessage.isNotEmpty()) {
                ScanMessageCard(scanMessage)
            }

            // Toggle para mostrar log de debugging
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrar Log de Depuración",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = showDebugLog,
                    onCheckedChange = {
                        showDebugLog = it
                        Log.d(TAG, "🔧 Debug log: $it")
                    }
                )
            }

            // Log de debugging
            if (showDebugLog) {
                DebugLogCard(logs = scanLog)
            }

            // Instrucciones
            if (!isConnected) {
                InstructionsCard()
            }
        }
    }

    // Diálogo de confirmación
    if (showPowerDialog) {
        Log.d(TAG, "💬 Mostrando PowerActionDialog: $powerAction")

        PowerActionDialog(
            powerAction = powerAction,
            viewModel = viewModel,
            onDismiss = {
                Log.d(TAG, "❌ Diálogo cerrado")
                showPowerDialog = false
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ScanMessageCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun PowerActionDialog(
    powerAction: String,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val isShutdown = powerAction == "shutdown"

    AlertDialog(
        onDismissRequest = {
            Log.d(TAG, "💬 Diálogo descartado (click fuera)")
            onDismiss()
        },
        icon = {
            Icon(
                imageVector = if (isShutdown) Icons.Default.PowerSettingsNew else Icons.Default.Refresh,
                contentDescription = null,
                tint = if (isShutdown) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = if (isShutdown) "¿Apagar Dispositivo?" else "¿Reiniciar Dispositivo?")
        },
        text = {
            Text(
                text = if (isShutdown)
                    "El ESP32 entrará en modo sleep. Para encenderlo nuevamente, presiona el botón físico del dispositivo."
                else
                    "El ESP32 se reiniciará y deberás volver a conectarte."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    Log.d(TAG, "✅ Botón confirmar presionado: $powerAction")
                    if (isShutdown) {
                        Log.d(TAG, "🔴 Llamando viewModel.shutdownDevice()")
                        viewModel.shutdownDevice()
                    } else {
                        Log.d(TAG, "🔄 Llamando viewModel.restartDevice()")
                        viewModel.restartDevice()
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isShutdown)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isShutdown) "Apagar" else "Reiniciar")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Log.d(TAG, "❌ Botón cancelar presionado")
                onDismiss()
            }) {
                Text("Cancelar")
            }
        }
    )
}