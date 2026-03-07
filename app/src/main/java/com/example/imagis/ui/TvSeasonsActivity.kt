// FILE_PATH: app/src/main/java/com/example/imagis/ui/TvSeasonsActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.example.imagis.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.itemsIndexed
import coil.compose.AsyncImage
import com.example.imagis.api.Episode
import com.example.imagis.api.Season
import com.example.imagis.api.TmdbApiClient
import com.example.imagis.ui.components.juicyBrutalistFocus
import com.example.imagis.ui.theme.BrutalistTheme
import com.example.imagis.ui.theme.DeepMatteBlack
import com.example.imagis.ui.theme.ElectricMagenta
import com.example.imagis.ui.theme.NeonLime
import com.example.imagis.ui.theme.PureWhite
import com.example.imagis.utils.MediaScraperEngine
import androidx.compose.ui.res.stringResource
import com.example.imagis.R
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TV Seasons screen — Compose for TV with Brutalist theme.
 * Replaces FragmentActivity + TvSeasonsFragment.
 */
class TvSeasonsActivity : ComponentActivity(), TorrentListener {

    private var tvShowId: Int = 0
    private var tvShowName: String = ""

    // ── Compose observable state ───────────────────────────
    private val seasons = mutableStateListOf<Season>()
    private val episodesBySeason = mutableStateMapOf<Int, List<Episode>>()
    private var selectedSeasonNumber by mutableIntStateOf(1)
    private var statusMessage by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvShowId = intent.getIntExtra("TV_SHOW_ID", 0)
        tvShowName = intent.getStringExtra("TV_SHOW_NAME") ?: getString(R.string.unknown_series)

        setContent {
            BrutalistTheme {
                SeasonsScreen(
                    showName = tvShowName,
                    seasons = seasons,
                    episodesBySeason = episodesBySeason,
                    selectedSeasonNumber = selectedSeasonNumber,
                    statusMessage = statusMessage,
                    onSeasonSelected = { seasonNum -> selectedSeasonNumber = seasonNum },
                    onEpisodeClick = { episode -> handleEpisodeClick(episode) },
                )
            }
        }

