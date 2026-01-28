package com.cryptovision.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cryptovision.feature.dashboard.DashboardScreen
import com.cryptovision.feature.details.DetailsScreen
import com.cryptovision.feature.favorites.FavoritesScreen

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Favorites : Screen("favorites")
    object Details : Screen("details/{coinId}") {
        fun createRoute(coinId: String) = "details/$coinId"
    }
}

/**
 * Main navigation host for the app.
 */
@Composable
fun CryptoVisionNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onCoinClick = { coinId ->
                    navController.navigate(Screen.Details.createRoute(coinId))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCoinClick = { coinId ->
                    navController.navigate(Screen.Details.createRoute(coinId))
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("coinId") { type = NavType.StringType }
            )
        ) {
            DetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
