package com.zetta.dicodingevent.ui.screen.upcoming

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class UpcomingViewModel(
    private val eventRepository: EventRepository
): ViewModel() {

    var uiState by mutableStateOf(UpcomingUiState())
        private set

    private val _searchQuery = MutableStateFlow("")

    init {
        observeUpcomingEvents()
    }

    fun onUiAction(action: UpcomingIntent) {
        when (action) {
            is UpcomingIntent.SearchIntent -> {
                uiState = uiState.copy(searchQuery = action.query, isLoading = true, errorMessage = null)
                _searchQuery.value = action.query
            }
            is UpcomingIntent.RefreshIntent, UpcomingIntent.ClearSearchIntent -> refreshEvents()
            is UpcomingIntent.ErrorDisplayed -> uiState = uiState.copy(errorMessage = null)
            is UpcomingIntent.ToggleFavoriteIntent -> toggleFavorite(action.event)
        }
    }

    private fun refreshEvents() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, searchQuery = "", errorMessage = null)
            _searchQuery.value = ""

            try {
                eventRepository.refreshEvents(1)
                uiState = uiState.copy(errorMessage = null, isLoading = false)
            } catch (e: Exception) {
                delay(500L)
                uiState = uiState.copy(errorMessage = e.message, isLoading = false)
            }
        }
    }

    private fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.toggleFavorite(event)
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeUpcomingEvents() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        eventRepository.getEvents(1)
                    } else {
                        eventRepository.searchEvents(1, query)
                            .catch {
                                uiState = uiState.copy(errorMessage = it.message)
                                emit(emptyList())
                            }
                    }
                }
                .collect { data ->
                    uiState = uiState.copy(
                        upcomingEvents = data,
                        isInitialDataLoaded = data.isNotEmpty(),
                        isLoading = false
                    )
                }
        }
    }
}

interface UpcomingIntent {
    data class SearchIntent(val query: String): UpcomingIntent
    object RefreshIntent: UpcomingIntent
    object ClearSearchIntent: UpcomingIntent
    object ErrorDisplayed: UpcomingIntent
    data class ToggleFavoriteIntent(val event: EventEntity): UpcomingIntent
}

data class UpcomingUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val upcomingEvents: List<EventEntity> = emptyList(),
    val isInitialDataLoaded: Boolean = false,
    val searchQuery: String = ""
)