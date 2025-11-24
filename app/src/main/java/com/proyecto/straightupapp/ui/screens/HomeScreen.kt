package com.proyecto.straightupapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.straightupapp.ui.components.BottomNavigationBar
import com.proyecto.straightupapp.ui.components.PostureAvatar
import com.proyecto.straightupapp.ui.components.PostureState
import com.proyecto.straightupapp.ui.theme.*
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

    val avatarState = when {
        !isConnected -> PostureState.WAITING
        !isMonitoring -> PostureState.WAITING
        else -> postureState
    }

    Scaffold(
        topBar = {
            // TopBar con diseño limpio y elevación sutil
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Logo/Icono de la app
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            "StraightUp",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Indicador de conexión mejorado
                    ConnectionIndicator(
                        isConnected = isConnected,
                        onClick = onNavigateToDevice
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
        // Fondo con gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundGradientStart,
                            BackgroundGradientEnd
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Estado de monitoreo con animación
                AnimatedVisibility(
                    visible = isConnected,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    MonitoringStatusBanner(
                        isMonitoring = isMonitoring,
                        postureState = avatarState
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar con peso flexible
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PostureAvatar(
                        state = avatarState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Estadísticas mejoradas (solo cuando está monitoreando)
                AnimatedVisibility(
                    visible = isConnected && isMonitoring,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    StatsCard(todayStats = todayStats)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones de acción principales
                ActionButtons(
                    isConnected = isConnected,
                    isMonitoring = isMonitoring,
                    onToggleMonitoring = {
                        if (isMonitoring) viewModel.stopMonitoring()
                        else viewModel.startMonitoring()
                    },
                    onNavigateToDevice = onNavigateToDevice
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ConnectionIndicator(
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isConnected)
            PostureGoodBackground
        else
            PostureWaitingBackground,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador pulsante cuando está conectado
            if (isConnected) {
                PulsingDot()
            } else {
                Icon(
                    imageVector = Icons.Default.BluetoothDisabled,
                    contentDescription = null,
                    tint = GrayMedium,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = if (isConnected) "Conectado" else "Sin conexión",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isConnected) PostureGoodText else GrayMedium
            )
        }
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(SuccessGreen.copy(alpha = alpha))
    )
}

@Composable
private fun MonitoringStatusBanner(
    isMonitoring: Boolean,
    postureState: PostureState
) {
    val (backgroundColor, textColor, icon, message) = when {
        !isMonitoring -> {
            Tuple4(
                PostureWaitingBackground,
                PostureWaitingText,
                Icons.Outlined.Pause,
                "En espera"
            )
        }
        postureState == PostureState.GOOD -> {
            Tuple4(
                PostureGoodBackground,
                PostureGoodText,
                Icons.Outlined.CheckCircle,
                "Postura excelente"
            )
        }
        else -> {
            Tuple4(
                PostureBadBackground,
                PostureBadText,
                Icons.Outlined.Warning,
                "Corrige tu postura"
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (isMonitoring) {
                    Text(
                        text = "Monitoreando activamente",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(todayStats: com.proyecto.straightupapp.viewmodel.DailyStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Encabezado de estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estadísticas de Hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrayDark
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        todayStats.postureScore >= 80 -> PostureGoodBackground
                        todayStats.postureScore >= 60 -> WarningAmber.copy(alpha = 0.1f)
                        else -> PostureBadBackground
                    }
                ) {
                    Text(
                        text = "${todayStats.postureScore}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            todayStats.postureScore >= 80 -> PostureGoodText
                            todayStats.postureScore >= 60 -> WarningAmber
                            else -> PostureBadText
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Estadísticas en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModernStatItem(
                    icon = Icons.Outlined.CheckCircle,
                    value = todayStats.goodPostureCount.toString(),
                    label = "Correctas",
                    color = SuccessGreen
                )

                VerticalDivider()

                ModernStatItem(
                    icon = Icons.Outlined.Warning,
                    value = todayStats.badPostureCount.toString(),
                    label = "Alertas",
                    color = ErrorRed
                )

                VerticalDivider()

                ModernStatItem(
                    icon = Icons.Outlined.Timeline,
                    value = "${(todayStats.goodPostureCount + todayStats.badPostureCount)}",
                    label = "Total",
                    color = InfoBlue
                )
            }
        }
    }
}

@Composable
private fun ModernStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = GrayDark
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrayMedium
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(80.dp)
            .background(GrayLight)
    )
}

@Composable
private fun ActionButtons(
    isConnected: Boolean,
    isMonitoring: Boolean,
    onToggleMonitoring: () -> Unit,
    onNavigateToDevice: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isConnected) {
            // Botón principal con animación
            Button(
                onClick = onToggleMonitoring,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(if (isMonitoring) 0.dp else 8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring)
                        ErrorRed
                    else
                        GreenPrimary,
                    contentColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isMonitoring) 0.dp else 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isMonitoring) "Detener Monitoreo" else "Iniciar Monitoreo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón secundario para ir a configuración de dispositivo
            OutlinedButton(
                onClick = onNavigateToDevice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GreenPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.5.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configurar Dispositivo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Estado sin conexión - CTA prominente
            Button(
                onClick = onNavigateToDevice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Conectar Dispositivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mensaje informativo
            Text(
                text = "Conecta tu dispositivo para comenzar el monitoreo de tu postura",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

// Helper para tuplas (hasta que Kotlin lo soporte nativamente)
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)