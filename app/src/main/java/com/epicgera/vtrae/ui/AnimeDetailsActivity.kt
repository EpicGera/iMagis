// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/AnimeDetailsActivity.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.epicgera.vtrae.data.AnimeEpisode
import com.epicgera.vtrae.data.DdlResult
import com.epicgera.vtrae.data.TorrentResult
import com.epicgera.vtrae.ui.components.juicyBrutalistFocus
import com.epicgera.vtrae.ui.theme.BrutalistTheme
import com.epicgera.vtrae.ui.theme.DeepMatteBlack
import com.epicgera.vtrae.ui.theme.ElectricMagenta
import com.epicgera.vtrae.ui.theme.NeonLime
import com.epicgera.vtrae.ui.theme.PureWhite
import com.epicgera.vtrae.utils.MediaScraperEngine
import com.epicgera.vtrae.utils.JkanimeScraper
import androidx.compose.ui.res.stringResource
import com.epicgera.vtrae.R
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeDetailsActivity : ComponentActivity(), TorrentListener {

    private lateinit var animeTitle: String
    private lateinit var episodeNumber: String
    private lateinit var imageUrl: String
    private lateinit var episodeUrl: String

    private val torrentResults = mutableStateListOf<TorrentResult>()
    private val ddlResults = mutableStateListOf<DdlResult>()
    private var statusMessage by mutableStateOf("")
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animeTitle = intent.getStringExtra("ANIME_TITLE") ?: getString(R.string.unknown_anime)
        episodeNumber = intent.getStringExtra("ANIME_EPISODE") ?: ""
        imageUrl = intent.getStringExtra("ANIME_IMAGE") ?: ""
        episodeUrl = intent.getStringExtra("ANIME_URL") ?: ""

        setContent {
            BrutalistTheme {
                AnimeSourceScreen(
                    title = animeTitle,
                    episode = episodeNumber,
                    imageUrl = imageUrl,
                    statusMessage = statusMessage,
                    isLoading = isLoading,
                    torrents = torrentResults,
                    ddls = ddlResults,
                    onTorrentClick = { startTorrentStream(it.magnetUrl, "$animeTitle $episodeNumber") },
                    onDdlClick = { 
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("VIDEO_URL", it.downloadUrl)
                        startActivity(intent)
                    }
                )
            }
        }

        searchSources()
    }

    private fun searchSources() {
        isLoading = true
        val episodeNumDigits = episodeNumber.replace(Regex("\\D"), "").trim()
        val searchQuery = "$animeTitle $episodeNumDigits".trim()
        statusMessage = getString(R.string.msg_searching_sources, searchQuery)
        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Searching the Æther for $searchQuery...")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val progressCallback: (String) -> Unit = { status ->
                    runOnUiThread { statusMessage = status }
                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(status)
                }

                // Fetch Jkanime direct streaming links
                runOnUiThread { statusMessage = getString(R.string.msg_fetching_jkanime) }
                val jkServers = if (episodeUrl.isNotEmpty()) {
                    JkanimeScraper.getEpisodeServers(episodeUrl)
                } else emptyList()

                val jkDdls = jkServers.map { server ->
                    DdlResult(
                        title = "JKAnime [${server.serverName}]",
                        quality = "1080p",
                        sizeDisplay = "Stream",
                        host = server.serverName.uppercase(),
                        hostFullName = "JKAnime Player CDN",
                        downloadUrl = server.embedUrl,
                        source = "JKAnime"
                    )
                }

                // Fetch torrents in parallel
                val torrents = MediaScraperEngine.searchAllSources(searchQuery, progressCallback)
                val ddls = MediaScraperEngine.searchPahe(searchQuery, progressCallback)

                withContext(Dispatchers.Main) {
                    torrentResults.clear()
                    torrentResults.addAll(torrents)
                    
                    ddlResults.clear()
                    ddlResults.addAll(jkDdls)  // Jkanime direct links first
                    ddlResults.addAll(ddls)     // Then other DDL sources

                    isLoading = false
                    val totalDdl = ddlResults.size
                    if (torrents.isEmpty() && totalDdl == 0) {
                        statusMessage = getString(R.string.msg_no_sources_fallback)
                        // Fallback: Open the Jkanime direct player link in webview
                        val fallbackIntent = Intent(this@AnimeDetailsActivity, WebViewActivity::class.java)
                        fallbackIntent.putExtra("VIDEO_URL", episodeUrl)
                        startActivity(fallbackIntent)
                        finish()
                    } else {
                        statusMessage = getString(R.string.msg_found_sources, torrents.size, totalDdl)
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Found $totalDdl DDLs and ${torrents.size} Torrents")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusMessage = getString(R.string.msg_p2p_error, e.message ?: "")
                    isLoading = false
                }
            }
        }
    }

    // ── P2P TORRENT STREAMING ──────────────────────────────

    private fun startTorrentStream(magnetUrl: String, title: String) {
        com.epicgera.vtrae.data.TorrentRepository.initialize(this)
        com.epicgera.vtrae.data.TorrentRepository.startStream(this, magnetUrl, title)
        
        statusMessage = getString(R.string.msg_connecting_p2p_title, title)

        lifecycleScope.launch(Dispatchers.Main) {
            com.epicgera.vtrae.data.TorrentRepository.downloadState.collect { state ->
                if (state != null) {
                    if (state.error != null) {
                        onStreamError(null, Exception(state.error))
                    } else if (state.videoFile != null) {
                        onStreamReady(com.epicgera.vtrae.data.TorrentRepository.currentTorrent())
                    } else if (state.buffering) {
                        onStreamStarted(null)
                    }
                    
                    if (state.progress > 0) {
                        onStreamProgress(com.epicgera.vtrae.data.TorrentRepository.currentTorrent(), null)
                    }
                }
            }
        }
    }

    private var isStreamReady = false
    private var hasLaunchedPlayer = false
    private var streamStartTime = 0L

    override fun onStreamPrepared(torrent: Torrent?) { Log.d("P2P_Anime", "Prepared!") }
    
    override fun onStreamStarted(torrent: Torrent?) {
        streamStartTime = System.currentTimeMillis()
        hasLaunchedPlayer = false
        isStreamReady = false
        
        runOnUiThread { statusMessage = getString(R.string.msg_p2p_started) }
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        runOnUiThread { statusMessage = getString(R.string.msg_p2p_error, e?.message ?: "") }
    }

    override fun onStreamReady(torrent: Torrent?) {
        isStreamReady = true
        checkAndLaunchPlayer(torrent, 0f)
    }

    private fun checkAndLaunchPlayer(torrent: Torrent?, currentProgress: Float) {
        if (hasLaunchedPlayer || !isStreamReady || torrent == null) return
        
        // Emulate the robust pre-buffering from TvSeasonsActivity to prevent ExoPlayer MKV header crashes
        // Wait until we have at least 1.5% progress or 15 seconds have elapsed, OR 15MB buffered
        val elapsed = System.currentTimeMillis() - streamStartTime
        if (currentProgress >= 1.5f || elapsed >= 15_000 || torrent.hasBytes(15 * 1024 * 1024L)) {
            hasLaunchedPlayer = true
            runOnUiThread {
                statusMessage = getString(R.string.msg_stream_ready)
                val videoUrl = com.epicgera.vtrae.data.TorrentRepository.currentTorrent()?.videoFile?.absolutePath
                val intent = Intent(this@AnimeDetailsActivity, VideoPlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", videoUrl)
                intent.putExtra("TITLE", animeTitle)
                intent.putExtra("EPISODE_LABEL", episodeNumber)
                intent.putExtra("CONTENT_ID", animeTitle)
                intent.putExtra("CONTENT_TYPE", "ANIME")
                intent.putExtra("POSTER_URL", imageUrl)
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

    override fun onStreamStopped() { Log.d("P2P_Anime", "Stopped") }

    override fun onDestroy() {
        super.onDestroy()
        // Delegate stop to the service or user action
    }
}

// ── COMPOSE UI ───────────────────────────────────────────────

@Composable
private fun AnimeSourceScreen(
    title: String,
    episode: String,
    imageUrl: String,
    statusMessage: String,
    isLoading: Boolean,
    torrents: List<TorrentResult>,
    ddls: List<DdlResult>,
    onTorrentClick: (TorrentResult) -> Unit,
    onDdlClick: (DdlResult) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(DeepMatteBlack)) {
        // Full screen blurred background
        AsyncImage(
            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(40.dp)
        )
        // Dark glass overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)))

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ── LEFT PANE: ANIME POSTER ──
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .padding(32.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, PureWhite.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
                            .build(),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = title.uppercase(),
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = PureWhite,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = episode.uppercase(),
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NeonLime
                )
            }

            // ── DIVIDER ──
            Box(modifier = Modifier.width(1.dp).fillMaxHeight().padding(vertical = 32.dp).background(PureWhite.copy(alpha = 0.2f)))

            // ── RIGHT PANE: SOURCES ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(32.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_available_sources),
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = PureWhite
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = statusMessage,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ElectricMagenta
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isLoading) {
                    TvLazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // DDLS
                        if (ddls.isNotEmpty()) {
                            item {
                                Text(stringResource(R.string.streams_direct), color = NeonLime, fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold)
                            }
                            items(ddls) { result ->
                                SourceCard(
                                    type = "DDL [${result.host}]",
                                    title = result.title,
                                    sizeInfo = result.sizeDisplay,
                                    seedInfo = result.quality,
                                    isDdl = true,
                                    onClick = { onDdlClick(result) }
                                )
                            }
                        }

                        // TORRENTS
                        if (torrents.isNotEmpty()) {
                            item {
                                if (ddls.isNotEmpty()) Spacer(Modifier.height(16.dp))
                                Text(stringResource(R.string.streams_p2p), color = NeonLime, fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold)
                            }
                            items(torrents) { result ->
                                SourceCard(
                                    type = "TORRENT [${result.source}]",
                                    title = result.title,
                                    sizeInfo = result.sizeDisplay,
                                    seedInfo = result.seeds.toString(),
                                    isDdl = false,
                                    onClick = { onTorrentClick(result) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceCard(
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
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan else PureWhite.copy(alpha = 0.10f),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.05f),
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isFocused) 16f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "elevation"
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
                elevation = elevation.dp,
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = type,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isFocused) Color.Cyan else ElectricMagenta
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isFocused) Color.Cyan else ElectricMagenta
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDdl) Icons.Filled.PlayArrow else Icons.Default.Share,
                            contentDescription = "Quality/Seeds",
                            tint = if (isFocused) Color.Cyan else ElectricMagenta,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = seedInfo,
                            fontFamily = FontFamily.Default,
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
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

