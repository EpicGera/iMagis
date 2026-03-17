package com.epicgera.vtrae.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.util.Log
import androidx.annotation.OptIn
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.epicgera.vtrae.R
import com.epicgera.vtrae.utils.JkanimeScraper
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.ComposeView
import android.view.ViewGroup
import android.widget.FrameLayout

class PlayerActivity : FragmentActivity() {

    companion object {
        private const val TAG = "PlayerActivity"
    }

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var videoUrl: String? = null
    private var isVodPage: Boolean = false

    // ── HTTP HEADER SUPPORT ────────────────────────────────────
    private var httpReferrer: String? = null
    private var httpUserAgent: String? = null
    private var daiEventId: String? = null

    // ── FALLBACK STATE ──────────────────────────────────────────
    private var fallbackUrls: List<String> = emptyList()
    private var currentFallbackIndex: Int = -1 // -1 = primary, 0+ = fallback index
    private var totalAttempts: Int = 1

    // ── Strict Auto-Hide Timeout Handling ──
    private val hideTimeoutHandler = Handler(Looper.getMainLooper())
    private val hideControllerRunnable = Runnable {
        if (player?.isPlaying == true) {
            playerView.hideController()
            playerView.clearFocus()
            playerView.requestFocus() // Prevent UI elements from stealing focus permanently
        }
    }

    private fun resetHideTimeout() {
        hideTimeoutHandler.removeCallbacks(hideControllerRunnable)
        if (player?.isPlaying == true) {
            hideTimeoutHandler.postDelayed(hideControllerRunnable, 3000)
        }
    }

