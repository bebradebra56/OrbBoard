package com.orbboard.boardoar.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.orbboard.boardoar.presentation.activity.ActivityScreen
import com.orbboard.boardoar.presentation.board.BoardScreen
import com.orbboard.boardoar.presentation.categories.CategoriesScreen
import com.orbboard.boardoar.presentation.create.CreateOrbScreen
import com.orbboard.boardoar.presentation.detail.OrbDetailScreen
import com.orbboard.boardoar.presentation.focus.FocusScreen
import com.orbboard.boardoar.presentation.onboarding.OnboardingScreen
import com.orbboard.boardoar.presentation.search.SearchScreen
import com.orbboard.boardoar.presentation.settings.SettingsScreen
import com.orbboard.boardoar.presentation.stats.StatsScreen

@Composable
fun OrbNavGraph(
    navController: NavHostController,
    startDestination: String,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Board.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Board.route) {
            BoardScreen(
                paddingValues = paddingValues,
                onCreateOrb = { navController.navigate(Screen.CreateOrb.createRoute()) },
                onOrbClick = { orbId -> navController.navigate(Screen.OrbDetail.createRoute(orbId)) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onStatsClick = { navController.navigate(Screen.Stats.route) }
            )
        }

        composable(
            route = Screen.CreateOrb.route,
            arguments = listOf(navArgument("orbId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val orbId = backStackEntry.arguments?.getLong("orbId") ?: -1L
            CreateOrbScreen(
                orbId = orbId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                paddingValues = paddingValues,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Board.route) {
                        popUpTo(Screen.Board.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrbDetail.route,
            arguments = listOf(navArgument("orbId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orbId = backStackEntry.arguments?.getLong("orbId") ?: -1L
            OrbDetailScreen(
                orbId = orbId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.CreateOrb.createRoute(orbId)) },
                onFocus = { navController.navigate(Screen.Focus.createRoute(orbId)) }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Focus.route,
            arguments = listOf(navArgument("orbId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val orbId = backStackEntry.arguments?.getLong("orbId") ?: -1L
            FocusScreen(
                initialOrbId = orbId,
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Activity.route) {
            ActivityScreen(paddingValues = paddingValues)
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onOrbClick = { orbId -> navController.navigate(Screen.OrbDetail.createRoute(orbId)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(paddingValues = paddingValues)
        }
    }
}
