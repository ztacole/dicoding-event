package com.zetta.dicodingevent.ui.screen.favorite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    var uiState by mutableStateOf(FavoriteUiState())
        private set

    init {
        observeFavorites()
    }

    fun onUiAction(action: FavoriteIntent) {
        when (action) {
            is FavoriteIntent.ToggleFavoriteIntent -> toggleFavorite(action.event)
            is FavoriteIntent.RefreshIntent -> observeFavorites()
        }
    }

    private fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.toggleFavorite(event)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            try {
                eventRepository.getFavoriteEvents()
                    .distinctUntilChanged()
                    .collect {
                        uiState = uiState.copy(
                            events = it,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message, isLoading = false)
            }
        }
    }
}

data class FavoriteUiState(
    val events: List<EventEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

interface FavoriteIntent {
    data class ToggleFavoriteIntent(val event: EventEntity) : FavoriteIntent
    object RefreshIntent : FavoriteIntent
}