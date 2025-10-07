package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ToepenRepository
) : ViewModel() {

    data class UiState(
        val sessions: List<Session> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState(isLoading = true))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            repository.allSessions
                .collect { sessions ->
                    _uiState.value = UiState(sessions = sessions, isLoading = false)
                }
        }
    }

    fun addSessionAndNavigate(onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val newSession = Session(
                date = System.currentTimeMillis(),
                active = true
            )
            val newId = repository.insertSession(newSession).toInt()
            onCreated(newId)
        }
    }
}