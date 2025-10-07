package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.relation.RoundPlayerWithPlayer
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithRounds
import com.chalabysolutions.toepenscorebord.ui.navigation.Screen
import com.chalabysolutions.toepenscorebord.ui.theme.ToepenScorebordTheme
import com.chalabysolutions.toepenscorebord.util.DateTimeUtils
import com.chalabysolutions.toepenscorebord.viewmodel.DatabaseOverviewViewModel

@Composable
fun DatabaseOverviewScreen(
    navController: NavHostController,
    viewModel: DatabaseOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DatabaseOverviewScreenContent(
        navController = navController,
        sessions = uiState.sessionsWithRounds,
        sessionsWithPlayers = uiState.sessionsWithPlayers,
        roundsWithPlayers = uiState.roundsWithPlayers
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseOverviewScreenContent(
    navController: NavHostController,
    sessions: List<SessionWithRounds>,
    sessionsWithPlayers: List<SessionWithPlayers>,
    roundsWithPlayers: List<RoundWithPlayers>,
    initiallyExpanded: Boolean = false
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Overzicht") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigeer expliciet naar Home en verwijder tussenliggende schermen
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug naar Home")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(sessions) { sessionWithRounds ->
                    SessionItem(
                        sessionWithRounds = sessionWithRounds,
                        players = sessionsWithPlayers.find { it.session.id == sessionWithRounds.session.id }?.players.orEmpty(),
                        roundsWithPlayers = roundsWithPlayers,
                        initiallyExpanded = initiallyExpanded
                    )
                }
            }
        }
    }
}

@Composable
fun SessionItem(
    sessionWithRounds: SessionWithRounds,
    players: List<Player>,
    roundsWithPlayers: List<RoundWithPlayers>,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Session ${sessionWithRounds.session.id} (${DateTimeUtils.formatDateToDayMonth(sessionWithRounds.session.date)})")
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("Players:", style = MaterialTheme.typography.bodyMedium)
                players.forEach { player ->
                    Text("- ${player.name}", modifier = Modifier.padding(start = 8.dp))
                }

                Text("Rounds:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                sessionWithRounds.rounds.forEach { round ->
                    val roundWithPlayers = roundsWithPlayers.find { it.round.id == round.id }
                    RoundItem(roundWithPlayers, initiallyExpanded)
                }
            }
        }
    }
}

@Composable
fun RoundItem(
    roundWithPlayers: RoundWithPlayers?,
    initiallyExpanded: Boolean = false) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Round ${roundWithPlayers?.round?.roundNumber} (maxPoints=${roundWithPlayers?.round?.maxPoints})")
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                roundWithPlayers?.players?.forEach { rp ->
                    Text(
                        "- ${rp.player.name}: ${rp.roundPlayer.points}p" +
                                if (rp.roundPlayer.eliminated) " (eliminated)" else ""
                    )
                }
            }
        }
    }
}


@Composable
fun DatabaseOverviewScreenPreviewContent(darkTheme: Boolean = false) {
    val navController = rememberNavController()

    // Fake spelers
    val player1 = Player(id = 1, name = "Alice")
    val player2 = Player(id = 2, name = "Bob")

    // Fake rondes
    val round1 = Round(id = 15, roundNumber = 1, sessionId = 1, maxPoints = 15, active = true)
    val round2 = Round(id = 16, roundNumber = 2, sessionId = 1, maxPoints = 10, active = false)

    val roundWithPlayers1 = RoundWithPlayers(
        round = round1,
        players = listOf(
            RoundPlayerWithPlayer(
                roundPlayer = RoundPlayer(id = 1, roundId = 1, playerId = 1, points = 10),
                player = player1
            ),
            RoundPlayerWithPlayer(
                roundPlayer = RoundPlayer(id = 2, roundId = 1, playerId = 2, points = 5),
                player = player2
            )
        )
    )

    val roundWithPlayers2 = RoundWithPlayers(
        round = round2,
        players = listOf()
    )

    val session = Session(id = 1, date = System.currentTimeMillis(), active = true)
    val sessions = listOf(SessionWithRounds(session = session, rounds = listOf(round1, round2)))
    val sessionsWithPlayers = listOf(SessionWithPlayers(session = session, players = listOf(player1, player2)))
    val roundsWithPlayers = listOf(roundWithPlayers1, roundWithPlayers2)

    ToepenScorebordTheme(darkTheme = darkTheme) {
        DatabaseOverviewScreenContent(
            navController = navController,
            sessions = sessions,
            sessionsWithPlayers = sessionsWithPlayers,
            roundsWithPlayers = roundsWithPlayers,
            initiallyExpanded = true
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun DatabaseOverviewScreenPreviewLight() {
    DatabaseOverviewScreenPreviewContent(darkTheme = false)
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun DatabaseOverviewScreenPreviewDark() {
    DatabaseOverviewScreenPreviewContent(darkTheme = true)
}
