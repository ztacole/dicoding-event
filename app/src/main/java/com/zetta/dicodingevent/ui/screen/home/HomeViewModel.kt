package com.zetta.dicodingevent.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        observeEvents()
    }

    fun onUiAction(action: HomeIntent) {
        when (action) {
            is HomeIntent.RefreshIntent -> {
                refreshEvents()
                observeEvents()
            }
            is HomeIntent.ErrorDisplayed -> uiState = uiState.copy(errorMessage = null)
            is HomeIntent.ToggleFavoriteIntent -> toggleFavorite(action.event)
        }
    }

    private fun refreshEvents() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                eventRepository.refreshEvents(1)
                eventRepository.refreshEvents(0)
                uiState = uiState.copy(errorMessage = null, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message, isLoading = false)
            }
        }
    }

    private fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.toggleFavorite(event)
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            combine(
                eventRepository.getEvents(1, 5),
                eventRepository.getEvents(0, 5)
            ) { up, fin ->
                uiState.copy(
                    upcomingEvents = up,
                    finishedEvents = fin,
                    isInitialDataLoaded = up.isNotEmpty() || fin.isNotEmpty(),
                    isLoading = false
                )
            }.collect { uiState = it }
        }
    }
}

sealed interface HomeIntent {
    object RefreshIntent : HomeIntent
    object ErrorDisplayed : HomeIntent
    data class ToggleFavoriteIntent(val event: EventEntity) : HomeIntent
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val upcomingEvents: List<EventEntity> = listOf(),
    val finishedEvents: List<EventEntity> = listOf(),
    val isInitialDataLoaded: Boolean = false
)
