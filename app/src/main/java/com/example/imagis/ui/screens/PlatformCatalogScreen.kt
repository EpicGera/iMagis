// FILE_PATH: app/src/main/java/com/example/imagis/ui/screens/PlatformCatalogScreen.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.example.imagis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.example.imagis.api.Movie
import com.example.imagis.api.StreamingPlatform
import com.example.imagis.api.TmdbApiClient
import com.example.imagis.ui.components.juicyBrutalistFocus
import com.example.imagis.ui.theme.DeepMatteBlack
import com.example.imagis.ui.theme.ElectricMagenta
import com.example.imagis.ui.theme.NeonLime
import com.example.imagis.ui.theme.PureWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ── PLATFORM CATALOG SCREEN ────────────────────────────────
//
//  Layout:
//   ┌─────────────────────────────────────────────────────────┐
//   │  [ BROWSE BY PLATFORM ]                                │
//   │                                                         │
//   │  [NETFLIX] [PRIME] [HULU] [APPLE] [DISNEY+] [MAX] ...  │
//   │                                                         │
//   │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐           │
//   │  │    │ │    │ │    │ │    │ │    │ │    │           │
//   │  └────┘ └────┘ └────┘ └────┘ └────┘ └────┘           │
//   │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐           │
//   │  │    │ │    │ │    │ │    │ │    │ │    │           │
//   │  └────┘ └────┘ └────┘ └────┘ └────┘ └────┘           │
//   └─────────────────────────────────────────────────────────┘

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

    // Fetch content when platform changes
    LaunchedEffect(selectedPlatform) {
        isLoading = true
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
                    watchRegion = "US"
                )
                movies = movieResponse.results
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val tvResponse = api.discoverTv(
                    apiKey = key,
                    watchProviders = providerId,
                    watchRegion = "US"
                )
                series = tvResponse.results
            } catch (e: Exception) {
                e.printStackTrace()
            }

            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ── HEADER ─────────────────────────────────────────────
        Text(
            text = "[ BROWSE BY PLATFORM ]",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp,
            color = PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ── PLATFORM SELECTOR ROW ──────────────────────────────
        TvLazyRow(
            contentPadding = PaddingValues(end = 32.dp),
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
            text = "[ ${selectedPlatform.displayName.uppercase()} ]",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = platformColors[selectedPlatform] ?: NeonLime,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ── CONTENT GRID ───────────────────────────────────────
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when {
                isLoading -> {
                    Text(
                        text = "[ LOADING... ]",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = ElectricMagenta,
                    )
                }
                movies.isEmpty() && series.isEmpty() -> {
                    Text(
                        text = "[ NO CONTENT FOUND ]",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = PureWhite,
                    )
                }
                else -> {
                    val allContent = movies + series
                    TvLazyVerticalGrid(
                        columns = TvGridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            end = 24.dp,
                            bottom = 32.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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
    val brandColor = platformColors[platform] ?: PureWhite
    val bgColor = if (isSelected) brandColor else Color.Transparent
    val txtColor = if (isSelected) {
        // Dark text on bright backgrounds, white on dark
        if (platform == StreamingPlatform.PEACOCK || platform == StreamingPlatform.HULU || platform == StreamingPlatform.CRUNCHYROLL)
            DeepMatteBlack
        else PureWhite
    } else PureWhite

    Box(
        modifier = Modifier
            .juicyBrutalistFocus()
            .clickable { onClick() }
            .border(
                width = 3.dp,
                color = if (isSelected) NeonLime else brandColor,
                shape = RectangleShape
            )
            .background(bgColor)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "[ ${platform.displayName.uppercase()} ]",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
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
            .juicyBrutalistFocus()
            .clickable { onClick() }
            .background(DeepMatteBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .border(
                    width = 2.dp,
                    color = PureWhite,
                    shape = RectangleShape,
                )
                .background(Color(0xFF111111)),
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
                    .background(if (movie.title != null) ElectricMagenta else NeonLime)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = typeLabel,
                    color = DeepMatteBlack,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = movie.displayTitle.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        )
    }
}
