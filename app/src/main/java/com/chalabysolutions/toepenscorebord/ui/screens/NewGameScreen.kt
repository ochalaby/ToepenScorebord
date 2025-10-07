package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chalabysolutions.toepenscorebord.ui.navigation.Screen
import com.chalabysolutions.toepenscorebord.viewmodel.PlayerViewModel

@Composable
fun NewGameScreen(
    navController: NavController,
    viewModel: PlayerViewModel = viewModel()
) {
    var playerName by remember { mutableStateOf("") }
    val players by viewModel.players.observeAsState(emptyList())

    NewGameScreenContent(
        navController = navController,
        players = players.map { it.name }, // lijst van namen
        playerName = playerName,
        onPlayerNameChange = { playerName = it },
        onAddPlayer = {
            if (playerName.isNotBlank()) {
                viewModel.addPlayer(playerName)
                playerName = ""
            }
        },
        onStartGame = {
            viewModel.resetPlayers()
            players.forEach { p -> viewModel.addPlayer(p.name) }
            navController.navigate(Screen.Scoreboard.route)
        },
        onBack = { navController.popBackStack()}
    )
}

@Composable
fun NewGameScreenContent(
    navController: NavController,
    players: List<String>,
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    onAddPlayer: () -> Unit,
    onStartGame: () -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Nieuwe avond", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = playerName,
                onValueChange = onPlayerNameChange,
                label = { Text("Spelernaam") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onAddPlayer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Speler toevoegen")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(players) { player ->
                    Text(player, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start avond")
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = onBack) {
                Text("Terug")
            }

        }
    }
}

@Preview(showBackground = true)
//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewGameScreenPreview() {
    val navController = rememberNavController()
    NewGameScreenContent(
        navController = navController,
        players = listOf("Jan", "Piet", "Klaas"),
        playerName = "Anna",
        onPlayerNameChange = {},
        onAddPlayer = {},
        onStartGame = {},
        onBack = {}
    )
}