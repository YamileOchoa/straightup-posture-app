// ═══════════════════════════════════════════════════════════════════
// 📁 ui/components/PostureAvatar.kt
// ═══════════════════════════════════════════════════════════════════
package com.proyecto.straightupapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.straightupapp.ui.theme.*

enum class PostureState {
    GOOD,      // Postura correcta
    BAD,       // Postura incorrecta
    WAITING    // Sin conexión
}

@Composable
fun PostureAvatar(
    state: PostureState,
    modifier: Modifier = Modifier
) {
    // Animación de inclinación del avatar
    val targetRotation = when (state) {
        PostureState.GOOD -> 0f
        PostureState.BAD -> 15f
        PostureState.WAITING -> 0f
    }

    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    // Animación de color de fondo
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            PostureState.GOOD -> GreenLight
            PostureState.BAD -> Color(0xFFFFCDD2)  // Rojo suave
            PostureState.WAITING -> GrayLight
        },
        animationSpec = tween(600),
        label = "backgroundColor"
    )

    // Animación de rebote en alerta
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (state == PostureState.BAD) 10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(y = (-bounce).dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                // Aplicar rotación para simular mala postura
                rotate(degrees = rotation, pivot = Offset(centerX, centerY)) {
                    // Cabeza
                    drawCircle(
                        color = when (state) {
                            PostureState.GOOD -> GreenPrimary
                            PostureState.BAD -> ErrorRed
                            PostureState.WAITING -> GrayMedium
                        },
                        radius = 40.dp.toPx(),
                        center = Offset(centerX, centerY - 30.dp.toPx())
                    )

                    // Cuerpo (línea vertical)
                    drawLine(
                        color = when (state) {
                            PostureState.GOOD -> GreenDark
                            PostureState.BAD -> ErrorRed.copy(alpha = 0.8f)
                            PostureState.WAITING -> GrayDark
                        },
                        start = Offset(centerX, centerY + 10.dp.toPx()),
                        end = Offset(centerX, centerY + 80.dp.toPx()),
                        strokeWidth = 12.dp.toPx()
                    )

                    // Brazo izquierdo
                    drawLine(
                        color = when (state) {
                            PostureState.GOOD -> GreenDark
                            PostureState.BAD -> ErrorRed.copy(alpha = 0.8f)
                            PostureState.WAITING -> GrayDark
                        },
                        start = Offset(centerX, centerY + 20.dp.toPx()),
                        end = Offset(centerX - 30.dp.toPx(), centerY + 40.dp.toPx()),
                        strokeWidth = 8.dp.toPx()
                    )

                    // Brazo derecho
                    drawLine(
                        color = when (state) {
                            PostureState.GOOD -> GreenDark
                            PostureState.BAD -> ErrorRed.copy(alpha = 0.8f)
                            PostureState.WAITING -> GrayDark
                        },
                        start = Offset(centerX, centerY + 20.dp.toPx()),
                        end = Offset(centerX + 30.dp.toPx(), centerY + 40.dp.toPx()),
                        strokeWidth = 8.dp.toPx()
                    )

                    // Cara - Ojos
                    if (state == PostureState.BAD) {
                        // Ojos en X (preocupado)
                        drawLine(
                            color = Color.White,
                            start = Offset(centerX - 20.dp.toPx(), centerY - 35.dp.toPx()),
                            end = Offset(centerX - 10.dp.toPx(), centerY - 25.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(centerX - 10.dp.toPx(), centerY - 35.dp.toPx()),
                            end = Offset(centerX - 20.dp.toPx(), centerY - 25.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(centerX + 10.dp.toPx(), centerY - 35.dp.toPx()),
                            end = Offset(centerX + 20.dp.toPx(), centerY - 25.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(centerX + 20.dp.toPx(), centerY - 35.dp.toPx()),
                            end = Offset(centerX + 10.dp.toPx(), centerY - 25.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )
                    } else {
                        // Ojos normales
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = Offset(centerX - 15.dp.toPx(), centerY - 30.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = Offset(centerX + 15.dp.toPx(), centerY - 30.dp.toPx())
                        )
                    }

                    // Boca
                    val mouthPath = Path().apply {
                        if (state == PostureState.GOOD) {
                            // Sonrisa
                            moveTo(centerX - 15.dp.toPx(), centerY - 15.dp.toPx())
                            quadraticBezierTo(
                                centerX, centerY - 10.dp.toPx(),
                                centerX + 15.dp.toPx(), centerY - 15.dp.toPx()
                            )
                        } else {
                            // Boca triste
                            moveTo(centerX - 15.dp.toPx(), centerY - 10.dp.toPx())
                            quadraticBezierTo(
                                centerX, centerY - 15.dp.toPx(),
                                centerX + 15.dp.toPx(), centerY - 10.dp.toPx()
                            )
                        }
                    }
                    drawPath(
                        path = mouthPath,
                        color = Color.White,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto de estado
        Text(
            text = when (state) {
                PostureState.GOOD -> "¡Excelente postura!"
                PostureState.BAD -> "Endereza tu espalda"
                PostureState.WAITING -> "Conecta tu dispositivo"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = when (state) {
                PostureState.GOOD -> SuccessGreen
                PostureState.BAD -> ErrorRed
                PostureState.WAITING -> GrayMedium
            }
        )

        if (state == PostureState.BAD) {
            Text(
                text = "Tu espalda te lo agradecerá",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium
            )
        }
    }
}