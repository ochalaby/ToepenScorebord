package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.relation.RoundPlayerWithPlayer
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import com.chalabysolutions.toepenscorebord.ui.theme.ToepenScorebordTheme
import com.chalabysolutions.toepenscorebord.viewmodel.RoundViewModel

@Composable
fun RoundScreen (
    navController: NavController,
    roundId: Int,
    viewModel: RoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val knockCounter by viewModel.knockCounter

    // Trigger load when screen opens
    LaunchedEffect(roundId) {
        viewModel.loadRound(roundId)
    }

    RoundScreenContent(
        navController = navController,
        uiState = uiState,
        onPass = { playerId -> viewModel.pass(roundId, playerId) },
        onKnock = { viewModel.knock() },
        onWin = { playerId -> viewModel.win(roundId, playerId) },
        knockCounter = knockCounter,
        onEndGame = { uiState.roundWithPlayers?.round?.let { viewModel.endCurrentGame(it) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundScreenContent(
    navController: NavController,
    uiState: RoundViewModel.UiState,
    onPass: (Int) -> Unit = {},
    onKnock: () -> Unit = {},
    onWin: (Int) -> Unit = {},
    knockCounter: Int,
    onEndGame: () -> Unit = {}
) {
    val roundWithPlayers = uiState.roundWithPlayers
    uiState.roundWithPlayers?.round?.sessionId

    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text("Ronde ${roundWithPlayers?.round?.roundNumber ?: "-"} (tot ${roundWithPlayers?.round?.maxPoints}) punten") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug naar Session")
                }
            }
        ) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                roundWithPlayers?.let {
                    ActiveGameComposable(
                        players = it.players,
                        onPass = onPass,
                        onKnock = onKnock,
                        onWin = onWin,
                        knockCounter = knockCounter,
                        onEndGame = onEndGame
                    )
                } ?: Text("Bezig met laden...")
            }
        }
    }
}

@Composable
fun ActiveGameComposable(
    players: List<RoundPlayerWithPlayer>,
    onPass: (Int) -> Unit,
    onKnock: () -> Unit,
    onWin: (Int) -> Unit,
    knockCounter: Int,
    onEndGame: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        players.forEach { roundPlayerWithPlayer ->
            val player = roundPlayerWithPlayer.player
            val roundPlayer = roundPlayerWithPlayer.roundPlayer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    // Eerste rij: naam links + ScoreMarks ernaast
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${player.name} (${roundPlayer.points})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ScoreMarks(score = roundPlayer.points)
                        Spacer(modifier = Modifier.weight(1f)) // duwt alles links
                    }

                    Spacer(Modifier.height(8.dp))

                    // Tweede rij: knoppen rechts uitgelijnd
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { onPass(roundPlayer.playerId) }, enabled = !roundPlayer.eliminated) {
                            Text("Pas")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onWin(roundPlayer.playerId) }, enabled = !roundPlayer.eliminated) {
                            Text("Win")
                        }
                    }
                }
            }
        }


        // Klop-counter
        Spacer(Modifier.height(42.dp))
        Text(
            text = "Aantal kloppen: $knockCounter",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(4.dp))
        Button(onClick = { onKnock() }) {
            Text("Klop")
        }

        Spacer(Modifier.height(42.dp))
        Button(onClick = onEndGame) { Text("Nieuwe Game") }
    }
}

@Composable
fun ScoreMarks(
    score: Int,
    modifier: Modifier = Modifier,
    lineHeight: Float = 24f,     // hoogte van elk streepje in pixels
    lineWidth: Float = 4f,       // dikte van streepje
    groupSpacing: Float = 8f     // ruimte tussen groepjes van 5
) {
    val lineColor = MaterialTheme.colorScheme.onBackground

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(groupSpacing.dp)) {
        val groupsOfFive = score / 5
        val remainder = score % 5
        val density = LocalDensity.current
        val streepjesWidth = with(density) { 16.dp.toPx() } // breedte van 4 streepjes binnen een groepje

        // Volledige groepjes van 5
        repeat(groupsOfFive) {
            Canvas(modifier = Modifier.height(lineHeight.dp).width(streepjesWidth.dp)) {
                val spacing = size.width / 4
                // 4 verticale streepjes
                for (i in 0 until 4) {
                    val x = i * spacing + spacing / 2
                    drawLine(
                        color = lineColor,
                        start = Offset(x = x, y = 0f),
                        end = Offset(x = x, y = size.height),
                        strokeWidth = lineWidth
                    )
                }
                // diagonaal over 4 streepjes
                drawLine(
                    color = lineColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f),
                    strokeWidth = lineWidth
                )
            }
        }

        // Overgebleven streepjes (<5)
        if (remainder > 0) {
            Canvas(modifier = Modifier.height(lineHeight.dp).width(streepjesWidth.dp)) {
                val spacing = size.width / 4
                for (i in 0 until remainder) {
                    val x = i * spacing + spacing / 2
                    drawLine(
                        color = lineColor,
                        start = Offset(x = x, y = 0f),
                        end = Offset(x = x, y = size.height),
                        strokeWidth = lineWidth
                    )
                }
            }
        }
    }
}

@Composable
fun RoundScreenPreviewContent(darkTheme: Boolean = false, knockCounter: Int = 0) {
    val navController = rememberNavController()
    val dummyPlayers = listOf(
        RoundPlayerWithPlayer(
            roundPlayer = RoundPlayer(roundId = 1, playerId = 1, points = 4, eliminated = true),
            player = Player(id = 1, name = "Jan")
        ),
        RoundPlayerWithPlayer(
            roundPlayer = RoundPlayer(roundId = 1, playerId = 2, points = 13),
            player = Player(id = 2, name = "Piet")
        ),
        RoundPlayerWithPlayer(
            roundPlayer = RoundPlayer(roundId = 1, playerId = 3, points = 7),
            player = Player(id = 3, name = "Henk")
        )
    )
    val dummyRoundWithPlayers = RoundWithPlayers(
        round = Round(id = 1, roundNumber = 3, sessionId = 1, active = true),
        players = dummyPlayers
    )

    ToepenScorebordTheme(darkTheme = darkTheme) {
        RoundScreenContent(
            navController = navController,
            uiState = RoundViewModel.UiState(dummyRoundWithPlayers),
            knockCounter = knockCounter
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun RoundScreenLightPreview() {
    RoundScreenPreviewContent(darkTheme = false, knockCounter = 0)
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun RoundScreenDarkPreview() {
    RoundScreenPreviewContent(darkTheme = true, knockCounter = 1)
}