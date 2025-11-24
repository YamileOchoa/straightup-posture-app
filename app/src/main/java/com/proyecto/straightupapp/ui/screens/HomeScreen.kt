package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.straightupapp.ui.components.BottomNavigationBar
import com.proyecto.straightupapp.ui.components.PostureAvatar
import com.proyecto.straightupapp.ui.components.PostureState
import com.proyecto.straightupapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDevice: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val isConnected by viewModel.bleManager.isConnected.collectAsState()
    val postureState by viewModel.postureState.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()

    // ⭐ CLAVE: Determinar el estado del avatar según la situación
    val avatarState = when {
        !isConnected -> PostureState.WAITING              // No conectado
        !isMonitoring -> PostureState.WAITING             // Conectado pero en standby
        else -> postureState                              // Monitoreando: usar estado real
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "StraightUp",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToDevice) {
                        Icon(
                            imageVector = if (isConnected)
                                Icons.Default.BluetoothConnected
                            else
                                Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth",
                            tint = if (isConnected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = {},
                onStatsClick = onNavigateToProgress,
                onExerciseClick = onNavigateToAchievements,
                onProfileClick = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Avatar animado - Usa avatarState calculado arriba
            PostureAvatar(
                state = avatarState,  // ⭐ CAMBIO: Usa avatarState en lugar de postureState
                modifier = Modifier.weight(1f)
            )

            // Mensaje de estado
            StatusMessage(
                isConnected = isConnected,
                isMonitoring = isMonitoring
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas del día (solo si está monitoreando)
            if (isConnected && isMonitoring) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Hoy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                icon = "✅",
                                value = todayStats.goodPostureCount.toString(),
                                label = "Correctas"
                            )
                            Divider(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(1.dp)
                            )
                            StatItem(
                                icon = "⚠️",
                                value = todayStats.badPostureCount.toString(),
                                label = "Alertas"
                            )
                            Divider(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(1.dp)
                            )
                            StatItem(
                                icon = "📊",
                                value = "${todayStats.postureScore}%",
                                label = "Score"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botones de control
            if (isConnected) {
                Button(
                    onClick = {
                        if (isMonitoring) viewModel.stopMonitoring()
                        else viewModel.startMonitoring()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMonitoring)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMonitoring) "Detener Monitoreo" else "Activar Monitoreo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onNavigateToDevice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Conectar Dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusMessage(
    isConnected: Boolean,
    isMonitoring: Boolean
) {
    val (message, color) = when {
        !isConnected -> "Conecta tu dispositivo para comenzar" to MaterialTheme.colorScheme.onSurfaceVariant
        !isMonitoring -> "Dispositivo en espera - Presiona activar" to MaterialTheme.colorScheme.primary
        else -> "Monitoreando tu postura activamente" to MaterialTheme.colorScheme.primary
    }

    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        fontWeight = if (isMonitoring) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}