package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.straightupapp.ui.components.BottomBar

@Composable
fun AchievementsScreen(
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
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Logros y Recompensas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Card: Nivel
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Profesional de Postura - Nivel 5", fontWeight = FontWeight.Bold)
                        Text("12,500 puntos", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 0.75f,
                            color = Color(0xFFF7E96B),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("750/1000 puntos para el siguiente nivel", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aquí agregarías las insignias y los botones de iniciar ejercicios
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AchievementsScreenPreview() {
    AchievementsScreen(
        onHomeClick = {},
        onStatsClick = {},
        onExerciseClick = {},
        onProfileClick = {}
    )
}