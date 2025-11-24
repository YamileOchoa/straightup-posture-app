package com.proyecto.straightupapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.straightupapp.ui.components.BottomNavigationBar
import com.proyecto.straightupapp.viewmodel.Achievement
import com.proyecto.straightupapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val achievements by viewModel.achievements.collectAsState()

    // Logros predefinidos
    val allAchievements = listOf(
        Achievement("first_session", "Primera Sesión", "Inicia tu viaje", "🎉"),
        Achievement("perfect_posture", "Postura Perfecta", "10 correctas sin alertas", "⭐"),
        Achievement("posture_warrior", "Guerrero", "50 posturas correctas", "🏆"),
        Achievement("week_streak", "Racha Semanal", "7 días seguidos", "🔥"),
        Achievement("hundred_club", "Club de los 100", "100 posturas correctas", "💯"),
        Achievement("zen_master", "Maestro Zen", "Score 95% o más", "🧘")
    )

    val unlockedIds = achievements.map { it.id }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Logros",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 2,
                onHomeClick = onNavigateToHome,
                onStatsClick = onNavigateToProgress,
                onExerciseClick = {},
                onProfileClick = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Resumen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${achievements.size}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Desbloqueados",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${allAchievements.size}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Totales",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}