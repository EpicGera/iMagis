package com.example.imagis.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.imagis.R
import com.example.imagis.utils.JkanimeScraper
import kotlinx.coroutines.launch

class PlayerActivity : FragmentActivity() {

    companion object {
        private const val TAG = "PlayerActivity"
    }

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var videoUrl: String? = null
    private var isVodPage: Boolean = false

    // ── FALLBACK STATE ──────────────────────────────────────────
    private var fallbackUrls: List<String> = emptyList()
    private var currentFallbackIndex: Int = -1 // -1 = primary, 0+ = fallback index
    private var totalAttempts: Int = 1

    @OptIn(UnstableApi::class) 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.player_view)
        
        try {
            videoUrl = intent.getStringExtra("VIDEO_URL")
            isVodPage = intent.getBooleanExtra("IS_VOD_PAGE", false)

            // Load fallback URLs if provided
            val extras = intent.getStringArrayExtra("FALLBACK_URLS")
            if (extras != null) {
                fallbackUrls = extras.toList()
            }
            totalAttempts = 1 + fallbackUrls.size

            if (videoUrl == null) {
                Toast.makeText(this, R.string.error_no_video_source, Toast.LENGTH_LONG).show()
                finish()
                return
            }

            Log.d(TAG, "Primary URL: $videoUrl | Fallbacks: ${fallbackUrls.size}")
            Toast.makeText(this, getString(R.string.msg_loading_url, videoUrl), Toast.LENGTH_SHORT).show()

            if (isVodPage) {
                resolveVodStream(videoUrl!!)
            } else {
                initializePlayer(videoUrl!!)
            }
        } catch (e: Exception) {
             e.printStackTrace()
             Toast.makeText(this, getString(R.string.msg_player_error, e.message), Toast.LENGTH_LONG).show()
             finish()
        }
    }

    private fun resolveVodStream(pageUrl: String) {
        Toast.makeText(this, R.string.msg_resolving_servers, Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            val servers = JkanimeScraper.getVideoServers(pageUrl)
            if (servers.isNotEmpty()) {
                val target = servers.first().embedUrl
                
                // Check if it's a direct video file we can play natively
                if (target.endsWith(".mp4") || target.endsWith(".m3u8")) {
                    initializePlayer(target)
                } else {
                    // It's a web embed (iframe/page), Use WebView
                    Toast.makeText(this@PlayerActivity, R.string.msg_launching_web_player, Toast.LENGTH_SHORT).show()
                    val intent = android.content.Intent(this@PlayerActivity, WebViewActivity::class.java)
                    intent.putExtra("VIDEO_URL", target)
                    startActivity(intent)
                    finish() // Close PlayerActivity
                }
            } else {
                Toast.makeText(this@PlayerActivity, R.string.msg_no_video_servers, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        try {
            // Release any existing player before creating a new one
            player?.release()

            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .setAllowCrossProtocolRedirects(true)
            
            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

            player = ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
                
            playerView.player = player

            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            
            player?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.e(TAG, "Playback error on URL: $url — ${error.message}")
                    error.printStackTrace()
                    
                    // ── FALLBACK LOGIC ───────────────────────────────
                    tryNextFallback()
                }
            })

            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "ExoPlayer init failed for $url: ${e.message}")
            // Try fallback even if init fails
            tryNextFallback()
        }
    }

    /**
     * Attempts the next fallback URL. If all exhausted, shows error and finishes.
     */
    private fun tryNextFallback() {
        currentFallbackIndex++

        if (currentFallbackIndex < fallbackUrls.size) {
            val nextUrl = fallbackUrls[currentFallbackIndex]
            val attemptNumber = currentFallbackIndex + 2 // +2 because primary was attempt 1
            
            Log.d(TAG, "Trying fallback ($attemptNumber/$totalAttempts): $nextUrl")
            Toast.makeText(
                this,
                "⚡ Stream failed. Trying backup ($attemptNumber/$totalAttempts)...",
                Toast.LENGTH_SHORT
            ).show()

            initializePlayer(nextUrl)
        } else {
            // All fallbacks exhausted
            Log.e(TAG, "All $totalAttempts streams failed.")
            Toast.makeText(
                this,
                "❌ All $totalAttempts streams failed. Channel unavailable.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
