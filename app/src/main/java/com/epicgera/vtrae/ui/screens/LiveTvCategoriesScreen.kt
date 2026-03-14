// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/screens/LiveTvCategoriesScreen.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import coil.compose.SubcomposeAsyncImage
import com.epicgera.vtrae.data.IptvChannel
import com.epicgera.vtrae.data.PlaylistSource
import com.epicgera.vtrae.ui.components.PulsingFlixLoader
import com.epicgera.vtrae.ui.components.flixFocus
import com.epicgera.vtrae.ui.theme.FlixBlack
import com.epicgera.vtrae.ui.theme.FlixCardSurface
import com.epicgera.vtrae.ui.theme.FlixRed
import com.epicgera.vtrae.ui.theme.FlixWhite
import androidx.compose.ui.res.stringResource
import com.epicgera.vtrae.R

@Composable
fun LiveTvCategoriesScreen(
    title: String,
    playlists: List<PlaylistSource> = emptyList(),
    channels: List<IptvChannel> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onPlaylistClick: (PlaylistSource) -> Unit = {},
    onChannelClick: (IptvChannel) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FlixBlack)
            .padding(top = 32.dp, start = 32.dp, end = 32.dp)
    ) {
        // ── FLIX HEADER ─────────────────────────────────────
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            color = FlixWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ── STATES ─────────────────────────────────────────────
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PulsingFlixLoader(message = stringResource(R.string.loading_channels))
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.error_loading_channels, errorMessage ?: ""),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = FlixRed,
                        textAlign = TextAlign.Center
                    )
                }
            }
            playlists.isNotEmpty() -> {
                TvLazyVerticalGrid(
                    columns = TvGridCells.Adaptive(minSize = 240.dp),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlists) { playlist ->
                        CategoryCard(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) }
                        )
                    }
                }
            }
            channels.isNotEmpty() -> {
                TvLazyVerticalGrid(
                    columns = TvGridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(channels) { channel ->
                        ChannelCard(
                            channel = channel,
                            onClick = { onChannelClick(channel) }
                        )
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_content_found),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = FlixWhite
                    )
                }
            }
        }
    }
}

// ── CATEGORY CARD ───────────────────────────────────────────

@Composable
private fun CategoryCard(
    playlist: PlaylistSource,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .flixFocus()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(FlixCardSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = playlist.name.uppercase(),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = FlixWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ── CHANNEL CARD ────────────────────────────────────────────

@Composable
private fun ChannelCard(
    channel: IptvChannel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .flixFocus()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(FlixCardSurface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(FlixBlack.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (channel.logoUrl.isNullOrBlank()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = channel.name,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FlixWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                SubcomposeAsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Fit, // Fit for logos so they aren't cropped
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("...", color = FlixWhite.copy(alpha = 0.3f))
                        }
                    },
                    error = {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = channel.name,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = FlixWhite.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                )
            }
            
            // Fallback badge — indicates backup streams are available
            if (channel.fallbackUrls.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.sources_count, channel.fallbackUrls.size + 1),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = FlixWhite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(FlixRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FlixCardSurface)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.name,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = FlixWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

