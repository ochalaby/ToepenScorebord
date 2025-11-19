package com.chalabysolutions.toepenscorebord.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.ui.navigation.Screen
import com.chalabysolutions.toepenscorebord.ui.theme.ToepenScorebordTheme
import com.chalabysolutions.toepenscorebord.util.DateTimeUtils
import com.chalabysolutions.toepenscorebord.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onAddSession = {
            // nieuwe sessie starten via repo
            viewModel.addSessionAndNavigate { newId ->
                navController.navigate(Screen.Session.createRoute(newId))
            }
        },
        onSessionClick = { sessionId ->
            navController.navigate(Screen.Session.createRoute(sessionId))
        },
        onSessionDeleteClick = { session ->
            viewModel.deleteSession(session)
        },
        onSettingsClicked = {
            navController.navigate((Screen.Setting.route))
        },
        onOverviewClicked = {
            navController.navigate(Screen.Overview.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeViewModel.UiState,
    onAddSession: () -> Unit = {},
    onSessionClick: (Int) -> Unit = {},
    onSessionDeleteClick: (Session) -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onOverviewClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kaartavonden") },
                actions = {
                    IconButton(onClick = onOverviewClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List, // of Storage/TableChart
                            contentDescription = "Database overzicht"
                        )
                    }
                    IconButton(onClick = onSettingsClicked) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Nieuwe sessie")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (uiState.sessions.isEmpty()) {
                    Text(
                        text = "Nog geen sessies gestart.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn {
                        items(uiState.sessions) { session ->
                            SessionCard(
                                session = session,
                                onClick = {
                                    onSessionClick(session.id)
                                },
                                onDeleteClick = {
                                    onSessionDeleteClick(session)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: Session,
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
                .padding(12.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val name = DateTimeUtils.formatDateToDayMonth(session.date)
                val status = if (session.active) "Actief" else "Afgerond"
                Text(
                    text = "$name ($status)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Gestart om: ${DateTimeUtils.formatTimeToSeconds(session.date)}",
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
            text = { Text("Weet je zeker dat je deze sessie wilt verwijderen? Deze actie kan niet ongedaan worden gemaakt.") },
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
fun HomeScreenPreviewContent(darkTheme: Boolean = false) {
    val fakeSessions = listOf(
        Session(id = 1, date = System.currentTimeMillis(), active = true),
        Session(id = 2, date = System.currentTimeMillis() - 86400000, active = false)
    )

    ToepenScorebordTheme(darkTheme = darkTheme) {
        HomeScreenContent(
            uiState = HomeViewModel.UiState(sessions = fakeSessions, isLoading = false)
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun HomeScreenPreviewLight() {
    HomeScreenPreviewContent(darkTheme = false)
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun HomeScreenPreviewDark() {
    HomeScreenPreviewContent(darkTheme = true)
}
