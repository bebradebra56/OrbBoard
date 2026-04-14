package com.orbboard.boardoar.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Board : Screen("board")
    object Categories : Screen("categories")
    object Activity : Screen("activity")
    object Settings : Screen("settings")

    object CreateOrb : Screen("create_orb?orbId={orbId}") {
        fun createRoute(orbId: Long = -1L) = "create_orb?orbId=$orbId"
    }

    object OrbDetail : Screen("orb_detail/{orbId}") {
        fun createRoute(orbId: Long) = "orb_detail/$orbId"
    }

    object Stats : Screen("stats")

    object Focus : Screen("focus?orbId={orbId}") {
        fun createRoute(orbId: Long = -1L) = "focus?orbId=$orbId"
    }

    object Search : Screen("search")
}

val bottomNavScreens = listOf(
    Screen.Board.route,
    Screen.Categories.route,
    Screen.Activity.route,
    Screen.Settings.route
)
