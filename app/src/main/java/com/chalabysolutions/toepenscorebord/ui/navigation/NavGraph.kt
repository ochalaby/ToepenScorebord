package com.chalabysolutions.toepenscorebord.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chalabysolutions.toepenscorebord.ui.screens.HomeScreen
import com.chalabysolutions.toepenscorebord.ui.screens.PlayersScreen
import com.chalabysolutions.toepenscorebord.ui.screens.RoundScreen
import com.chalabysolutions.toepenscorebord.ui.screens.SessionScreen
import com.chalabysolutions.toepenscorebord.viewmodel.PlayerViewModel
import com.chalabysolutions.toepenscorebord.viewmodel.SessionViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Session : Screen("session/{sessionId}") {
        fun createRoute(sessionId: Int) = "session/$sessionId"
    }
//    object Player : Screen("Players/{selectedIds}") {
//        fun createRoute(selectedIds: List<Int>): String {
//            val idsArg = selectedIds.joinToString(",") // encode als CSV
//            return "Players/$idsArg"
//        }
//    }
    object Player : Screen("Players")
    object Round : Screen("round/{roundId}") {
        fun createRoute(roundId: Int) = "round/$roundId"
    }
}

@Composable
fun ToepenNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable( Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Session.route) { backStackEntry ->
            val viewModel: SessionViewModel = hiltViewModel()
            SessionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Player.route) { backStackEntry ->
            val viewModel: PlayerViewModel = hiltViewModel()
            val selectedPlayers = backStackEntry.savedStateHandle
                .get<List<Int>>("selectedPlayers")?.toList() ?: emptyList()

            PlayersScreen(
                navController = navController,
                viewModel = viewModel,
                selectedPlayers = selectedPlayers
            )
        }

        composable(Screen.Round.route) { backStackEntry ->
            val roundId = backStackEntry.arguments?.getString("roundId")?.toInt() ?: 0
            RoundScreen(navController, roundId)
        }
    }
}