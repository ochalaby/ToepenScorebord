package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.ui.theme.ToepenScorebordTheme
import com.chalabysolutions.toepenscorebord.viewmodel.PlayerViewModel
import kotlin.Int

@Composable
fun PlayersScreen(
    navController: NavController,
    viewModel: PlayerViewModel
) {
    var newPlayerName by remember { mutableStateOf("") }
    val showInactive by viewModel.showInactive.collectAsState()
    val players by viewModel.allPlayers.collectAsState()
    val canDeleteMap by viewModel.canDeleteMap.collectAsState()

    // --- Read selectedPlayers from the previous backStackEntry.savedStateHandle ---
    val prevSavedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val selectedPlayersFromNav by remember(prevSavedStateHandle) {
        prevSavedStateHandle?.getStateFlow("selectedPlayers", emptyList<Int>())
    }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<Int>()) }

    // Lokale UI-state als mutableStateListOf
    val selectedPlayerIds = remember { mutableStateListOf<Int>() }

    // Init met geselecteerde spelers vanuit SavedStateHandle
    LaunchedEffect(selectedPlayersFromNav) {
        selectedPlayerIds.clear()
        selectedPlayerIds.addAll(selectedPlayersFromNav)
    }

    PlayersScreenContent(
        navController = navController,
        newPlayerName = newPlayerName,
        onNewPlayerNameChange = { newPlayerName = it },
        players = players,
        selectedPlayerIds = selectedPlayerIds,
        showInactive = showInactive,
        onToggleShowInactive = { viewModel.toggleShowInactive() },
        onAddNewPlayer = {
            if (newPlayerName.isNotBlank()) {
                viewModel.createNewPlayer(newPlayerName)
                newPlayerName = ""
            }
        },
        onTogglePlayerSelection = { playerId ->
            if (selectedPlayerIds.contains(playerId)) {
                selectedPlayerIds.remove(playerId)
            } else {
                selectedPlayerIds.add(playerId)
            }
        },
        onConfirmSelection = {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("selectedPlayers", ArrayList(selectedPlayerIds))
            navController.popBackStack()
        },
        onTogglePlayerActive = { playerId -> viewModel.togglePlayerActive(playerId) },
        onDeletePlayer = { player -> viewModel.deletePlayerIfUnused(player.id) },
        canDelete = {player -> canDeleteMap[player.id] == true}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreenContent(
    navController: NavController,
    newPlayerName: String,
    onNewPlayerNameChange: (String) -> Unit,
    players: List<Player>,
    selectedPlayerIds: List<Int>,
    showInactive: Boolean,
    onToggleShowInactive: () -> Unit,
    onAddNewPlayer: () -> Unit,
    onTogglePlayerSelection: (Int) -> Unit,
    onConfirmSelection: () -> Unit,
    onTogglePlayerActive: (Int) -> Unit,
    onDeletePlayer: (Player) -> Unit,
    canDelete: (Player) -> Boolean
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Selecteer spelers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .imePadding()
        ) {
            // Nieuw speler toevoegen
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = newPlayerName,
                    onValueChange = onNewPlayerNameChange,
                    placeholder = { Text("Nieuwe speler naam") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // laat "Done" zien in plaats van Enter
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newPlayerName.isNotBlank()) {
                                onAddNewPlayer()
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAddNewPlayer) {
                    Text("Aanmaken")
                }
            }

            // Toggle actieve/inactieve spelers
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onToggleShowInactive() }
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Checkbox(
//                    checked = showInactive,
//                    onCheckedChange = { onToggleShowInactive() }
//                )
//                Text("Toon ook inactieve spelers")
//            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toon ook inactieve spelers",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showInactive,
                    onCheckedChange = { onToggleShowInactive() }
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            LazyColumn {
                items(players) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePlayerSelection(player.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedPlayerIds.contains(player.id),
                            onCheckedChange = { onTogglePlayerSelection(player.id) }
                        )
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Text(if (player.active) "Actief" else "Inactief")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = player.active,
                            onCheckedChange = { onTogglePlayerActive(player.id) }
                        )

                        // Delete icon
                        IconButton(
                            onClick = { onDeletePlayer(player) },
                            enabled = canDelete(player)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Verwijder speler")
                        }
                    }
                }
            }

            // Knop om spelers toe te voegen
            Button(
                onClick = onConfirmSelection,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Bevestig spelers lijst")
            }
        }
    }
}

@Composable
fun PlayersScreenPreviewContent(darkTheme: Boolean = false) {
    val navController = rememberNavController()
    val dummyPlayers = listOf(
        Player(id = 1, name = "Jan"),
        Player(id = 2, name = "Piet"),
        Player(id = 3, name = "Anna", active = false)
    )

    val dummySelectedPlayerIds = listOf (1,2)

    // Dummy map om te laten zien welke spelers verwijderd kunnen worden
    val dummyCanDeleteMap = mapOf(
        1 to false,
        2 to false,
        3 to true
    )

    ToepenScorebordTheme(darkTheme = darkTheme) {
        PlayersScreenContent(
            navController = navController,
            newPlayerName = "Henk",
            onNewPlayerNameChange = {},
            players = dummyPlayers,
            selectedPlayerIds = dummySelectedPlayerIds,
            showInactive = true,
            onToggleShowInactive = {},
            onAddNewPlayer = {},
            onTogglePlayerSelection = {},
            onConfirmSelection = {},
            onTogglePlayerActive = {},
            onDeletePlayer = {},
            canDelete = { player -> dummyCanDeleteMap[player.id] == true }
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PlayerScreenLightPreview() {
    PlayersScreenPreviewContent(darkTheme = false)
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PlayerScreenDarkPreview() {
    PlayersScreenPreviewContent(darkTheme = true)
}