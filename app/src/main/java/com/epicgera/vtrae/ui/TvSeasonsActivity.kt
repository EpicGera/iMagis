// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/TvSeasonsActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.epicgera.vtrae.api.Episode
import com.epicgera.vtrae.api.Season
import com.epicgera.vtrae.api.TmdbApiClient
import com.epicgera.vtrae.data.DdlResult
import com.epicgera.vtrae.data.TorrentResult
import com.epicgera.vtrae.ui.components.juicyBrutalistFocus
import com.epicgera.vtrae.ui.theme.BrutalistTheme
import com.epicgera.vtrae.ui.theme.DeepMatteBlack
import com.epicgera.vtrae.ui.theme.ElectricMagenta
import com.epicgera.vtrae.ui.theme.NeonLime
import com.epicgera.vtrae.ui.theme.PureWhite
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ArrowBack
import com.epicgera.vtrae.utils.MediaScraperEngine
import androidx.compose.ui.res.stringResource
import com.epicgera.vtrae.R
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
    private var backdropUrl by mutableStateOf("")

    // ── Source selection state ────────────────────────────
    private val torrentResults = mutableStateListOf<TorrentResult>()
    private val ddlResults = mutableStateListOf<DdlResult>()
    private var showSourceSelection by mutableStateOf(false)
    private var isSearchingSources by mutableStateOf(false)
    private var selectedEpisodeLabel by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvShowId = intent.getIntExtra("TV_SHOW_ID", 0)
        tvShowName = intent.getStringExtra("TV_SHOW_NAME") ?: getString(R.string.unknown_series)

        setContent {
            BrutalistTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    SeasonsScreen(
                        showName = tvShowName,
                        seasons = seasons,
                        episodesBySeason = episodesBySeason,
                        selectedSeasonNumber = selectedSeasonNumber,
                        statusMessage = statusMessage,
                        backdropUrl = backdropUrl,
                        onSeasonSelected = { seasonNum -> selectedSeasonNumber = seasonNum },
                        onEpisodeClick = { episode -> handleEpisodeClick(episode) },
                    )

                    // Source selection overlay
                    if (showSourceSelection || isSearchingSources) {
                        EpisodeSourceOverlay(
                            episodeLabel = selectedEpisodeLabel,
                            showName = tvShowName,
                            statusMessage = statusMessage,
                            isLoading = isSearchingSources,
                            torrents = torrentResults,
                            ddls = ddlResults,
                            onTorrentClick = { startTorrentStream(it.magnetUrl, "$tvShowName $selectedEpisodeLabel") },
                            onDdlClick = {
                                val intent = Intent(this@TvSeasonsActivity, WebViewActivity::class.java)
                                intent.putExtra("VIDEO_URL", it.downloadUrl)
                                startActivity(intent)
                            },
                            onBack = {
                                showSourceSelection = false
                                isSearchingSources = false
                                statusMessage = ""
                            }
                        )
                    }
                }
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
                    backdropUrl = showDetails.fullBackdropUrl
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
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.error_loading_tv_show))
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
        selectedEpisodeLabel = episodeTag
        isSearchingSources = true
        showSourceSelection = false
        torrentResults.clear()
        ddlResults.clear()
        statusMessage = getString(R.string.msg_searching, "$tvShowName $episodeTag")
        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Searching the Æther for $tvShowName $episodeTag...")

        val searchQuery = "$tvShowName $episodeTag"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val progressCallback: (String) -> Unit = { status ->
                    runOnUiThread {
                        statusMessage = status
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(status)
                    }
                }

                // Search ALL torrent sources and Pahe DDL — let user pick
                val torrents = MediaScraperEngine.searchAllSources(searchQuery, progressCallback)
                val ddls = MediaScraperEngine.searchPahe(searchQuery, progressCallback)

                withContext(Dispatchers.Main) {
                    torrentResults.clear()
                    torrentResults.addAll(torrents)
                    ddlResults.clear()
                    ddlResults.addAll(ddls)
                    isSearchingSources = false

                    if (torrents.isNotEmpty() || ddls.isNotEmpty()) {
                        showSourceSelection = true
                        statusMessage = getString(R.string.msg_found_sources, torrents.size, ddls.size)
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Found ${ddls.size} DDLs and ${torrents.size} Torrents")
                    } else {
                        // Fallback: try local Room DB
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_checking_local_db))
                        lifecycleScope.launch(Dispatchers.IO) {
                            val streamUrl = MediaScraperEngine.findEpisodeStream(
                                this@TvSeasonsActivity, tvShowName,
                                episode.season_number, episode.episode_number
                            )
                            withContext(Dispatchers.Main) {
                                if (streamUrl != null) {
                                    statusMessage = getString(R.string.msg_stream_found)
                                    val intent = Intent(this@TvSeasonsActivity, VideoPlayerActivity::class.java)
                                    intent.putExtra("VIDEO_URL", streamUrl)
                                    intent.putExtra("TITLE", tvShowName)
                                    intent.putExtra("EPISODE_LABEL", episodeTag)
                                    intent.putExtra("CONTENT_ID", tvShowId.toString())
                                    intent.putExtra("CONTENT_TYPE", "SERIES")
                                    startActivity(intent)
                                } else {
                                    statusMessage = getString(R.string.msg_no_sources, episodeTag)
                                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_no_sources_toast, "$tvShowName $episodeTag"))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isSearchingSources = false
                    statusMessage = getString(R.string.msg_p2p_error, e.message ?: "Unknown error")
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_p2p_error_toast, e.message ?: "Unknown error"))
                }
            }
        }
    }

    // ── P2P TORRENT STREAMING ──────────────────────────────

    private fun startTorrentStream(magnetUrl: String, title: String) {
        com.epicgera.vtrae.data.TorrentRepository.initialize(this)
        
        streamStartTime = System.currentTimeMillis()
        hasLaunchedPlayer = false
        isStreamReady = false
        statusMessage = getString(R.string.msg_connecting_swarm)
        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_connecting_swarm_toast))
        
        com.epicgera.vtrae.data.TorrentRepository.startStream(this, magnetUrl, title)
        
        lifecycleScope.launch(Dispatchers.Main) {
            com.epicgera.vtrae.data.TorrentRepository.downloadState.collect { state ->
                if (state == null) return@collect
                
                if (state.error != null) {
                    statusMessage = getString(R.string.msg_p2p_error, state.error)
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(
                        getString(R.string.msg_p2p_error_toast, state.error)
                    )
                    return@collect
                }
                
                // Update statusMessage with live progress from the flow
                val progress = state.progress.toInt()
                val speed = state.downloadSpeedBytes / 1024f
                val mbSpeed = speed / 1024f
                val speedStr = String.format("%.1f", mbSpeed)
                
                if (progress > 0 && !hasLaunchedPlayer) {
                    statusMessage = getString(R.string.msg_buffering_progress, progress, speedStr)
                } else if (state.buffering) {
                    statusMessage = getString(R.string.msg_connecting_swarm)
                }
                
                // Check ready
                if (!state.buffering && state.videoFile != null) {
                    isStreamReady = true
                    checkAndLaunchPlayer(
                        com.epicgera.vtrae.data.TorrentRepository.currentTorrent(),
                        state.progress
                    )
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
            com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_connecting_swarm_toast))
        }
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Log.e("P2P_TV", "Stream Error: ${e?.message}")
        runOnUiThread {
            statusMessage = getString(R.string.msg_p2p_error, e?.message ?: "")
            com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_p2p_error_toast, e?.message ?: ""))
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
    backdropUrl: String,
    onSeasonSelected: (Int) -> Unit,
    onEpisodeClick: (Episode) -> Unit,
) {
    val currentEpisodes = episodesBySeason[selectedSeasonNumber].orEmpty()

    Box(modifier = Modifier.fillMaxSize().background(DeepMatteBlack)) {
        if (backdropUrl.isNotEmpty()) {
            AsyncImage(
                model = backdropUrl,
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
                
                val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()
                val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")
                val borderColor by animateColorAsState(targetValue = if (isFocused) Color.Cyan else Color.Transparent, label = "borderColor")
                
                val bgColor = if (isFocused) {
                    Color.Cyan.copy(alpha = 0.20f)
                } else if (isSelected) {
                    NeonLime.copy(alpha = 0.8f) // Make selected stand out if not focused
                } else {
                    Color.Transparent
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .border(3.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(interactionSource = interactionSource, indication = null) { onSeasonSelected(season.season_number) }
                        .focusable(interactionSource = interactionSource)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = stringResource(R.string.season_format_upper, season.season_number),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PureWhite,
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
                .background(PureWhite.copy(alpha = 0.3f)),
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
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Season indicator
            Text(
                text = stringResource(R.string.season_format_upper, selectedSeasonNumber),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = NeonLime,
            )

            // Status message (search progress)
            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
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
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
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
}

// ── EPISODE CARD ───────────────────────────────────────────

@Composable
private fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")
    val borderColor by animateColorAsState(targetValue = if (isFocused) Color.Cyan else Color.Transparent, label = "borderColor")
    val imageAlpha by animateFloatAsState(targetValue = if (isFocused) 1.0f else 0.6f, label = "alpha")
    val bgColor = if (isFocused) Color.Cyan.copy(alpha = 0.20f) else DeepMatteBlack.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .width(220.dp)
            .scale(scale)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Thumbnail — Rounded corners at top, dimmer when unfocused
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(Color(0xFF111111)),
        ) {
            AsyncImage(
                model = episode.fullStillUrl,
                contentDescription = episode.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(imageAlpha),
            )
        }

        // Episode label
        Text(
            text = episode.displayTitle.uppercase(),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = PureWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }
}

// ── EPISODE SOURCE SELECTION OVERLAY ──────────────────────────

@Composable
private fun EpisodeSourceOverlay(
    episodeLabel: String,
    showName: String,
    statusMessage: String,
    isLoading: Boolean,
    torrents: List<TorrentResult>,
    ddls: List<DdlResult>,
    onTorrentClick: (TorrentResult) -> Unit,
    onDdlClick: (DdlResult) -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Header row with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val backInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val backFocused by backInteraction.collectIsFocusedAsState()
                val backBorder by animateColorAsState(targetValue = if (backFocused) Color.Cyan else PureWhite.copy(alpha = 0.3f), label = "bb")

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(2.dp, backBorder, RoundedCornerShape(8.dp))
                        .background(if (backFocused) Color.Cyan.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable(interactionSource = backInteraction, indication = null) { onBack() }
                        .focusable(interactionSource = backInteraction),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (backFocused) Color.Cyan else PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "$showName — $episodeLabel".uppercase(),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = statusMessage,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ElectricMagenta,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLoading) {
                TvLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // DDL section
                    if (ddls.isNotEmpty()) {
                        item {
                            Text(
                                text = "⬇ DIRECT DOWNLOADS (${ddls.size})",
                                color = NeonLime,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        items(ddls) { result ->
                            TvSourceCard(
                                type = "DDL [${result.host}]",
                                title = result.title,
                                sizeInfo = result.sizeDisplay,
                                seedInfo = result.quality,
                                isDdl = true,
                                onClick = { onDdlClick(result) }
                            )
                        }
                    }

                    // Torrent section
                    if (torrents.isNotEmpty()) {
                        item {
                            if (ddls.isNotEmpty()) Spacer(Modifier.height(12.dp))
                            Text(
                                text = "🌐 P2P TORRENTS (${torrents.size})",
                                color = NeonLime,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        items(torrents) { result ->
                            TvSourceCard(
                                type = "TORRENT [${result.source}]",
                                title = result.title,
                                sizeInfo = result.sizeDisplay,
                                seedInfo = "${result.seeds} seeds",
                                isDdl = false,
                                onClick = { onTorrentClick(result) }
                            )
                        }
                    }
                }
            } else {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusMessage,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ElectricMagenta
                    )
                }
            }
        }
    }
}

// ── SOURCE CARD (TV-OPTIMIZED) ──────────────────────────────

@Composable
private fun TvSourceCard(
    type: String,
    title: String,
    sizeInfo: String,
    seedInfo: String,
    isDdl: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan else PureWhite.copy(alpha = 0.10f),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1.0f else 0.75f,
        animationSpec = tween(durationMillis = 200),
        label = "contentAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isFocused) 12.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Cyan,
                spotColor = Color.Cyan
            )
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.alpha(contentAlpha)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isFocused) Color.Cyan else ElectricMagenta
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Action",
                            tint = if (isFocused) Color.Cyan else ElectricMagenta,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = sizeInfo,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isFocused) Color.Cyan else ElectricMagenta
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDdl) Icons.Filled.PlayArrow else Icons.Default.Share,
                            contentDescription = "Seeds",
                            tint = if (isFocused) Color.Cyan else ElectricMagenta,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = seedInfo,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isFocused) Color.Cyan else ElectricMagenta
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

