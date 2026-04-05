package com.zetta.dicodingevent.ui.screen.finished

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

class FinishedViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    var uiState by mutableStateOf(FinishedUiState())
        private set

    private val _searchQuery = MutableStateFlow("")

    init {
        observeFinishedEvents()
    }

    fun onUiAction(action: FinishedIntent) {
        when (action) {
            is FinishedIntent.SearchIntent -> {
                uiState = uiState.copy(searchQuery = action.query, isLoading = true, errorMessage = null)
                _searchQuery.value = action.query
            }
            is FinishedIntent.RefreshIntent, FinishedIntent.ClearSearchIntent -> refreshEvents()
            is FinishedIntent.ErrorDisplayed -> uiState = uiState.copy(errorMessage = null)
            is FinishedIntent.ToggleFavoriteIntent -> toggleFavorite(action.event)
        }
    }

    private fun refreshEvents() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, searchQuery = "", errorMessage = null)
            _searchQuery.value = ""

            try {
                eventRepository.refreshEvents(0)
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
    private fun observeFinishedEvents() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        eventRepository.getEvents(0)
                    } else {
                        eventRepository.searchEvents(0, query)
                            .catch {
                                uiState = uiState.copy(errorMessage = it.message)
                                emit(emptyList())
                            }
                    }
                }
                .collect { data ->
                    uiState = uiState.copy(
                        finishedEvents = data,
                        isInitialDataLoaded = data.isNotEmpty(),
                        isLoading = false
                    )
                }
        }
    }
}

interface FinishedIntent {
    data class SearchIntent(val query: String) : FinishedIntent
    object RefreshIntent : FinishedIntent
    object ClearSearchIntent : FinishedIntent
    object ErrorDisplayed : FinishedIntent
    data class ToggleFavoriteIntent(val event: EventEntity) : FinishedIntent
}

data class FinishedUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val finishedEvents: List<EventEntity> = emptyList(),
    val isInitialDataLoaded: Boolean = false,
    val searchQuery: String = ""
)