package com.zetta.dicodingevent.ui.screen.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.ui.components.ErrorComponent
import com.zetta.dicodingevent.ui.components.EventCard
import com.zetta.dicodingevent.ui.components.LoadingComponent

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    onEventClicked: (Int) -> Unit
) {
    FavoriteContent(
        uiState = viewModel.uiState,
        onUiAction = viewModel::onUiAction,
        onEventClicked = onEventClicked,
        modifier = Modifier.statusBarsPadding(),
    )
}

@Composable
private fun FavoriteContent(
    uiState: FavoriteUiState,
    onUiAction: (FavoriteIntent) -> Unit,
    onEventClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            overscrollEffect = null
        ) {
            item {
                Text(
                    text = stringResource(R.string.favorite),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                )
            }

            if (uiState.isLoading) {
                item {
                    LoadingComponent(Modifier.fillParentMaxHeight(0.8f))
                }
            }

            if (!uiState.isLoading && uiState.events.isEmpty()) {
                item {
                    ErrorComponent(
                        message = uiState.errorMessage ?: stringResource(R.string.favorite_empty_message),
                        showRetry = false,
                        onRetryClicked = {},
                        modifier = Modifier.fillParentMaxHeight(0.8f)
                    )
                }
            } else {
                items(
                    items = uiState.events,
                    key = { it.id }
                ) { event ->
                    EventCard(
                        modifier = Modifier.fillMaxWidth(),
                        onEventClicked = onEventClicked,
                        onToggleFavoriteClicked = { onUiAction(FavoriteIntent.ToggleFavoriteIntent(event)) },
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