        loadSeasons()
    }

    // ── DATA LOADING ───────────────────────────────────────

    private fun loadSeasons() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val showDetails = TmdbApiClient.service.getTvShowDetails(tvShowId, TmdbApiClient.API_KEY)
                val validSeasons = showDetails.seasons.filter { it.season_number > 0 }

                withContext(Dispatchers.Main) {
                    seasons.addAll(validSeasons)
                    if (validSeasons.isNotEmpty()) {
                        selectedSeasonNumber = validSeasons.first().season_number
                    }
                }

                // Fetch episodes for each season
                for (season in validSeasons) {
                    fetchEpisodesForSeason(season.season_number)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TvSeasonsActivity, R.string.error_loading_tv_show, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun fetchEpisodesForSeason(seasonNumber: Int) {
        try {
            val seasonData = TmdbApiClient.service.getSeasonDetails(tvShowId, seasonNumber, TmdbApiClient.API_KEY)
            withContext(Dispatchers.Main) {
                episodesBySeason[seasonNumber] = seasonData.episodes
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── EPISODE CLICK → P2P / DB FALLBACK ──────────────────

    private fun handleEpisodeClick(episode: Episode) {
        val episodeTag = String.format("S%02dE%02d", episode.season_number, episode.episode_number)
        statusMessage = getString(R.string.msg_searching, "$tvShowName $episodeTag")
        Toast.makeText(this, getString(R.string.msg_searching_sources_toast, "$tvShowName $episodeTag"), Toast.LENGTH_SHORT).show()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val progressCallback: (String) -> Unit = { status ->
                    runOnUiThread {
                        statusMessage = status
                        Toast.makeText(this@TvSeasonsActivity, status, Toast.LENGTH_SHORT).show()
                    }
                }

                // 1. Try P2P torrent search first
                val magnetUrl = MediaScraperEngine.findMagnetForEpisode(
                    tvShowId, tvShowName,
                    episode.season_number, episode.episode_number,
                    progressCallback
                )

                if (magnetUrl != null) {
                    withContext(Dispatchers.Main) {
                        statusMessage = getString(R.string.msg_torrent_found)
                        Toast.makeText(this@TvSeasonsActivity, R.string.msg_torrent_found_toast, Toast.LENGTH_SHORT).show()
                        startTorrentStream(magnetUrl, "$tvShowName $episodeTag")
                    }
                    return@launch
                }

                // 2. Fallback: search local Room DB
                withContext(Dispatchers.Main) {
                    statusMessage = getString(R.string.msg_checking_local_db)
                }

                val streamUrl = MediaScraperEngine.findEpisodeStream(
                    this@TvSeasonsActivity, tvShowName,
                    episode.season_number, episode.episode_number
                )

                withContext(Dispatchers.Main) {
                    if (streamUrl != null) {
                        val episodeTag = String.format("S%02dE%02d", episode.season_number, episode.episode_number)
                        statusMessage = getString(R.string.msg_stream_found)
                        val intent = Intent(this@TvSeasonsActivity, VideoPlayerActivity::class.java)
                        intent.putExtra("VIDEO_URL", streamUrl)
                        intent.putExtra("TITLE", tvShowName)
                        intent.putExtra("EPISODE_LABEL", episodeTag)
                        intent.putExtra("CONTENT_ID", tvShowId.toString())
                        intent.putExtra("CONTENT_TYPE", "SERIES")
                        startActivity(intent)
                    } else {
                        val episodeTag = String.format("S%02dE%02d", episode.season_number, episode.episode_number)
                        statusMessage = getString(R.string.msg_no_sources, episodeTag)
                        Toast.makeText(this@TvSeasonsActivity, getString(R.string.msg_no_sources_toast, "$tvShowName $episodeTag"), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    statusMessage = getString(R.string.msg_p2p_error, e.message ?: "Unknown error")
                    Toast.makeText(this@TvSeasonsActivity, getString(R.string.msg_p2p_error_toast, e.message ?: "Unknown error"), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ── P2P TORRENT STREAMING ──────────────────────────────

    private fun startTorrentStream(magnetUrl: String, title: String) {
        com.example.imagis.data.TorrentRepository.initialize(this)
        com.example.imagis.data.TorrentRepository.startStream(this, magnetUrl, title)
        
        lifecycleScope.launch(Dispatchers.Main) {
            com.example.imagis.data.TorrentRepository.downloadState.collect { state ->
                if (state != null) {
                    if (state.error != null) {
                        onStreamError(null, Exception(state.error))
                    } else if (state.videoFile != null) {
                        onStreamReady(com.example.imagis.data.TorrentRepository.currentTorrent())
                    } else if (state.buffering) {
                        onStreamStarted(null)
                    }
                    
                    if (state.progress > 0) {
                        onStreamProgress(com.example.imagis.data.TorrentRepository.currentTorrent(), null)
                    }
                }
            }
        }
    }

    private var isStreamReady = false
    private var hasLaunchedPlayer = false
    private var streamStartTime = 0L

    override fun onStreamPrepared(torrent: Torrent?) { Log.d("P2P_TV", "Stream Prepared!") }

    override fun onStreamStarted(torrent: Torrent?) {
        streamStartTime = System.currentTimeMillis()
        hasLaunchedPlayer = false
        isStreamReady = false
        
        runOnUiThread {
            statusMessage = getString(R.string.msg_connecting_swarm)
            Toast.makeText(this, R.string.msg_connecting_swarm_toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Log.e("P2P_TV", "Stream Error: ${e?.message}")
        runOnUiThread {
            statusMessage = getString(R.string.msg_p2p_error, e?.message ?: "")
            Toast.makeText(this, getString(R.string.msg_p2p_error_toast, e?.message ?: ""), Toast.LENGTH_LONG).show()
        }
    }

    override fun onStreamReady(torrent: Torrent?) {
        Log.d("P2P_TV", "Stream Ready! Local File: ${torrent?.videoFile?.absolutePath}")
        isStreamReady = true
        checkAndLaunchPlayer(torrent, 0f)
    }

    private fun checkAndLaunchPlayer(torrent: Torrent?, currentProgress: Float) {
        if (hasLaunchedPlayer || !isStreamReady || torrent == null) return
        
        // Wait until we have at least 1.5% progress or 15 seconds have elapsed, OR 15MB buffered
        val elapsed = System.currentTimeMillis() - streamStartTime
        if (currentProgress >= 1.5f || elapsed >= 15_000 || torrent.hasBytes(15 * 1024 * 1024L)) {
            hasLaunchedPlayer = true
            lifecycleScope.launch(Dispatchers.Main) {
                statusMessage = getString(R.string.msg_stream_ready)
                val videoUrl = torrent.videoFile?.absolutePath
                val intent = Intent(this@TvSeasonsActivity, VideoPlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", videoUrl)
                intent.putExtra("TITLE", tvShowName)
                intent.putExtra("CONTENT_ID", tvShowId.toString())
                intent.putExtra("CONTENT_TYPE", "SERIES")
                startActivity(intent)
            }
        }
    }

    override fun onStreamProgress(torrent: Torrent?, status: com.github.se_bastiaan.torrentstream.StreamStatus?) {
        val progress = status?.progress ?: 0f
        val speed = status?.downloadSpeed?.toFloat() ?: 0f
        val mbSpeed = speed / 1024f / 1024f
        
        runOnUiThread {
            if (!hasLaunchedPlayer) {
                statusMessage = getString(R.string.msg_buffering_progress, progress.toInt(), String.format("%.1f", mbSpeed))
            }
        }
        checkAndLaunchPlayer(torrent, progress)
    }

    override fun onStreamStopped() { Log.d("P2P_TV", "Stream Stopped") }

    override fun onDestroy() {
        super.onDestroy()
        // Delegate stop to the service or user action
    }
}

// ══════════════════════════════════════════════════════════════
// ── COMPOSE UI ───────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════
//
//  ┌──────────────┬──┬──────────────────────────────────────┐
//  │  SEASONS     │▌▌│  SERIES TITLE (massive)              │
//  │  TvLazyCol   │▌▌│                                      │
//  │              │▌▌│  Status line                          │
//  │  SEASON 1    │▌▌│                                      │
//  │  SEASON 2    │▌▌│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐   │
//  │  SEASON 3    │▌▌│  │ E01 │ │ E02 │ │ E03 │ │ E04 │   │
//  │              │▌▌│  └─────┘ └─────┘ └─────┘ └─────┘   │
//  └──────────────┴──┴──────────────────────────────────────┘

@Composable
private fun SeasonsScreen(
    showName: String,
    seasons: List<Season>,
    episodesBySeason: Map<Int, List<Episode>>,
    selectedSeasonNumber: Int,
    statusMessage: String,
    onSeasonSelected: (Int) -> Unit,
    onEpisodeClick: (Episode) -> Unit,
) {
    val currentEpisodes = episodesBySeason[selectedSeasonNumber].orEmpty()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMatteBlack),
    ) {
        // ── LEFT: SEASON LIST ──────────────────────────────
        TvLazyColumn(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
                .padding(vertical = 32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(seasons) { _, season ->
                val isSelected = season.season_number == selectedSeasonNumber
                val bgColor = if (isSelected) NeonLime else Color.Transparent
                val txtColor = if (isSelected) DeepMatteBlack else PureWhite

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .juicyBrutalistFocus()
                        .clickable { onSeasonSelected(season.season_number) }
                        .background(bgColor)
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = stringResource(R.string.season_format_upper, season.season_number),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = txtColor,
                        maxLines = 1,
                    )
                }
            }
        }

        // ── DIVIDER ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(PureWhite),
        )

        // ── RIGHT: TITLE + EPISODES ────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 24.dp, top = 32.dp, end = 24.dp),
        ) {
            // Series title — massive, heavy
            Text(
                text = showName.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Season indicator
            Text(
                text = stringResource(R.string.season_format_upper, selectedSeasonNumber),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NeonLime,
            )

            // Status message (search progress)
            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricMagenta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Episode row
            if (currentEpisodes.isEmpty()) {
                Text(
                    text = stringResource(R.string.loading_episodes),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ElectricMagenta,
                )
            } else {
                TvLazyRow(
                    contentPadding = PaddingValues(end = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(currentEpisodes, key = { it.id }) { episode ->
                        EpisodeCard(
                            episode = episode,
                            onClick = { onEpisodeClick(episode) },
                        )
                    }
                }
            }
        }
    }
}

// ── EPISODE CARD ───────────────────────────────────────────

@Composable
private fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .juicyBrutalistFocus()
            .clickable { onClick() }
            .background(DeepMatteBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Thumbnail — 2dp white border, 0dp corners, Coil AsyncImage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .border(
                    width = 2.dp,
                    color = PureWhite,
                    shape = RectangleShape,
                )
                .background(Color(0xFF111111)),
        ) {
            AsyncImage(
                model = episode.fullStillUrl,
                contentDescription = episode.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Episode label
        Text(
            text = episode.displayTitle.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
        )
    }
}
