// FILE_PATH: app/src/main/java/com/example/imagis/ui/AnimeDetailsActivity.kt
// ACTION: CREATE
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import coil.compose.AsyncImage
import com.example.imagis.data.AnimeEpisode
import com.example.imagis.data.DdlResult
import com.example.imagis.data.TorrentResult
import com.example.imagis.ui.components.juicyBrutalistFocus
import com.example.imagis.ui.theme.BrutalistTheme
import com.example.imagis.ui.theme.DeepMatteBlack
import com.example.imagis.ui.theme.ElectricMagenta
import com.example.imagis.ui.theme.NeonLime
import com.example.imagis.ui.theme.PureWhite
import com.example.imagis.utils.MediaScraperEngine
import com.example.imagis.utils.JkanimeScraper
import androidx.compose.ui.res.stringResource
import com.example.imagis.R
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

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val progressCallback: (String) -> Unit = { status ->
                    runOnUiThread { statusMessage = status }
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
        com.example.imagis.data.TorrentRepository.initialize(this)
        com.example.imagis.data.TorrentRepository.startStream(this, magnetUrl, title)
        
        statusMessage = getString(R.string.msg_connecting_p2p_title, title)

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
                val videoUrl = com.example.imagis.data.TorrentRepository.currentTorrent()?.videoFile?.absolutePath
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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMatteBlack)
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
                    .border(2.dp, PureWhite, RectangleShape)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = PureWhite,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = episode.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NeonLime
            )
        }

        // ── DIVIDER ──
        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(PureWhite))

        // ── RIGHT PANE: SOURCES ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.title_available_sources),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = PureWhite
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = statusMessage,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ElectricMagenta
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                // Keep UI clean, just the status message spinning
            } else {
                TvLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // TORRENTS
                    if (torrents.isNotEmpty()) {
                        item {
                            Text(stringResource(R.string.streams_p2p), color = NeonLime, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        items(torrents) { result ->
                            SourceCard(
                                type = "TORRENT [${result.source}]",
                                title = result.title,
                                sizeInfo = result.sizeDisplay,
                                seedInfo = "🌱 ${result.seeds}",
                                onClick = { onTorrentClick(result) }
                            )
                        }
                    }

                    // DDLS
                    if (ddls.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(R.string.streams_direct), color = NeonLime, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        items(ddls) { result ->
                            SourceCard(
                                type = "DDL [${result.host}]",
                                title = result.title,
                                sizeInfo = result.sizeDisplay,
                                seedInfo = result.quality,
                                onClick = { onDdlClick(result) }
                            )
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .juicyBrutalistFocus()
            .clickable { onClick() }
            .background(DeepMatteBlack)
            .border(2.dp, PureWhite, RectangleShape)
            .padding(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = type,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NeonLime
                )
                Text(
                    text = "$sizeInfo  |  $seedInfo",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricMagenta
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = PureWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
