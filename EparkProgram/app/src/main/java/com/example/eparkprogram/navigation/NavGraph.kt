@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { /* SplashScreen() */ }
        composable(Routes.LOGIN) { /* LoginScreen() */ }
        composable(Routes.REGISTER) { /* RegisterScreen() */ }
        composable(Routes.DRIVER_HOME) { /* DriverHomeScreen() */ }
        composable(Routes.ADMIN_HOME) { /* AdminDashboardScreen() */ }
        // el resto lo vas agregando conforme creas las pantallas
    }
}