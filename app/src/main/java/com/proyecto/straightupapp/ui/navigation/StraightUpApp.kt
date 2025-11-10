package com.proyecto.straightupapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.straightupapp.ui.screens.DashboardScreen
import com.proyecto.straightupapp.ui.screens.DeviceControlScreen

@Composable
fun StraightUpApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {

        composable("dashboard") {
            DashboardScreen(
                onGoToDeviceControl = { navController.navigate("deviceControl") },
                onHomeClick = { /* Acción Home */ },
                onStatsClick = { /* Acción Stats */ },
                onExerciseClick = { /* Acción Ejercicios */ },
                onProfileClick = { /* Acción Perfil */ }
            )
        }

        composable("deviceControl") {
            DeviceControlScreen(
                onBack = { navController.popBackStack() },
                onHomeClick = { navController.navigate("dashboard") },
                onStatsClick = { /* Acción Stats */ },
                onExerciseClick = { /* Acción Ejercicios */ },
                onProfileClick = { /* Acción Perfil */ }
            )
        }
    }
}