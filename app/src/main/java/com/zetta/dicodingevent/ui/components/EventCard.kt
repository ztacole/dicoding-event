package com.zetta.dicodingevent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.zetta.dicodingevent.R
import com.zetta.dicodingevent.helper.DateFormatter

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    onEventClicked: (Int) -> Unit,
    onToggleFavoriteClicked: () -> Unit,
    eventId: Int,
    eventImageUrl: String,
    eventTitle: String,
    eventBeginTime: String,
    isFavorite: Boolean
) {
    Surface(
        modifier = modifier
            .clickable { onEventClicked(eventId) }
            .clip(MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(eventImageUrl)
                        .crossfade(400)
                        .build(),
                    contentDescription = eventTitle,
                    modifier = Modifier.size(144.dp),
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder),
                    contentScale = ContentScale.Crop,
                )
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clickable { onToggleFavoriteClicked() }
                        .clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_favorite
                            else R.drawable.ic_favorite_outline
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .height(144.dp)
                    .padding(bottom = 8.dp, top = 8.dp, end = 8.dp),
            ) {
                Text(
                    text = eventTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = DateFormatter.formatDateToString(eventBeginTime),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}