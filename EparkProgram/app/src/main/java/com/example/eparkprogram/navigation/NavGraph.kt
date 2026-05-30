package com.example.eparkprogram.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eparkprogram.ui.auth.LoginScreen
import com.example.eparkprogram.ui.auth.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginAsDriver = {
                    navController.navigate(Routes.DRIVER_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginAsAdmin = {
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // El resto de pantallas las vas agregando acá conforme las creás
    }
}