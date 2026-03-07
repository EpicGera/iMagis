package com.example.imagis.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.example.imagis.data.AnimeEpisode
import com.example.imagis.ui.components.juicyBrutalistFocus
import com.example.imagis.ui.theme.BrutalistTheme
import com.example.imagis.ui.theme.DeepMatteBlack
import com.example.imagis.ui.theme.ElectricMagenta
import com.example.imagis.ui.theme.NeonLime
import com.example.imagis.ui.theme.PureWhite
import com.example.imagis.utils.JkanimeScraper
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
            Toast.makeText(this, "Inválid Series URL", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@AnimeSeriesActivity, "Failed to load episodes", Toast.LENGTH_SHORT).show()
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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMatteBlack),
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
                    model = seriesImageUrl,
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
                .background(PureWhite),
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
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = NeonLime,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Text(
                    text = "[ LOAD SEQUENCE IN PROGRESS ]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ElectricMagenta,
                )
            } else if (episodes.isEmpty()) {
                 Text(
                    text = "[ NO EPISODES FOUND ]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
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

// ── EPISODE ROW CARD ───────────────────────────────────────────

@Composable
private fun AnimeSeriesEpisodeCard(
    episode: AnimeEpisode,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .juicyBrutalistFocus()
            .clickable { onClick() }
            .background(DeepMatteBlack)
            .border(2.dp, PureWhite, RectangleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail (Left side)
        Box(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight()
                .background(Color(0xFF111111))
                .border(2.dp, PureWhite, RectangleShape),
        ) {
             AsyncImage(
                model = episode.imageUrl,
                contentDescription = episode.episodeNumber,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
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
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
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
                .background(ElectricMagenta)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "PLAY",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = PureWhite
            )
        }
    }
}
