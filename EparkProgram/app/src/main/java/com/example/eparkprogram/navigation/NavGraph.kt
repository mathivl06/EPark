package com.example.eparkprogram.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eparkprogram.ui.auth.LoginScreen
import com.example.eparkprogram.ui.auth.SplashScreen
import com.example.eparkprogram.ui.auth.RegisterScreen
import com.example.eparkprogram.ui.driver.DriverHomeScreen
import com.example.eparkprogram.ui.driver.MunicipalitySelectionScreen
import com.example.eparkprogram.ui.driver.NearbyZonesScreen
import com.example.eparkprogram.ui.driver.StartParkingScreen
import com.example.eparkprogram.ui.driver.ActiveSessionScreen
import com.example.eparkprogram.ui.driver.PaymentScreen
import com.example.eparkprogram.ui.driver.FinesScreen
import com.example.eparkprogram.ui.driver.HistoryScreen
import com.example.eparkprogram.ui.driver.ProfileScreen
import com.example.eparkprogram.ui.admin.AdminDashboardScreen
import com.example.eparkprogram.ui.admin.ZoneManagementScreen
import com.example.eparkprogram.ui.admin.ZoneEditorScreen
import com.example.eparkprogram.ui.admin.RateManagementScreen
import com.example.eparkprogram.ui.admin.ReportsScreen

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

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegistered = {
                    navController.navigate(Routes.DRIVER_HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DRIVER_HOME) {
            DriverHomeScreen(navController = navController)
        }

        composable(Routes.MUNICIPALITY_SELECT) {
            MunicipalitySelectionScreen(navController = navController)
        }

        // Ruta sin municipalityId — por si se navega directo desde otro lugar
        composable(Routes.NEARBY_ZONES) {
            NearbyZonesScreen(navController = navController, municipalityId = null)
        }

        // FIX: ruta con municipalityId — viene desde MunicipalitySelectionScreen
        composable("${Routes.NEARBY_ZONES}/{municipalityId}") { backStack ->
            val municipalityId = backStack.arguments?.getString("municipalityId")?.toIntOrNull()
            NearbyZonesScreen(navController = navController, municipalityId = municipalityId)
        }

        composable(Routes.START_PARKING) {
            StartParkingScreen(navController = navController)
        }

        composable(Routes.ACTIVE_SESSION) {
            ActiveSessionScreen(navController = navController)
        }

        composable(Routes.PAYMENT) {
            PaymentScreen(navController = navController)
        }
        composable("${Routes.PAYMENT_FINE}/{fineId}") { backStack ->
            val fineId = backStack.arguments?.getString("fineId")?.toLongOrNull()
            PaymentScreen(navController = navController, fineId = fineId)
        }

        composable(Routes.FINES) {
            FinesScreen(navController = navController)
        }

        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboardScreen(navController = navController)
        }

        composable(Routes.ZONE_MANAGEMENT) {
            ZoneManagementScreen(navController = navController)
        }

        composable("${Routes.ZONE_EDITOR}/{zoneId}") { backStack ->
            val zoneId = backStack.arguments?.getString("zoneId")?.toIntOrNull()
            ZoneEditorScreen(navController = navController, zoneId = zoneId)
        }

        composable(Routes.ZONE_EDITOR) {
            ZoneEditorScreen(navController = navController, zoneId = null)
        }

        composable(Routes.RATE_MANAGEMENT) {
            RateManagementScreen(navController = navController)
        }

        composable(Routes.REPORTS) {
            ReportsScreen(navController = navController)
        }
    }
}