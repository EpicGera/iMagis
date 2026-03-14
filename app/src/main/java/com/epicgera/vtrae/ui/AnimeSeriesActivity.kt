package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.epicgera.vtrae.data.AnimeEpisode
import com.epicgera.vtrae.ui.components.juicyBrutalistFocus
import com.epicgera.vtrae.ui.theme.BrutalistTheme
import com.epicgera.vtrae.ui.theme.DeepMatteBlack
import com.epicgera.vtrae.ui.theme.ElectricMagenta
import com.epicgera.vtrae.ui.theme.NeonLime
import com.epicgera.vtrae.ui.theme.PureWhite
import com.epicgera.vtrae.utils.JkanimeScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Anime Series screen — Displays the poster and the list of episodes for an Anime Series.
 */
class AnimeSeriesActivity : ComponentActivity() {

    private var seriesUrl: String = ""
    private var seriesTitle: String = ""
    private var seriesType: String = ""
    private var seriesImageUrl: String = ""

    private val episodes = mutableStateListOf<AnimeEpisode>()
    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seriesUrl = intent.getStringExtra("SERIES_URL") ?: ""
        seriesTitle = intent.getStringExtra("SERIES_TITLE") ?: "UNKNOWN SERIES"
        seriesType = intent.getStringExtra("SERIES_TYPE") ?: "ANIME"
        seriesImageUrl = intent.getStringExtra("SERIES_IMAGE") ?: ""

        setContent {
            BrutalistTheme {
                AnimeSeriesScreen(
                    seriesTitle = seriesTitle,
                    seriesType = seriesType,
                    seriesImageUrl = seriesImageUrl,
                    episodes = episodes,
                    isLoading = isLoading,
                    onEpisodeClick = { episode -> handleEpisodeClick(episode) }
                )
            }
        }

        loadEpisodes()
    }

    private fun loadEpisodes() {
        if (seriesUrl.isEmpty()) {
            isLoading = false
            com.epicgera.vtrae.ui.components.VtrToastManager.showError("Inválid Series URL")
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fetchedEpisodes = JkanimeScraper.getSeriesEpisodes(seriesUrl)
                withContext(Dispatchers.Main) {
                    episodes.clear()
                    episodes.addAll(fetchedEpisodes)
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError("Failed to load episodes")
                }
            }
        }
    }

    private fun handleEpisodeClick(episode: AnimeEpisode) {
        val intent = Intent(this, AnimeDetailsActivity::class.java).apply {
            putExtra("ANIME_TITLE", seriesTitle)
            putExtra("ANIME_EPISODE", episode.episodeNumber)
            putExtra("ANIME_IMAGE", episode.imageUrl)
            putExtra("ANIME_URL", episode.episodeUrl)
        }
        startActivity(intent)
    }
}

// ══════════════════════════════════════════════════════════════
// ── COMPOSE UI ───────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════

@Composable
private fun AnimeSeriesScreen(
    seriesTitle: String,
    seriesType: String,
    seriesImageUrl: String,
    episodes: List<AnimeEpisode>,
    isLoading: Boolean,
    onEpisodeClick: (AnimeEpisode) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(DeepMatteBlack)) {
        if (seriesImageUrl.isNotEmpty()) {
            AsyncImage(
                model = seriesImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            )
        }
        
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
        // ── LEFT: SERIES INFO & POSTER ──────────────────────────────
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .border(2.dp, PureWhite, RectangleShape)
                    .background(Color(0xFF111111)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(seriesImageUrl)
                        .crossfade(true)
                        .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
                        .build(),
                    contentDescription = seriesTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(ElectricMagenta)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = seriesType.uppercase(),
                        color = PureWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = seriesTitle.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = PureWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── DIVIDER ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(PureWhite.copy(alpha = 0.3f)),
        )

        // ── RIGHT: EPISODE LIST ────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 24.dp, top = 32.dp, end = 24.dp),
        ) {
            Text(
                text = "[ EPISODES ]",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = NeonLime,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Text(
                    text = "[ LOAD SEQUENCE IN PROGRESS ]",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = ElectricMagenta,
                )
            } else if (episodes.isEmpty()) {
                 Text(
                    text = "[ NO EPISODES FOUND ]",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = ElectricMagenta,
                )
            } else {
                TvLazyColumn(
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(episodes, key = { it.episodeUrl }) { episode ->
                        AnimeSeriesEpisodeCard(
                            episode = episode,
                            onClick = { onEpisodeClick(episode) }
                        )
                    }
                }
            }
        }
    }
    }
}

// ── EPISODE ROW CARD ───────────────────────────────────────────

@Composable
private fun AnimeSeriesEpisodeCard(
    episode: AnimeEpisode,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan.copy(alpha = 0.20f) else DeepMatteBlack.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )
    val imageAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0.6f,
        label = "alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .scale(scale)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail (Left side)
        Box(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                .background(Color(0xFF111111)),
        ) {
             AsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(episode.imageUrl)
                    .crossfade(true)
                    .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
                    .build(),
                contentDescription = episode.episodeNumber,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(imageAlpha),
            )
        }

        // Details (Right side)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = episode.episodeNumber.uppercase(),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        
        // Action indicator
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .background(ElectricMagenta.copy(alpha = if (isFocused) 1f else 0.8f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "PLAY",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PureWhite
            )
        }
    }
}

