package com.zetta.dicodingevent.ui.screen.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zetta.dicodingevent.data.local.datastore.SettingPreferences
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.data.remote.response.Event
import com.zetta.dicodingevent.data.repository.EventRepository
import com.zetta.dicodingevent.ui.screen.setting.ThemeMode
import com.zetta.dicodingevent.ui.navigation.NavRoute
import com.zetta.dicodingevent.ui.screen.setting.toThemeMode
import kotlinx.coroutines.launch

class EventViewModel(
    private val eventRepository: EventRepository,
    private val prefs: SettingPreferences,
    val route: NavRoute.Event
): ViewModel() {

    var uiState by mutableStateOf(EventUiState())
        private set

    init {
        observeThemeMode()
        observeLocalEventDetail(route.id)
        fetchEventDetail(route.id)
    }

    fun onUiAction(action: EventIntent) {
        when (action) {
            is EventIntent.RefreshIntent -> fetchEventDetail(eventId = route.id)
            is EventIntent.ToggleFavoriteIntent -> toggleFavorite(action.event)
        }
    }

    private fun fetchEventDetail(eventId: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val response = eventRepository.getEventDetail(eventId)
                uiState = uiState.copy(event = response.event, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message, isLoading = false)
            }
        }
    }

    private fun observeLocalEventDetail(eventId: Int) {
        viewModelScope.launch {
            eventRepository.getFavoriteEvent(eventId).collect {
                uiState = uiState.copy(localEvent = it)
            }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            prefs.getThemeSetting().collect {
                uiState = uiState.copy(themeMode = it.toThemeMode())
            }
        }
    }

    private fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.toggleFavorite(event)
        }
    }
}

interface EventIntent {
    object RefreshIntent: EventIntent
    data class ToggleFavoriteIntent(val event: EventEntity): EventIntent
}

data class EventUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val event: Event? = null,
    val localEvent: EventEntity? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)