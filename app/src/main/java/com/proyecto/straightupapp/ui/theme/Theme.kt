package com.proyecto.straightupapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// TEMA LIGHT - Material Design 3
// Optimizado para apps de salud y bienestar
// ═══════════════════════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    // ─────────────────────────────────────────────────────────────
    // PRIMARY - Color principal de la marca (Verde salud)
    // ─────────────────────────────────────────────────────────────
    primary = GreenPrimary,                    // Botones principales, FABs
    onPrimary = White,                         // Texto sobre primary
    primaryContainer = GreenLight,             // Contenedores destacados
    onPrimaryContainer = GreenOnContainer,     // Texto sobre contenedores

    // ─────────────────────────────────────────────────────────────
    // SECONDARY - Acciones secundarias
    // ─────────────────────────────────────────────────────────────
    secondary = GreenAccent,                   // Botones secundarios
    onSecondary = White,                       // Texto sobre secondary
    secondaryContainer = GreenContainer,       // Chips, badges
    onSecondaryContainer = GreenDark,          // Texto sobre secondary containers

    // ─────────────────────────────────────────────────────────────
    // TERTIARY - Elementos de apoyo (opcional, usa grises)
    // ─────────────────────────────────────────────────────────────
    tertiary = GrayMedium,
    onTertiary = White,
    tertiaryContainer = Surface,
    onTertiaryContainer = GrayDark,

    // ─────────────────────────────────────────────────────────────
    // BACKGROUND - Fondo principal de la app
    // ─────────────────────────────────────────────────────────────
    background = OffWhite,                     // Fondo general cálido
    onBackground = GrayDark,                   // Texto principal

    // ─────────────────────────────────────────────────────────────
    // SURFACE - Tarjetas, diálogos, bottom sheets
    // ─────────────────────────────────────────────────────────────
    surface = White,                           // Tarjetas blancas
    onSurface = GrayDark,                      // Texto sobre tarjetas
    surfaceVariant = Surface,                  // Variación de superficie
    onSurfaceVariant = GrayMedium,             // Texto secundario

    // Tonos adicionales de superficie (Material Design 3)
    surfaceTint = GreenPrimary,                // Tinte para elevación
    surfaceBright = White,
    surfaceDim = Surface,
    surfaceContainer = Surface,
    surfaceContainerHigh = SurfaceVariant,
    surfaceContainerHighest = GrayLight,
    surfaceContainerLow = OffWhite,
    surfaceContainerLowest = White,

    // ─────────────────────────────────────────────────────────────
    // ERROR - Estados de error
    // ─────────────────────────────────────────────────────────────
    error = ErrorRed,
    onError = White,
    errorContainer = PostureBadBackground,
    onErrorContainer = PostureBadText,

    // ─────────────────────────────────────────────────────────────
    // OUTLINE - Bordes y divisores
    // ─────────────────────────────────────────────────────────────
    outline = GrayBorder,                      // Bordes de inputs
    outlineVariant = GrayLight,                // Divisores sutiles

    // ─────────────────────────────────────────────────────────────
    // INVERSE - Para elementos flotantes (Snackbars)
    // ─────────────────────────────────────────────────────────────
    inverseSurface = GrayDark,
    inverseOnSurface = White,
    inversePrimary = GreenAccent,

    // ─────────────────────────────────────────────────────────────
    // SCRIM - Overlay oscuro para modales
    // ─────────────────────────────────────────────────────────────
    scrim = Color(0x99000000)                  // 60% negro transparente
)

// ═══════════════════════════════════════════════════════════════════
// TEMA PRINCIPAL
// ═══════════════════════════════════════════════════════════════════

@Composable
fun StraightUpAppTheme(
    darkTheme: Boolean = false,  // Por ahora solo light, puedes agregar dark después
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}