package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.ui.navigation.Screen
import com.chalabysolutions.toepenscorebord.ui.theme.ToepenScorebordTheme
import com.chalabysolutions.toepenscorebord.util.DateTimeUtils
import com.chalabysolutions.toepenscorebord.viewmodel.SessionViewModel

@Composable
fun SessionScreen(
    navController: NavHostController,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // Collect selectedPlayers uit SavedStateHandle (gewone List<Int>)
    val selectedPlayers by remember(savedStateHandle) {
        savedStateHandle?.getStateFlow("selectedPlayers", emptyList<Int>())
    }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

    // Voeg toe aan SessionViewModel zodra de lijst beschikbaar is
    LaunchedEffect(selectedPlayers) {
        if (selectedPlayers.isNotEmpty()) {
            viewModel.addPlayersToSession(selectedPlayers)
            savedStateHandle?.remove<List<Int>>("selectedPlayers") // reset zodat het niet opnieuw triggert
        }
    }

    SessionScreenContent(
        navController = navController,
        uiState = uiState,
        onRoundDeleteClick = { round ->
            viewModel.deleteRound(round)
        },
        onTogglePlayer = { playerId -> viewModel.togglePlayerActiveInSession(playerId) },
        onAddPlayer = {
            // Opslaan in SavedStateHandle als gewone ArrayList
            savedStateHandle?.set("selectedPlayers", ArrayList(uiState.players.map { it.player.id }))
            navController.navigate(Screen.Player.route)
        },
        onStartRound = {
            viewModel.startNewRoundAndNavigate { newId ->
                navController.navigate(Screen.Round.createRoute(newId))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreenContent(
    navController: NavHostController,
    uiState: SessionViewModel.UiState,
    onRoundDeleteClick: (Round) -> Unit = {},
    onTogglePlayer: (Int) -> Unit,
    onAddPlayer: () -> Unit = {},
    onStartRound: () -> Unit = {}
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text("Sessie") },
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
        ) },
        floatingActionButton = {
            if (uiState.session?.active == true) {
                FloatingActionButton(onClick = onStartRound) {
                    Icon(Icons.Default.Add, contentDescription = "Nieuwe ronde")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column{
                // spelers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Spelers", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = onAddPlayer) {
                        Text("Spelers toevoegen")
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(uiState.players) { playerSel ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTogglePlayer(playerSel.player.id) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = playerSel.isActive,
                                onCheckedChange = { onTogglePlayer(playerSel.player.id) }
                            )
                            Text(playerSel.player.name, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // rondes
                Text("Rondes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Top
                )  {
                    items(uiState.rounds) { round ->
                        RoundCard(
                            round = round,
                            onClick = {
                                 navController.navigate(Screen.Round.createRoute(round.id))
                            },
                            onDeleteClick = {
                                onRoundDeleteClick(round)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoundCard(
    round: Round,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val showDeleteDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ronde ${round.roundNumber}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (round.winnerId == null) "Actief" else "Afgerond",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Max punten: ${round.maxPoints}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = {
                showDeleteDialog.value = true
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Verwijder sessie"
                )
            }
        }
    }

    // Bevestigingsdialoog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Verwijderen?") },
            text = { Text("Weet je zeker dat je deze ronde wilt verwijderen? Deze actie kan niet ongedaan worden gemaakt.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()      // Pas de delete toe
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Ja, verwijderen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Annuleren")
                }
            }
        )
    }


}

@Composable
fun SessionScreenPreviewContent(darkTheme: Boolean = false) {
    val navController = rememberNavController()
    val dummySession = Session(id = 1, date = System.currentTimeMillis(), active = true)
    val dummyPlayers = listOf(
        SessionViewModel.PlayerSelection(player = Player(id = 1, name = "Jan"), isActive = true),
        SessionViewModel.PlayerSelection(player = Player(id = 2, name = "Piet"), isActive = true),
        SessionViewModel.PlayerSelection(player = Player(id = 3, name = "Anna"), isActive = false)
    )
    val dummyRounds = listOf(
        Round(id = 10, roundNumber =1, sessionId = 1, active = false, winnerId = 1),
        Round(id = 11, roundNumber =2, sessionId = 1, active = false, winnerId = 1),
        Round(id = 12, roundNumber =3, sessionId = 1, active = false, winnerId = null)
    )

    ToepenScorebordTheme(darkTheme = darkTheme) {
        SessionScreenContent(
            navController = navController,
            uiState = SessionViewModel.UiState(
                session = dummySession,
                players = dummyPlayers,
                rounds = dummyRounds,
                isLoading = false
            ),
            onTogglePlayer = {}
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun SessionScreenLightPreview() {
    SessionScreenPreviewContent(darkTheme = false)
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun SessionScreenDarkPreview() {
    SessionScreenPreviewContent(darkTheme = true)
}