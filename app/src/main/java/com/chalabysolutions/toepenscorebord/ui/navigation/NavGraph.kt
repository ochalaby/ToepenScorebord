package com.chalabysolutions.toepenscorebord.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chalabysolutions.toepenscorebord.ui.screens.HomeScreen
import com.chalabysolutions.toepenscorebord.ui.screens.NewGameScreen
import com.chalabysolutions.toepenscorebord.ui.screens.ScoreboardScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NewGame : Screen("new_game")
    object Scoreboard : Screen("scoreboard")
}

@Composable
fun ToepenNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.NewGame.route) { NewGameScreen(navController) }
        composable(Screen.Scoreboard.route) { ScoreboardScreen(navController) }
    }
}