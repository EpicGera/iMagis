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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.api.StreamingPlatform
import com.epicgera.vtrae.api.TmdbApiClient
import com.epicgera.vtrae.ui.components.flixFocus
import com.epicgera.vtrae.ui.theme.FlixBlack
import com.epicgera.vtrae.ui.theme.FlixCardSurface
import com.epicgera.vtrae.ui.theme.FlixWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Platform brand colors for the selector buttons
private val platformColors = mapOf(
    StreamingPlatform.NETFLIX to Color(0xFFE50914),
    StreamingPlatform.AMAZON_PRIME to Color(0xFF00A8E1),
    StreamingPlatform.HULU to Color(0xFF1CE783),
    StreamingPlatform.APPLE_TV_PLUS to Color(0xFF555555),
    StreamingPlatform.DISNEY_PLUS to Color(0xFF113CCF),
    StreamingPlatform.MAX to Color(0xFF5822B4),
    StreamingPlatform.PARAMOUNT_PLUS to Color(0xFF0064FF),
    StreamingPlatform.PEACOCK to Color(0xFFFFC800),
    StreamingPlatform.CRUNCHYROLL to Color(0xFFF47521),
    StreamingPlatform.STARZ to Color(0xFF1A1A1A),
)

@Composable
fun PlatformCatalogScreen(
    onMovieClick: (Movie) -> Unit = {},
) {
    var selectedPlatform by remember { mutableStateOf(StreamingPlatform.NETFLIX) }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var series by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    var currentPage by remember { mutableIntStateOf(1) }
    var isFetchingNextPage by remember { mutableStateOf(false) }
    val gridState = rememberTvLazyGridState()

    // Fetch initial content when platform changes
    LaunchedEffect(selectedPlatform) {
        isLoading = true
        currentPage = 1
        movies = emptyList()
        series = emptyList()

        withContext(Dispatchers.IO) {
            val api = TmdbApiClient.service
            val key = TmdbApiClient.API_KEY
            val providerId = selectedPlatform.providerId.toString()

            try {
                val movieResponse = api.discoverMovies(
                    apiKey = key,
                    watchProviders = providerId,
                    watchRegion = "US",
                    page = currentPage
                )
                movies = movieResponse.results
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val tvResponse = api.discoverTv(
                    apiKey = key,
                    watchProviders = providerId,
                    watchRegion = "US",
                    page = currentPage
                )
                series = tvResponse.results
            } catch (e: Exception) {
                e.printStackTrace()
            }

            isLoading = false
        }
    }

    // Pagination logic
    val isScrolledToEnd by remember {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = movies.size + series.size
            // Trigger fetch when 6 items away from end of current list
            lastVisibleIndex != null && lastVisibleIndex >= totalItems - 6
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !isLoading && !isFetchingNextPage) {
            isFetchingNextPage = true
            currentPage += 1

            withContext(Dispatchers.IO) {
                val api = TmdbApiClient.service
                val key = TmdbApiClient.API_KEY
                val providerId = selectedPlatform.providerId.toString()

                try {
                    val movieResponse = api.discoverMovies(
                        apiKey = key,
                        watchProviders = providerId,
                        watchRegion = "US",
                        page = currentPage
                    )
                    movies = movies + movieResponse.results
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    val tvResponse = api.discoverTv(
                        apiKey = key,
                        watchProviders = providerId,
                        watchRegion = "US",
                        page = currentPage
                    )
                    series = series + tvResponse.results
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isFetchingNextPage = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ── HEADER ─────────────────────────────────────────────
        Text(
            text = "Browse by Platform",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = FlixWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 16.dp, start = 32.dp)
        )

        // ── PLATFORM SELECTOR ROW ──────────────────────────────
        TvLazyRow(
            contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            items(StreamingPlatform.entries.toList()) { platform ->
                PlatformButton(
                    platform = platform,
                    isSelected = platform == selectedPlatform,
                    onClick = { selectedPlatform = platform }
                )
            }
        }

        // ── CURRENT PLATFORM LABEL ─────────────────────────────
        Text(
            text = selectedPlatform.displayName,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = platformColors[selectedPlatform] ?: FlixWhite,
            modifier = Modifier.padding(bottom = 16.dp, start = 32.dp)
        )

        // ── CONTENT GRID ───────────────────────────────────────
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when {
                isLoading -> {
                    Text(
                        text = "Loading...",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = FlixWhite.copy(alpha = 0.6f),
                    )
                }
                movies.isEmpty() && series.isEmpty() -> {
                    Text(
                        text = "No content found",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = FlixWhite.copy(alpha = 0.6f),
                    )
                }
                else -> {
                    val allContent = movies + series
                    TvLazyVerticalGrid(
                        state = gridState,
                        columns = TvGridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 32.dp,
                            top = 8.dp,
                            end = 32.dp,
                            bottom = 32.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        items(allContent, key = { "${it.id}_${it.title ?: it.name}" }) { movie ->
                            PlatformPosterCard(
                                movie = movie,
                                onClick = { onMovieClick(movie) },
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── PLATFORM SELECTOR BUTTON ───────────────────────────────

@Composable
private fun PlatformButton(
    platform: StreamingPlatform,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val brandColor = platformColors[platform] ?: FlixWhite
    val bgColor = if (isSelected) brandColor else FlixCardSurface
    val txtColor = if (isSelected) {
        if (platform == StreamingPlatform.PEACOCK || platform == StreamingPlatform.HULU || platform == StreamingPlatform.CRUNCHYROLL)
            FlixBlack
        else FlixWhite
    } else FlixWhite

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .flixFocus()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = platform.displayName,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp,
            color = txtColor,
            maxLines = 1,
        )
    }
}

// ── POSTER CARD ────────────────────────────────────────────

@Composable
private fun PlatformPosterCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FlixCardSurface)
            .flixFocus()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(FlixCardSurface),
        ) {
            AsyncImage(
                model = movie.fullPosterUrl,
                contentDescription = movie.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Type badge
            val typeLabel = if (movie.title != null) "MOVIE" else "SERIES"
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(FlixBlack.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = typeLabel,
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = movie.displayTitle,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = FlixWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
        )
    }
}
