package com.proyecto.straightupapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.proyecto.straightupapp.ui.navigation.StraightUpApp
import com.proyecto.straightupapp.ui.theme.StraightUpAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StraightUpAppTheme {
                StraightUpApp()
            }
        }
    }
}