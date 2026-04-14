package com.orbboard.boardoar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbboard.boardoar.data.preferences.AppPreferences
import com.orbboard.boardoar.presentation.components.OrbBottomBar
import com.orbboard.boardoar.presentation.navigation.OrbNavGraph
import com.orbboard.boardoar.presentation.navigation.Screen
import com.orbboard.boardoar.presentation.navigation.bottomNavScreens
import com.orbboard.boardoar.ui.theme.OrbBoardTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val onboardingCompleted = runBlocking { preferences.onboardingCompleted.first() }

        setContent {
            val colorMode by preferences.colorMode.collectAsState(initial = "Neon")

            OrbBoardTheme(colorMode = colorMode) {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route
                val showBottomBar = currentRoute in bottomNavScreens

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        if (showBottomBar) {
                            OrbBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Board.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    OrbNavGraph(
                        navController = navController,
                        startDestination = if (onboardingCompleted) Screen.Board.route
                        else Screen.Onboarding.route,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}
