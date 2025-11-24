package com.proyecto.straightupapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = GreenAccent,
    onSecondary = GrayDark,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenDark,
    background = White,
    onBackground = GrayDark,
    surface = OffWhite,
    onSurface = GrayDark,
    surfaceVariant = GreenLight,
    onSurfaceVariant = GrayMedium,
    error = ErrorRed,
    onError = White,
    outline = GrayLight
)

@Composable
fun StraightUpAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
