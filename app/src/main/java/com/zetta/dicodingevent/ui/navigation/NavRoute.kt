package com.zetta.dicodingevent.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavRoute: NavKey {
    @Serializable data object Home: NavRoute
    @Serializable data object Upcoming: NavRoute
    @Serializable data object Finished: NavRoute
    @Serializable data class Event(val id: Int): NavRoute
    @Serializable data object Favorite: NavRoute
    @Serializable data object Setting: NavRoute
}