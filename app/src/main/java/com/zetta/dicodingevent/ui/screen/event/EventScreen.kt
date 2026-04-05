package com.zetta.dicodingevent.ui.screen.event

import android.graphics.Color
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.data.remote.response.Event
import com.zetta.dicodingevent.helper.DateFormatter
import com.zetta.dicodingevent.ui.components.ErrorComponent
import com.zetta.dicodingevent.ui.components.LoadingComponent
import com.zetta.dicodingevent.ui.screen.setting.ThemeMode
import com.zetta.dicodingevent.ui.theme.DicodingEventTheme
import java.util.Date

@Composable
fun EventScreen(
    viewModel: EventViewModel,
    onBackClicked: () -> Unit
) {
    EventContent(
        uiState = viewModel.uiState,
        onUiAction = viewModel::onUiAction,
        onBackClicked = onBackClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventContent(
    uiState: EventUiState,
    onUiAction: (EventIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val currentDate = Date()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.event_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = topAppBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    uiState.event?.let { remoteEvent ->
                        val isFavorite = uiState.localEvent?.isFavorite ?: false
                        val date = DateFormatter.formatStringToDate(remoteEvent.beginTime)
                        val active = if (date < currentDate) 0 else 1

                        IconButton(
                            onClick = {
                                onUiAction(
                                    EventIntent.ToggleFavoriteIntent(
                                        uiState.localEvent ?: remoteEvent.toEntity(active, false)
                                    )
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isFavorite) R.drawable.ic_favorite
                                    else R.drawable.ic_favorite_outline
                                ),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, overscrollEffect = null)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (uiState.isLoading) {
                LoadingComponent(Modifier.fillMaxSize())
            } else if (uiState.errorMessage != null) {
                ErrorComponent(
                    message = uiState.errorMessage,
                    showRetry = true,
                    onRetryClicked = { onUiAction(EventIntent.RefreshIntent) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                uiState.event?.let {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uiState.event.mediaCover)
                                .crossfade(400)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                            placeholder = painterResource(R.drawable.img_placeholder),
                            error = painterResource(R.drawable.img_placeholder),
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val date = DateFormatter.formatStringToDate(uiState.event.beginTime)
                            val formattedDate = DateFormatter.formatDateToString(uiState.event.beginTime, "EEE, MMM dd yyyy | HH:mm")
                            val availableQuota = uiState.event.quota - uiState.event.registrants

                            val uriHandler = LocalUriHandler.current

                            Text(
                                text = uiState.event.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(
                                    R.string.event_organized_by,
                                    uiState.event.ownerName
                                ),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                                textAlign = TextAlign.Center
                            )

                            CurrentInformation(
                                date < currentDate,
                                formattedDate,
                                availableQuota
                            )

                            Spacer(Modifier.height(4.dp))

                            Badge(
                                modifier = Modifier.border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.shapes.small
                                ),
                                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = uiState.event.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            Text(
                                text = uiState.event.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(8.dp))

                            EventDescriptionHtml(uiState.event.description, uiState.themeMode)

                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = { uriHandler.openUri(uiState.event.link) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                                    .height(56.dp)
                            ) {
                                Text(text = stringResource(R.string.event_open_in_browser))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentInformation(
    isFinished: Boolean,
    formattedDate: String,
    availableQuota: Int,
) {
    if (isFinished) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.event_finished),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.event_open_until),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Center
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.event_available_quota),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Center
            )
            Text(
                text = "$availableQuota registrants",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun EventDescriptionHtml(
    htmlContent: String,
    themeMode: ThemeMode,
    modifier: Modifier = Modifier
) {
    val theme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val textColor = if (theme) "#EEEEEE" else "#333333"
    val bgColor = if (theme) "#121212" else "#FFFFFF"
    val accentColor = if (theme) "#BB86FC" else "#6200EE"

    val styledHtml = remember(htmlContent, theme) {
        """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: -apple-system, sans-serif; 
                    font-size: 14px; line-height: 1.6; 
                    color: $textColor; background-color: $bgColor;
                    margin: 0; padding: 0;
                }
                img { 
                    max-width: 100%; 
                    height: auto; 
                    border-radius: 12px;
                    margin: 12px 0; 
                }
                table { 
                    border-collapse: collapse;
                    width: 100%;
                    margin: 16px 0;
                    font-size: 12px; 
                }
                th, td { 
                    border: 1px solid #444;
                    padding: 10px;
                    text-align: left;
                }
                th { 
                    background-color: ${if (theme) "#222" else "#f2f2f2"}; 
                }
                blockquote { 
                    border-left: 4px solid $accentColor; 
                    padding: 8px 16px; margin: 16px 0;
                    background: ${if (theme) "#1A1A1A" else "#F9F9F9"};
                    font-style: italic; 
                }
                a { 
                    color: $accentColor; 
                    text-decoration: none; 
                    font-weight: bold; 
                }
            </style>
        </head>
        <body>$htmlContent</body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            WebView(ctx).apply {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(Color.TRANSPARENT)

                settings.apply {
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                    loadWithOverviewMode = true
                    useWideViewPort = true

                    javaScriptEnabled = false
                }

                setBackgroundColor(Color.TRANSPARENT)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "file:///android_asset/",
                styledHtml,
                "text/html",
                "utf-8",
                null
            )
        },
        onRelease = { webView ->
            webView.stopLoading()
            webView.destroy()
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EventContentPreview() {
    DicodingEventTheme {
        EventContent(
            uiState = EventUiState(
                event = Event(
                    id = 1,
                    name = "Sample Event",
                    summary = "This is a sample event summary.",
                    description = "<p>This is a sample event description.</p>",
                    imageLogo = "",
                    mediaCover = "",
                    category = "Technology",
                    ownerName = "Dicoding",
                    cityName = "Bandung",
                    quota = 100,
                    registrants = 50,
                    beginTime = "2024-12-31 23:59:59",
                    endTime = "2025-01-01 01:00:00",
                    link = "https://www.dicoding.com"
                )
            ),
            onUiAction = {},
            onBackClicked = {}
        )
    }
}
