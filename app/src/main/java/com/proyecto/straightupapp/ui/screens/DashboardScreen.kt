package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.proyecto.straightupapp.ui.components.BottomBar

@Composable
fun DashboardScreen(
    onGoToDeviceControl: () -> Unit,
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onExerciseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                onHomeClick = onHomeClick,
                onStatsClick = onStatsClick,
                onExerciseClick = onExerciseClick,
                onProfileClick = onProfileClick
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F6FA))
                    .padding(padding) // respeta el espacio del BottomBar
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Card Clip conectado
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clip Conectado", fontWeight = FontWeight.Bold)
                            Text(
                                "Dispositivo activo y monitoreando",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            Icons.Default.BluetoothConnected,
                            contentDescription = null,
                            tint = Color(0xFF0ECF7A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AsyncImage(
                    model = "https://cdn-icons-png.flaticon.com/512/5233/5233713.png",
                    contentDescription = "Posture Character",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Postura: Buena",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "¡Te ves genial!",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoCard(title = "4h 15m", subtitle = "Postura Buena", icon = "⏱")
                    InfoCard(title = "2°", subtitle = "Ángulo de Inclinación", icon = "📈")
                }
            }
        }
    )
}

@Composable
fun RowScope.InfoCard(title: String, subtitle: String, icon: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
