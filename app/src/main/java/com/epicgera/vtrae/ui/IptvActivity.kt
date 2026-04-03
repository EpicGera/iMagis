// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/IptvActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.epicgera.vtrae.R
import com.epicgera.vtrae.data.ChannelStore
import com.epicgera.vtrae.data.IptvChannel
import com.epicgera.vtrae.data.PlaylistSource
import com.epicgera.vtrae.ui.screens.LiveTvCategoriesScreen
import com.epicgera.vtrae.ui.theme.FlixTheme
import com.epicgera.vtrae.utils.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class IptvActivity : ComponentActivity() {

    companion object {
        private const val TAG = "IptvActivity"
    }

    private var playlistUrl: String? = null
    private var playlistName: String? = null

    // Compose state
    private val playlists = mutableStateListOf<PlaylistSource>()
    private val channels = mutableStateListOf<IptvChannel>()
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistUrl = intent.getStringExtra("PLAYLIST_URL")
        playlistName = intent.getStringExtra("PLAYLIST_NAME")

        setContent {
            FlixTheme {
                LiveTvCategoriesScreen(
                    title = playlistName ?: "LIVE TV CATEGORIES",
                    playlists = playlists,
                    channels = channels,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onPlaylistClick = { playlist ->
                        val intent = Intent(this, IptvActivity::class.java).apply {
                            putExtra("PLAYLIST_URL", playlist.url)
                            putExtra("PLAYLIST_NAME", playlist.name)
                        }
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    },
                    onChannelClick = { channel ->
                        if (channel.streamUrl.startsWith("webview://")) {
                            // WebView-based channel (e.g. Locomotion via serenotv)
                            val webUrl = channel.streamUrl.removePrefix("webview://")
                            val intent = Intent(this, WebViewActivity::class.java).apply {
                                putExtra("VIDEO_URL", webUrl)
                            }
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        } else {
                            val intent = Intent(this, PlayerActivity::class.java).apply {
                                putExtra("VIDEO_URL", channel.streamUrl)
                                putExtra("IS_VOD_PAGE", false)
                                if (channel.fallbackUrls.isNotEmpty()) {
                                    putExtra("FALLBACK_URLS", channel.fallbackUrls.toTypedArray())
                                }
                                channel.referrer?.let { putExtra("HTTP_REFERRER", it) }
                                channel.userAgent?.let { putExtra("HTTP_USER_AGENT", it) }
                                channel.daiEventId?.let { putExtra("DAI_EVENT_ID", it) }
                                channel.tokenizeUrl?.let { putExtra("TOKENIZE_URL", it) }
                                channel.youtubeLiveId?.let { putExtra("YOUTUBE_LIVE_ID", it) }
                            }
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                )
            }
        }

        if (playlistUrl != null) {
            // Check if this is the Argentina playlist — use multi-source merge
            if (playlistName.equals("Argentina Live", ignoreCase = true)) {
                loadArgentinaChannels()
            } else {
                loadChannels(playlistUrl!!)
            }
        } else {
            loadPlaylists()
        }
    }

    private fun loadPlaylists() {
        playlists.clear()
        playlists.addAll(ChannelStore.preLoadedPlaylists)
        channels.clear()
    }

    /**
     * Standard single-source M3U loader.
     */
    private fun loadChannels(urlStr: String) {
        isLoading = true
        errorMessage = null
        playlists.clear()
        channels.clear()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val parsedChannels = fetchAndParseM3u(urlStr)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (parsedChannels.isEmpty()) {
                        errorMessage = "No channels found in this playlist."
                    } else {
                        channels.addAll(parsedChannels)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = e.message
                }
            }
        }
    }

    /**
     * Multi-source Argentina loader:
     * 1. Fetches from all 3 M3U sources in parallel.
     * 2. Merges with curated hardcoded channels.
     * 3. Deduplicates by name (case-insensitive), collecting all stream URLs as fallbacks.
     */
    private fun loadArgentinaChannels() {
        isLoading = true
        errorMessage = null
        playlists.clear()
        channels.clear()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Fetch all M3U sources in parallel
                val deferreds = ChannelStore.argentinaPlaylistSources.map { url ->
                    async {
                        try {
                            fetchAndParseM3u(url)
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to fetch Argentina source: $url — ${e.message}")
                            emptyList()
                        }
                    }
                }
                val allRemoteChannels = deferreds.awaitAll().flatten()

                // 2. Combine remote + curated
                val allChannels = mutableListOf<IptvChannel>()
                // Curated channels go first (higher priority)
                allChannels.addAll(ChannelStore.curatedArgentinaChannels)
                allChannels.addAll(allRemoteChannels)

                // 3. Deduplicate by name, collecting alternative URLs as fallbacks
                val merged = mergeChannelsByName(allChannels)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (merged.isEmpty()) {
                        errorMessage = "No Argentina channels found."
                    } else {
                        channels.addAll(merged)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = e.message
                }
            }
        }
    }

    /**
     * Fetches and parses a single M3U URL. Shared between single & multi-source loaders.
     */
    private fun fetchAndParseM3u(urlStr: String): List<IptvChannel> {
        val playlistContent = if (urlStr.startsWith("android.resource://")) {
            val identifier = resources.getIdentifier("ar_working", "raw", packageName)
            resources.openRawResource(identifier).bufferedReader().use { it.readText() }
        } else {
            val url = URL(urlStr)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )

            if (connection.responseCode != 200) {
                throw Exception("HTTP Error: ${connection.responseCode}")
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        }
        return M3uParser.parse(playlistContent)
    }

    /**
     * Deduplicates channels by name (case-insensitive).
     * The first occurrence wins as primary; subsequent URLs become fallbacks.
     */
    private fun mergeChannelsByName(allChannels: List<IptvChannel>): List<IptvChannel> {
        // LinkedHashMap preserves insertion order (curated first)
        val map = LinkedHashMap<String, MutableList<IptvChannel>>()

        for (ch in allChannels) {
            val key = ch.name.trim().lowercase()
            map.getOrPut(key) { mutableListOf() }.add(ch)
        }

        return map.values.map { group ->
            val primary = group.first()
            // Collect all unique URLs from duplicates as fallbacks
            val allUrls = mutableSetOf(primary.streamUrl)
            allUrls.addAll(primary.fallbackUrls)
            for (dup in group.drop(1)) {
                allUrls.add(dup.streamUrl)
                allUrls.addAll(dup.fallbackUrls)
            }
            // Remove the primary URL from fallbacks
            val fallbacks = allUrls.filter { it != primary.streamUrl }

            primary.copy(
                fallbackUrls = fallbacks,
                // Use the first available logo
                logoUrl = primary.logoUrl ?: group.mapNotNull { it.logoUrl }.firstOrNull(),
                // Preserve headers from any source
                referrer = primary.referrer ?: group.mapNotNull { it.referrer }.firstOrNull(),
                userAgent = primary.userAgent ?: group.mapNotNull { it.userAgent }.firstOrNull(),
                daiEventId = primary.daiEventId ?: group.mapNotNull { it.daiEventId }.firstOrNull(),
                tokenizeUrl = primary.tokenizeUrl ?: group.mapNotNull { it.tokenizeUrl }.firstOrNull(),
                youtubeLiveId = primary.youtubeLiveId ?: group.mapNotNull { it.youtubeLiveId }.firstOrNull()
            )
        }
    }
}

