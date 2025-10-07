package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.chalabysolutions.toepenscorebord.viewmodel.PlayerViewModel
import com.chalabysolutions.toepenscorebord.data.model.Player

@Composable
fun ScoreboardScreen(
    navController: NavHostController,
    viewModel: PlayerViewModel = viewModel()
) {
    val players by viewModel.players.observeAsState(emptyList<Player>())
    ScoreboardScreenContent(
        players = players,
        onAddPoints = { player -> viewModel.addPoints(player) },
        onBack = { navController.popBackStack()}
    )
}

@Composable
fun ScoreboardScreenContent(
    players: List<Player>,
    onAddPoints: (Player) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Scorebord", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Terug")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(players) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(player.name)
                    Row {
                        Text("Punten: ${player.points}")
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onAddPoints(player) }) {
                            Text("+1")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreboardScreenPreview() {
    val dummyPlayers = listOf(
        Player(id = 1, name = "Jan", points = 3),
        Player(id = 2, name = "Piet", points = 7),
        Player(id = 3, name = "Anna", points = 0)
    )

    ScoreboardScreenContent(
        players = dummyPlayers,
        onAddPoints = {},
        onBack = {}
    )
}
