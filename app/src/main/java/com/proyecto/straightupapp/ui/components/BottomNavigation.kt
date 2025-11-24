package com.proyecto.straightupapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onExerciseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = selectedItem == 0,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Progreso") },
            selected = selectedItem == 1,
            onClick = onStatsClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
            label = { Text("Logros") },
            selected = selectedItem == 2,
            onClick = onExerciseClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Config") },
            selected = selectedItem == 3,
            onClick = onProfileClick
        )
    }
}