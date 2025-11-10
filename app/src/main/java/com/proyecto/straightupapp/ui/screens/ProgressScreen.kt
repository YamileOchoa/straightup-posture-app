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
fun ProgressScreen(
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
                    text = "Mi Progreso",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Card: Promedio Diario
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Promedio Diario de Buena Postura", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("6h 45m", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("85%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0ECF7A))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aquí podrías agregar gráficas de tiempo de buena postura y historial de correcciones
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    ProgressScreen(
        onHomeClick = {},
        onStatsClick = {},
        onExerciseClick = {},
        onProfileClick = {}
    )
}