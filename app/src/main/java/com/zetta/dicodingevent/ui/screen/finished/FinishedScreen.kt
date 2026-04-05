package com.zetta.dicodingevent.ui.screen.finished

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.ui.components.ErrorComponent
import com.zetta.dicodingevent.ui.components.EventCard
import com.zetta.dicodingevent.ui.components.EventSearchBar
import com.zetta.dicodingevent.ui.components.LoadingComponent
import com.zetta.dicodingevent.ui.theme.DicodingEventTheme

@Composable
fun FinishedScreen(
    viewModel: FinishedViewModel,
    onEventClicked: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        if (viewModel.uiState.searchQuery.isEmpty()) viewModel.onUiAction(FinishedIntent.RefreshIntent)
    }

    FinishedContent(
        uiState = viewModel.uiState,
        onUiAction = viewModel::onUiAction,
        onEventClicked = onEventClicked,
        modifier = Modifier.statusBarsPadding(),
    )
}

@Composable
fun FinishedContent(
    uiState: FinishedUiState,
    onUiAction: (FinishedIntent) -> Unit,
    onEventClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null && uiState.isInitialDataLoaded) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
            onUiAction(FinishedIntent.ErrorDisplayed)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            overscrollEffect = null
        ) {
            item {
                EventSearchBar(
                    modifier = Modifier.padding(bottom = 12.dp),
                    query = uiState.searchQuery,
                    onQueryChange = { onUiAction(FinishedIntent.SearchIntent(it)) },
                    onClearQueryClicked = { onUiAction(FinishedIntent.ClearSearchIntent) }
                )
            }

            if (uiState.isLoading) {
                item {
                    LoadingComponent(
                        modifier = if (uiState.finishedEvents.isEmpty()) Modifier.fillParentMaxHeight(0.8f)
                        else Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            if (!uiState.isInitialDataLoaded && !uiState.isLoading) {
                item {
                    ErrorComponent(
                        message = uiState.errorMessage ?: stringResource(R.string.events_not_found),
                        showRetry = uiState.errorMessage != null && uiState.searchQuery.isEmpty(),
                        onRetryClicked = { onUiAction(FinishedIntent.RefreshIntent) },
                        modifier = Modifier.fillParentMaxHeight(0.8f)
                    )
                }
            } else {
                items(
                    items = uiState.finishedEvents,
                    key = { it.id }
                ) { event ->
                    EventCard(
                        modifier = Modifier.fillMaxWidth(),
                        onEventClicked = onEventClicked,
                        onToggleFavoriteClicked = { onUiAction(FinishedIntent.ToggleFavoriteIntent(event)) },
                        eventId = event.id,
                        eventImageUrl = event.mediaCover,
                        eventTitle = event.name,
                        eventBeginTime = event.beginTime,
                        isFavorite = event.isFavorite
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DicodingEventTheme {
        FinishedContent(
            uiState = FinishedUiState(
                finishedEvents = listOf(
                    EventEntity(
                        id = 1,
                        name = "Event 1",
                        mediaCover = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/event/dos-career_session_android_memulai_karir_sebagai_android_developer_logo_150524144219.png",
                        beginTime = "2021-03-25 10:25:43",
                        active = 1,
                        isFavorite = false,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            ),
            onUiAction = {},
            onEventClicked = {}
        )
    }
}