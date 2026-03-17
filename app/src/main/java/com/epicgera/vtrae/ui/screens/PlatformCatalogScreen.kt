package com.epicgera.vtrae.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
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

// ── Platform brand colors ──────────────────────────────────
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

// ── Content type filter ────────────────────────────────────
private enum class ContentType(val label: String) {
    ALL("All"),
    MOVIES("Movies"),
    SERIES("Series"),
}

// ── Genre filter (TMDB IDs for both Movie & TV) ────────────
private data class GenreFilter(val label: String, val movieIds: List<Int>, val tvIds: List<Int>)

private val genreFilters = listOf(
    GenreFilter("All", emptyList(), emptyList()),
    GenreFilter("Action", listOf(28), listOf(10759)),
    GenreFilter("Comedy", listOf(35), listOf(35)),
    GenreFilter("Drama", listOf(18), listOf(18)),
    GenreFilter("Horror", listOf(27), listOf()),
    GenreFilter("Sci-Fi", listOf(878), listOf(10765)),
    GenreFilter("Thriller", listOf(53), listOf()),
    GenreFilter("Romance", listOf(10749), listOf()),
    GenreFilter("Animation", listOf(16), listOf(16)),
    GenreFilter("Documentary", listOf(99), listOf(99)),
    GenreFilter("Crime", listOf(80), listOf(80)),
    GenreFilter("Mystery", listOf(9648), listOf(9648)),
    GenreFilter("Fantasy", listOf(14), listOf(10765)),
    GenreFilter("Family", listOf(10751), listOf(10751)),
)

// ── MAIN SCREEN ────────────────────────────────────────────

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

    // Filter states (no search — Fire TV has no keyboard)
    var selectedType by remember { mutableStateOf(ContentType.ALL) }
    var selectedGenre by remember { mutableStateOf(genreFilters.first()) }

    // Fetch initial 5 pages (~200 titles) when platform changes
    LaunchedEffect(selectedPlatform) {
        isLoading = true
        currentPage = 5
        movies = emptyList()
        series = emptyList()
        selectedType = ContentType.ALL
        selectedGenre = genreFilters.first()

        withContext(Dispatchers.IO) {
            val api = TmdbApiClient.service
            val key = TmdbApiClient.API_KEY
            val providerId = selectedPlatform.providerId.toString()
            val movieResults = mutableListOf<Movie>()
            val seriesResults = mutableListOf<Movie>()

            for (pg in 1..5) {
                try {
                    val movieResponse = api.discoverMovies(
                        apiKey = key,
                        watchProviders = providerId,
                        watchRegion = "US",
                        page = pg
                    )
                    movieResults.addAll(movieResponse.results)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    val tvResponse = api.discoverTv(
                        apiKey = key,
                        watchProviders = providerId,
                        watchRegion = "US",
                        page = pg
                    )
                    seriesResults.addAll(tvResponse.results)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            movies = movieResults
            series = seriesResults
            isLoading = false
        }
    }

    // ── Derived filtered content ────────────────────────────
    val filteredContent by remember(movies, series, selectedType, selectedGenre) {
        derivedStateOf {
            val pool: List<Movie> = when (selectedType) {
                ContentType.ALL -> movies + series
                ContentType.MOVIES -> movies
                ContentType.SERIES -> series
            }

            if (selectedGenre.label == "All") pool
            else {
                val allGenreIds = (selectedGenre.movieIds + selectedGenre.tvIds).toSet()
                pool.filter { movie ->
                    movie.genre_ids?.any { it in allGenreIds } == true
                }
            }
        }
    }

    // Pagination logic
    val isScrolledToEnd by remember {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = filteredContent.size
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

    // ── UI LAYOUT ───────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        // ── PLATFORM SELECTOR ROW (compact, with inline title) ─
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Platform",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = FlixWhite,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        TvLazyRow(
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            items(StreamingPlatform.entries.toList()) { platform ->
                PlatformChip(
                    platform = platform,
                    isSelected = platform == selectedPlatform,
                    onClick = { selectedPlatform = platform }
                )
            }
        }

        // ── COMBINED FILTER ROW: type + divider + genre ─────
        TvLazyRow(
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            // Type chips
            items(ContentType.entries.toList()) { type ->
                FilterChip(
                    label = type.label,
                    isSelected = type == selectedType,
                    accentColor = platformColors[selectedPlatform] ?: FlixWhite,
                    onClick = { selectedType = type },
                )
            }

            // Visual divider
            item {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(FlixWhite.copy(alpha = 0.15f))
                )
            }

            // Genre chips
            items(genreFilters) { genre ->
                FilterChip(
                    label = genre.label,
                    isSelected = genre == selectedGenre,
                    accentColor = platformColors[selectedPlatform] ?: FlixWhite,
                    onClick = { selectedGenre = genre },
                )
            }
        }

        // ── CONTENT GRID ─────────────────────────────────────
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when {
                isLoading -> {
                    Text(
                        text = "Loading…",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = FlixWhite.copy(alpha = 0.6f),
                    )
                }
                filteredContent.isEmpty() -> {
                    Text(
                        text = "No content found",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = FlixWhite.copy(alpha = 0.6f),
                    )
                }
                else -> {
                    TvLazyVerticalGrid(
                        state = gridState,
                        columns = TvGridCells.Fixed(6),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            top = 4.dp,
                            end = 24.dp,
                            bottom = 24.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(filteredContent, key = { "${it.id}_${it.title ?: it.name}" }) { movie ->
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

// ── FILTER CHIP ────────────────────────────────────────────

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) accentColor else FlixCardSurface
    val txtColor = if (isSelected) {
        if (accentColor == Color(0xFF1CE783) || accentColor == Color(0xFFFFC800) || accentColor == Color(0xFFF47521))
            FlixBlack
        else FlixWhite
    } else FlixWhite.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .flixFocus()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            color = txtColor,
            maxLines = 1,
        )
    }
}

// ── PLATFORM CHIP (compact — icon + short name) ────────────

@Composable
private fun PlatformChip(
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

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .flixFocus()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = platform.iconRes),
            contentDescription = platform.displayName,
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(3.dp)),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = platform.displayName,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = txtColor,
            maxLines = 1,
        )
    }
}

// ── POSTER CARD (compact — fills column width) ─────────────

@Composable
private fun PlatformPosterCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(FlixCardSurface)
            .flixFocus()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                    .padding(4.dp)
                    .background(FlixBlack.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = typeLabel,
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 8.sp
                )
            }
        }

        Text(
            text = movie.displayTitle,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = FlixWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
        )
    }
}
