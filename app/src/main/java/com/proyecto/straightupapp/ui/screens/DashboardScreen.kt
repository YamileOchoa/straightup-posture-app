package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.straightupapp.viewmodel.MainViewModel
import com.proyecto.straightupapp.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onGoToDeviceControl: () -> Unit,
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onExerciseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val isConnected by viewModel.bleManager.isConnected.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StraightUp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = onHomeClick,
                onStatsClick = onStatsClick,
                onExerciseClick = onExerciseClick,
                onProfileClick = onProfileClick
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
            ConnectionStatusCard(isConnected = isConnected)

            // Botón para ir a control de dispositivo
            if (!isConnected) {
                Button(
                    onClick = onGoToDeviceControl,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Conectar Dispositivo")
                }
            }

            // Estadísticas del día
            if (isConnected) {
                StatsCard(stats = todayStats)

                // Botón de monitoreo
                Button(
                    onClick = {
                        if (isMonitoring) viewModel.stopMonitoring()
                        else viewModel.startMonitoring()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMonitoring)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isMonitoring) "Detener Monitoreo" else "Iniciar Monitoreo")
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Column {
                Text(
                    text = if (isConnected) "Conectado" else "Desconectado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isConnected)
                        "Dispositivo ESP32 conectado"
                    else
                        "Conecta tu dispositivo para empezar",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun StatsCard(stats: com.proyecto.straightupapp.viewmodel.DailyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estadísticas de Hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("✅ Correctas", stats.goodPostureCount.toString())
                StatItem("⚠️ Alertas", stats.badPostureCount.toString())
                StatItem("📊 Score", "${stats.postureScore}%")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
