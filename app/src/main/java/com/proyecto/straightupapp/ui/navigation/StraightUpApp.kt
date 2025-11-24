package com.proyecto.straightupapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.straightupapp.ui.screens.*
import com.proyecto.straightupapp.viewmodel.MainViewModel

@Composable
fun StraightUpApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onNavigateToDevice = { navController.navigate("deviceControl") },
                onNavigateToProgress = { navController.navigate("progress") },
                onNavigateToAchievements = { navController.navigate("achievements") },
                onNavigateToSettings = { navController.navigate("settings") },
                viewModel = mainViewModel
            )
        }

        composable("deviceControl") {
            DeviceControlScreen(
                bleManager = mainViewModel.bleManager,
                viewModel = mainViewModel,  // ← AGREGAR ESTA LÍNEA
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("progress") {
            ProgressScreen(
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }},
                onNavigateToAchievements = { navController.navigate("achievements") },
                onNavigateToSettings = { navController.navigate("settings") },
                viewModel = mainViewModel
            )
        }

        composable("achievements") {
            AchievementsScreen(
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }},
                onNavigateToProgress = { navController.navigate("progress") },
                onNavigateToSettings = { navController.navigate("settings") },
                viewModel = mainViewModel
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }},
                onNavigateToProgress = { navController.navigate("progress") },
                onNavigateToAchievements = { navController.navigate("achievements") },
                viewModel = mainViewModel
            )
        }
    }
}