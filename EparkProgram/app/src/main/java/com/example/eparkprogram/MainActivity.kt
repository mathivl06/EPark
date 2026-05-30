package com.example.eparkprogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.eparkprogram.navigation.NavGraph
import com.example.eparkprogram.ui.theme.EparkProgramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EparkProgramTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}