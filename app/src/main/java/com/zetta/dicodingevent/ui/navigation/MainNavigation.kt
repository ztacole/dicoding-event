package com.zetta.dicodingevent.ui.navigation

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.ui.screen.event.EventScreen
import com.zetta.dicodingevent.ui.screen.event.EventViewModel
import com.zetta.dicodingevent.ui.screen.favorite.FavoriteScreen
import com.zetta.dicodingevent.ui.screen.favorite.FavoriteViewModel
import com.zetta.dicodingevent.ui.screen.finished.FinishedScreen
import com.zetta.dicodingevent.ui.screen.finished.FinishedViewModel
import com.zetta.dicodingevent.ui.screen.home.HomeScreen
import com.zetta.dicodingevent.ui.screen.home.HomeViewModel
import com.zetta.dicodingevent.ui.screen.setting.SettingScreen
import com.zetta.dicodingevent.ui.screen.setting.SettingViewModel
import com.zetta.dicodingevent.ui.screen.upcoming.UpcomingScreen
import com.zetta.dicodingevent.ui.screen.upcoming.UpcomingViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class BottomNavItem(
    @field:DrawableRes val selectedIcon: Int,
    @field:DrawableRes val unselectedIcon: Int,
    @field:StringRes val label: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: SettingViewModel) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(NavRoute.Home)
    val currentRoute = backStack.lastOrNull()

    val bottomNavItems = mapOf(
        NavRoute.Home to BottomNavItem(
            selectedIcon = R.drawable.ic_home,
            unselectedIcon = R.drawable.ic_home_outline,
            label = R.string.home
        ),
        NavRoute.Upcoming to BottomNavItem(
            selectedIcon = R.drawable.ic_event_upcoming,
            unselectedIcon = R.drawable.ic_event_upcoming_outline,
            label = R.string.upcoming
        ),
        NavRoute.Finished to BottomNavItem(
            selectedIcon = R.drawable.ic_event_finished,
            unselectedIcon = R.drawable.ic_event_finished_outline,
            label = R.string.finished
        ),
        NavRoute.Favorite to BottomNavItem(
            selectedIcon = R.drawable.ic_favorite,
            unselectedIcon = R.drawable.ic_favorite_outline,
            label = R.string.favorite
        ),
        NavRoute.Setting to BottomNavItem(
            selectedIcon = R.drawable.ic_setting,
            unselectedIcon = R.drawable.ic_setting_outline,
            label = R.string.setting
        )
    )

    val navigateToTab = { route: NavKey ->
        if (currentRoute != route) {
            val existingIndex = backStack.indexOf(route)

            if (existingIndex == -1) {
                backStack.add(route)
            } else {
                val entry = backStack.removeAt(existingIndex)
                backStack.add(entry)
            }
        }
    }
    val navigateUp: () -> Unit = {
        val activity = context as Activity

        when (currentRoute) {
            NavRoute.Home -> {
                activity.finish()
            }
            in bottomNavItems.keys -> {
                val index = backStack.indexOf(NavRoute.Home)
                if (index != -1) {
                    val entry = backStack.removeAt(index)
                    backStack.add(entry)
                } else {
                    backStack.add(NavRoute.Home)
                }
            }
            else -> {
                backStack.removeLastOrNull()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            if (currentRoute in bottomNavItems.keys) {
                NavigationBar {
                    bottomNavItems.map { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.key,
                            onClick = { navigateToTab(item.key) },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (currentRoute == item.key) item.value.selectedIcon
                                        else item.value.unselectedIcon
                                    ),
                                    contentDescription = stringResource(item.value.label)
                                )
                            },
                            label = { Text(text = stringResource(item.value.label)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .padding(innerPadding),
            backStack = backStack,
            onBack = navigateUp,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            predictivePopTransitionSpec = defaultPredictivePopTransitionSpec(),
            entryProvider = entryProvider {
                HomeEntry { id -> backStack.add(NavRoute.Event(id)) }
                UpcomingEntry { id -> backStack.add(NavRoute.Event(id)) }
                FinishedEntry { id -> backStack.add(NavRoute.Event(id)) }
                FavoriteEntry { id -> backStack.add(NavRoute.Event(id)) }
                SettingEntry(viewModel)
                EventEntry { backStack.removeLastOrNull() }
            }
        )
    }
}

@Composable
private fun EntryProviderScope<NavKey>.HomeEntry(onEventClicked: (Int) -> Unit) {
    entry<NavRoute.Home> {
        val viewModel: HomeViewModel = koinViewModel()
        HomeScreen(
            viewModel = viewModel,
            onEventClicked = onEventClicked
        )
    }
}

@Composable
private fun EntryProviderScope<NavKey>.UpcomingEntry(onEventClicked: (Int) -> Unit) {
    entry<NavRoute.Upcoming> {
        val viewModel: UpcomingViewModel = koinViewModel()
        UpcomingScreen(
            viewModel = viewModel,
            onEventClicked = onEventClicked
        )
    }
}

@Composable
private fun EntryProviderScope<NavKey>.FinishedEntry(onEventClicked: (Int) -> Unit) {
    entry<NavRoute.Finished> {
        val viewModel: FinishedViewModel = koinViewModel()
        FinishedScreen(
            viewModel = viewModel,
            onEventClicked = onEventClicked
        )
    }
}

@Composable
private fun EntryProviderScope<NavKey>.FavoriteEntry(onEventClicked: (Int) -> Unit) {
    entry<NavRoute.Favorite> {
        val viewModel: FavoriteViewModel = koinViewModel()
        FavoriteScreen(
            viewModel = viewModel,
            onEventClicked = onEventClicked
        )
    }
}

@Composable
private fun EntryProviderScope<NavKey>.SettingEntry(viewModel: SettingViewModel) {
    entry<NavRoute.Setting> {
        SettingScreen(viewModel)
    }
}

@Composable
private fun EntryProviderScope<NavKey>.EventEntry(onBackClicked: () -> Unit) {
    entry<NavRoute.Event>(
        metadata = NavDisplay.transitionSpec {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        } + NavDisplay.popTransitionSpec {
            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        } + NavDisplay.predictivePopTransitionSpec {
            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        }
    ) { route ->
        val viewModel: EventViewModel = koinViewModel { parametersOf(route) }
        EventScreen(
            viewModel = viewModel,
            onBackClicked = onBackClicked
        )
    }
}