    @OptIn(UnstableApi::class) 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Inject ComposeView for Custom Notifications
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val composeView = ComposeView(this).apply {
            setContent {
                com.epicgera.vtrae.ui.theme.FlixTheme {
                    com.epicgera.vtrae.ui.components.VtrToastHost()
                }
            }
        }
        rootView.addView(composeView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        playerView = findViewById(R.id.player_view)
        
        // Disable ExoPlayer's native timeout so we can strictly control it
        playerView.controllerShowTimeoutMs = -1
        
        playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == android.view.View.GONE) {
                // Guarantee focus returns to the player view so it doesn't get trapped
                playerView.requestFocus()
            }
        })
        
        try {
            videoUrl = intent.getStringExtra("VIDEO_URL")
            isVodPage = intent.getBooleanExtra("IS_VOD_PAGE", false)

            // Load HTTP headers for CDNs that require Referer/UA
            httpReferrer = intent.getStringExtra("HTTP_REFERRER")
            httpUserAgent = intent.getStringExtra("HTTP_USER_AGENT")
            daiEventId = intent.getStringExtra("DAI_EVENT_ID")

            // Load fallback URLs if provided
            val extras = intent.getStringArrayExtra("FALLBACK_URLS")
            if (extras != null) {
                fallbackUrls = extras.toList()
            }
            totalAttempts = 1 + fallbackUrls.size

            if (videoUrl == null) {
                com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.error_no_video_source))
                Handler(Looper.getMainLooper()).postDelayed({ if (!isFinishing) finish() }, 3500)
                return
            }

            Log.d(TAG, "Primary URL: $videoUrl | Fallbacks: ${fallbackUrls.size}")
            com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_loading_url, videoUrl))

            if (daiEventId != null) {
                // Google DAI session-based stream (e.g. América TV)
                resolveDaiStream(daiEventId!!)
            } else if (isVodPage) {
                resolveVodStream(videoUrl!!)
            } else {
                initializePlayer(videoUrl!!)
            }
        } catch (e: Exception) {
             e.printStackTrace()
             com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_player_error, e.message))
             finish()
        }
    }

    private fun resolveVodStream(pageUrl: String) {
        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_resolving_servers))
        lifecycleScope.launch {
            val servers = JkanimeScraper.getVideoServers(pageUrl)
            if (servers.isNotEmpty()) {
                val target = servers.first().embedUrl
                
                // Check if it's a direct video file we can play natively
                if (target.endsWith(".mp4") || target.endsWith(".m3u8")) {
                    initializePlayer(target)
                } else {
                    // It's a web embed (iframe/page), Use WebView
                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_launching_web_player))
                    val intent = android.content.Intent(this@PlayerActivity, WebViewActivity::class.java)
                    intent.putExtra("VIDEO_URL", target)
                    startActivity(intent)
                    finish() // Close PlayerActivity
                }
            } else {
                com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_no_video_servers))
                Handler(Looper.getMainLooper()).postDelayed({ if (!isFinishing) finish() }, 3500)
            }
        }
    }

    private var httpCookie: String? = null

    /**
     * Resolves a Google DAI (Dynamic Ad Insertion) stream by creating a session.
     * Used for channels like América TV that use Google SSAI infrastructure.
     * POST to pubads.g.doubleclick.net/ssai/event/{eventId}/streams
     * Response JSON contains "stream_manifest" with the session-specific HLS URL.
     */
    private fun resolveDaiStream(eventId: String) {
        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Conectando con América TV...")
        lifecycleScope.launch {
            try {
                val manifestUrl = withContext(Dispatchers.IO) {
                    val url = java.net.URL("https://pubads.g.doubleclick.net/ssai/event/$eventId/streams")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Length", "0")
                    
                    // Critical: Use the exact same User-Agent for both the POST and ExoPlayer
                    val defaultUa = "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
                    httpUserAgent = defaultUa
                    conn.setRequestProperty("User-Agent", defaultUa)
                    
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000
                    conn.doOutput = true
                    conn.outputStream.close() // Send empty body

                    val responseCode = conn.responseCode
                    if (responseCode == 201 || responseCode == 200) {
                        // Capture any session cookies that Google might require
                        val cookiesHeader = conn.headerFields["Set-Cookie"]
                        if (cookiesHeader != null) {
                            httpCookie = cookiesHeader.joinToString("; ")
                            Log.d(TAG, "DAI session cookies: $httpCookie")
                        }

                        val responseBody = conn.inputStream.bufferedReader().readText()
                        val json = org.json.JSONObject(responseBody)
                        val manifest = json.optString("stream_manifest", "")
                        Log.d(TAG, "DAI session created: $manifest")
                        manifest.ifEmpty { null }
                    } else {
                        Log.e(TAG, "DAI session creation failed: HTTP $responseCode")
                        null
                    }
                }

                if (manifestUrl != null) {
                    Log.d(TAG, "Playing DAI stream: $manifestUrl")
                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Stream listo ▶")
                    initializePlayer(manifestUrl)
                } else {
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError("No se pudo conectar con América TV")
                    Handler(Looper.getMainLooper()).postDelayed({ if (!isFinishing) finish() }, 3500)
                }
            } catch (e: Exception) {
                Log.e(TAG, "DAI resolution error", e)
                com.epicgera.vtrae.ui.components.VtrToastManager.showError("Error: ${e.message}")
                Handler(Looper.getMainLooper()).postDelayed({ if (!isFinishing) finish() }, 3500)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        try {
            // Release any existing player before creating a new one
            player?.release()

            // Many IPTV providers block standard Chrome/Browser User-Agents (403 Forbidden).
            // Using a common player UA (or just "ExoPlayer") usually works best for Live TV.
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent(httpUserAgent ?: "ExoPlayer")
                .setAllowCrossProtocolRedirects(true)

            // Inject custom HTTP headers (e.g., Referer, Cookies)
            val headers = mutableMapOf<String, String>()
            httpReferrer?.let { headers["Referer"] = it }
            httpCookie?.let { headers["Cookie"] = it }
            
            if (headers.isNotEmpty()) {
                dataSourceFactory.setDefaultRequestProperties(headers)
                Log.d(TAG, "Injecting custom headers: ${headers.keys}")
            }
            
            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

            player = ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
                
            playerView.player = player

            // Help ExoPlayer identify the stream type in case the URL lacks a standard extension
            val mediaItemBuilder = MediaItem.Builder().setUri(url)
            when {
                url.contains(".m3u8", ignoreCase = true) -> mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                url.contains(".ts", ignoreCase = true) -> mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.VIDEO_MP2T)
                url.contains("m3u8", ignoreCase = true) -> mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
            }

            val mediaItem = mediaItemBuilder.build()
            player?.setMediaItem(mediaItem)
            
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        resetHideTimeout()
                    } else {
                        hideTimeoutHandler.removeCallbacks(hideControllerRunnable)
                        playerView.showController()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            Log.d(TAG, "Buffering stream: $url")
                        }
                        Player.STATE_READY -> {
                            Log.d(TAG, "Stream ready: $url")
                        }
                        Player.STATE_ENDED -> {
                            Log.w(TAG, "Stream ended (possibly empty): $url")
                            com.epicgera.vtrae.ui.components.VtrToastManager.showError("⚠️ Stream ended unexpectedly.")
                        }
                        Player.STATE_IDLE -> {
                            // STATE_IDLE after prepare() usually means a silent failure
                            // (e.g., cleartext blocked, DNS failure, 404).
                            // onPlayerError handles explicit errors, but some failures
                            // leave the player idle with no error callback.
                            Log.w(TAG, "Player went IDLE on: $url")
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    val codeName = error.errorCodeName
                    val cause = error.cause?.message ?: "Unknown cause"
                    Log.e(TAG, "Playback error [$codeName] on URL: $url — ${error.message} | Cause: $cause")
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
            com.epicgera.vtrae.ui.components.VtrToastManager.showError("⚡ Stream failed. Trying backup ($attemptNumber/$totalAttempts)...")

            initializePlayer(nextUrl)
        } else {
            // All fallbacks exhausted
            Log.e(TAG, "All $totalAttempts streams failed.")
            com.epicgera.vtrae.ui.components.VtrToastManager.showError("❌ All $totalAttempts streams failed. Channel unavailable.")
            Handler(Looper.getMainLooper()).postDelayed({ if (!isFinishing) finish() }, 3500)
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideTimeoutHandler.removeCallbacksAndMessages(null)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode

            // Reset strict auto-hide timeout on D-Pad interaction
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
                keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD ||
                keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                
                playerView.showController()
                resetHideTimeout()
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                // Check if focus is on a specific controller button
                val focusedView = currentFocus
                val isButtonFocused = focusedView is android.widget.ImageButton || focusedView is android.widget.Button

                if (!isButtonFocused) {
                    player?.let { exo ->
                        if (exo.isPlaying) {
                            exo.pause()
                            playerView.showController()
                            hideTimeoutHandler.removeCallbacks(hideControllerRunnable)
                        } else {
                            exo.play()
                            playerView.hideController()
                            playerView.requestFocus()
                        }
                    }
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}

