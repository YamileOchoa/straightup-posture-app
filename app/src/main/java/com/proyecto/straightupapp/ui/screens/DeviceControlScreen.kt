// ═══════════════════════════════════════════════════════════════════
// 📁 ui/screens/DeviceControlScreen.kt - CON CONTROL DE ENCENDIDO
// ═══════════════════════════════════════════════════════════════════
package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.straightupapp.bluetooth.BleManager
import com.proyecto.straightupapp.bluetooth.ScanResultStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlScreen(
    bleManager: BleManager,
    onNavigateToSettings: () -> Unit
) {
    val isScanning by bleManager.isScanning.collectAsState()
    val isConnected by bleManager.isConnected.collectAsState()
    val connectedDeviceName by bleManager.connectedDeviceName.collectAsState()
    val lastPayload by bleManager.lastPayload.collectAsState()
    val scanLog by bleManager.scanLog.collectAsState()

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

            // Controles de conexión
            if (!isConnected) {
                ControlButtonsSection(
                    isScanning = isScanning,
                    isConnected = isConnected,
                    bleManager = bleManager,
                    onScanResult = { status, message ->
                        scanMessage = message
                    }
                )
            } else {
                // Controles cuando está conectado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Control Remoto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Botón de apagar ESP32
                        Button(
                            onClick = {
                                powerAction = "shutdown"
                                showPowerDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apagar Dispositivo ESP32")
                        }

                        // Botón de reiniciar ESP32
                        OutlinedButton(
                            onClick = {
                                powerAction = "restart"
                                showPowerDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reiniciar Dispositivo")
                        }

                        // Botón de desconectar
                        OutlinedButton(
                            onClick = { bleManager.disconnect() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.BluetoothDisabled, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Desconectar")
                        }
                    }
                }
            }

            // Mensaje de estado del escaneo
            if (scanMessage.isNotEmpty()) {
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
                            text = scanMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
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
                    onCheckedChange = { showDebugLog = it }
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

    // Diálogo de confirmación para acciones de poder
    if (showPowerDialog) {
        AlertDialog(
            onDismissRequest = { showPowerDialog = false },
            icon = {
                Icon(
                    imageVector = if (powerAction == "shutdown")
                        Icons.Default.PowerSettingsNew
                    else
                        Icons.Default.Refresh,
                    contentDescription = null,
                    tint = if (powerAction == "shutdown")
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = if (powerAction == "shutdown")
                        "¿Apagar Dispositivo?"
                    else
                        "¿Reiniciar Dispositivo?"
                )
            },
            text = {
                Text(
                    text = if (powerAction == "shutdown")
                        "El ESP32 entrará en modo sleep. Para encenderlo nuevamente, presiona el botón físico del dispositivo."
                    else
                        "El ESP32 se reiniciará y deberás volver a conectarte."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (powerAction == "shutdown") {
                            bleManager.writeString("SHUTDOWN")
                        } else {
                            bleManager.writeString("RESTART")
                        }
                        showPowerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (powerAction == "shutdown")
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (powerAction == "shutdown") "Apagar" else "Reiniciar"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPowerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ConnectionStatusCard(
    isConnected: Boolean,
    deviceName: String?,
    lastPayload: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isConnected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isConnected) "Conectado" else "Desconectado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isConnected && deviceName != null) {
                Divider()
                Text(
                    text = "Dispositivo: $deviceName",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (lastPayload != null) {
                Divider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (lastPayload.contains("ALERTA", ignoreCase = true))
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Último mensaje: $lastPayload",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButtonsSection(
    isScanning: Boolean,
    isConnected: Boolean,
    bleManager: BleManager,
    onScanResult: (ScanResultStatus, String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                if (isScanning) {
                    bleManager.stopScan()
                    onScanResult(ScanResultStatus.ScanStarted, "")
                } else {
                    val status = bleManager.startScan()
                    val message = when (status) {
                        ScanResultStatus.BleDisabled -> "Por favor, activa el Bluetooth"
                        ScanResultStatus.NoAdapter -> "Este dispositivo no tiene Bluetooth"
                        ScanResultStatus.ScanStarted -> ""
                    }
                    onScanResult(status, message)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isConnected,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Default.Close else Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isScanning) "Detener Escaneo" else "Buscar Dispositivo")
        }

        if (isScanning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Buscando POSTURA-ESP32...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DebugLogCard(logs: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Log de Depuración",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "Sin eventos aún...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(logs) { log ->
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (log.contains("❌") || log.contains("⚠️"))
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                    else if (log.contains("✅"))
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Instrucciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                text = "1. Asegúrate que el ESP32 esté encendido",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "2. Verifica que el Bluetooth esté activado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "3. Presiona 'Buscar Dispositivo'",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "4. Mantén presionado el botón físico 2s para apagar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}