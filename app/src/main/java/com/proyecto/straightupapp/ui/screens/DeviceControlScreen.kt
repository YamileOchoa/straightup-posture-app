package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.straightupapp.ui.components.BottomBar
import kotlin.math.roundToInt

@Composable
fun DeviceControlScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onExerciseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var sliderValue by remember { mutableStateOf(0.5f) }
    val green = Color(0xFF0ECF7A)
    val yellow = Color(0xFFF7E96B)

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
                    .background(Color(0xFFFDFDFE))
                    .padding(padding)
                    .padding(16.dp)
            ) {

                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Control del Dispositivo",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFE8FFE7), Color.White)
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📳", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Intensidad de Vibración",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Máx", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            modifier = Modifier
                                .height(200.dp)
                                .width(50.dp),
                            colors = SliderDefaults.colors(
                                activeTrackColor = green,
                                inactiveTrackColor = yellow
                            ),
                            valueRange = 0f..1f
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Min", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = "${(sliderValue * 100).roundToInt()}%",
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apagar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = green
                        )
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pausar", color = Color.White)
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DeviceControlScreenPreview() {
    DeviceControlScreen(
        onBack = {},
        onHomeClick = {},
        onStatsClick = {},
        onExerciseClick = {},
        onProfileClick = {}
    )
}
