package com.proyecto.straightupapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomBar(
    onHomeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onExerciseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Estadísticas") },
            label = { Text("Estadísticas") },
            selected = false,
            onClick = onStatsClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Ejercicios") },
            label = { Text("Ejercicios") },
            selected = false,
            onClick = onExerciseClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = onProfileClick
        )
    }
}

