package com.zetta.dicodingevent.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.ui.components.CarouselEventCard
import com.zetta.dicodingevent.ui.components.ErrorComponent
import com.zetta.dicodingevent.ui.components.EventCard
import com.zetta.dicodingevent.ui.components.LoadingComponent
import com.zetta.dicodingevent.ui.theme.DicodingEventTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEventClicked: (Int) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onUiAction(HomeIntent.RefreshIntent)
    }

    HomeContent(
        uiState = viewModel.uiState,
        onUiAction = viewModel::onUiAction,
        onEventClicked = onEventClicked,
        modifier = Modifier.statusBarsPadding(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onUiAction: (HomeIntent) -> Unit,
    onEventClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null && uiState.isInitialDataLoaded) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
            onUiAction(HomeIntent.ErrorDisplayed)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            overscrollEffect = null
        ) {
            item {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                )
            }

            if (uiState.isLoading && !uiState.isInitialDataLoaded) {
                item {
                    LoadingComponent(modifier = Modifier.fillParentMaxHeight(0.8f))
                }
            } else if (uiState.errorMessage != null && !uiState.isInitialDataLoaded) {
                item {
                    ErrorComponent(
                        message = uiState.errorMessage,
                        showRetry = true,
                        onRetryClicked = { onUiAction(HomeIntent.RefreshIntent) },
                        modifier = Modifier.fillParentMaxHeight(0.8f)
                    )
                }
            } else {
                if (uiState.upcomingEvents.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.home_upcoming_section),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        val carouselState =
                            rememberCarouselState { uiState.upcomingEvents.size }
                        HorizontalCenteredHeroCarousel(
                            state = carouselState,
                            modifier = Modifier.fillMaxWidth(),
                            itemSpacing = 8.dp
                        ) { index ->
                            val shape = this.rememberMaskShape(MaterialTheme.shapes.large)

                            uiState.upcomingEvents.getOrNull(index)?.let { event ->
                                CarouselEventCard(
                                    modifier = Modifier
                                        .height(196.dp)
                                        .clip(shape),
                                    onEventClicked = onEventClicked,
                                    eventId = event.id,
                                    eventImageUrl = event.mediaCover,
                                    eventTitle = event.name
                                )
                            }
                        }
                    }
                }

                if (uiState.finishedEvents.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.home_finished_section),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    items(items = uiState.finishedEvents, key = { it.id }) { event ->
                        EventCard(
                            modifier = Modifier.fillMaxWidth(),
                            onEventClicked = onEventClicked,
                            onToggleFavoriteClicked = { onUiAction(HomeIntent.ToggleFavoriteIntent(event)) },
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
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    DicodingEventTheme {
        HomeContent(
            uiState = HomeUiState(
                upcomingEvents = listOf(
                    EventEntity(
                        id = 1,
                        name = "Event 1",
                        mediaCover = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/event/dos-career_session_android_memulai_karir_sebagai_android_developer_logo_150524144219.png",
                        beginTime = "2021-03-25 10:25:43",
                        active = 1,
                        isFavorite = false,
                        updatedAt = System.currentTimeMillis()
                    )
                ),
                finishedEvents = listOf(
                    EventEntity(
                        id = 2,
                        name = "Event 2",
                        mediaCover = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/event/dos-career_session_android_memulai_karir_sebagai_android_developer_logo_150524144219.png",
                        beginTime = "2021-03-25 10:25:43",
                        active = 0,
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
