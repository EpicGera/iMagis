// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/components/HeroSpotlight.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.ui.theme.FlixBlack
import com.epicgera.vtrae.ui.theme.FlixRed
import com.epicgera.vtrae.ui.theme.FlixWhite
import com.epicgera.vtrae.ui.theme.FlixGray
import kotlinx.coroutines.delay

// ── HERO SPOTLIGHT BANNER ──────────────────────────────────
// Auto-rotating featured poster with gradient overlay.
// Shows first 5 trending items as large backdrop images.

@Composable
fun HeroSpotlight(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (movies.isEmpty()) return

    val heroItems = movies.take(5)
    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto-advance every 6 seconds
    LaunchedEffect(heroItems.size) {
        while (true) {
            delay(6000)
            currentIndex = (currentIndex + 1) % heroItems.size
        }
    }

    val currentMovie = heroItems[currentIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onMovieClick(currentMovie) }
            .flixFocus(),
    ) {
        // Backdrop image with crossfade
        AnimatedContent(
            targetState = currentMovie,
            transitionSpec = {
                fadeIn(tween(800)) togetherWith fadeOut(tween(600))
            },
            label = "hero_crossfade",
        ) { movie ->
            AsyncImage(
                model = movie.fullBackdropUrl,
                contentDescription = movie.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Gradient overlay: bottom → top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            FlixBlack.copy(alpha = 0.6f),
                            FlixBlack.copy(alpha = 0.95f),
                        ),
                    )
                )
        )

        // Content at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
        ) {
            Text(
                text = currentMovie.displayTitle,
                color = FlixWhite,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Year + overview
            val year = currentMovie.displayYear
            if (year.isNotEmpty()) {
                Text(
                    text = year,
                    color = FlixGray,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Overview text
            if (currentMovie.overview.isNotEmpty()) {
                Text(
                    text = currentMovie.overview,
                    color = FlixWhite.copy(alpha = 0.8f),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Play button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(FlixRed)
                    .flixFocus()
                    .clickable { onMovieClick(currentMovie) }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "▶  PLAY",
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }

        // Page indicators (dots) at bottom-right
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            heroItems.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentIndex) 10.dp else 6.dp)
                        .background(
                            color = if (index == currentIndex) FlixRed else FlixWhite.copy(alpha = 0.4f),
                            shape = CircleShape,
                        )
                )
            }
        }
    }
}

