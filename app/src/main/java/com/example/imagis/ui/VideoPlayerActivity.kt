package com.example.imagis.ui

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.example.imagis.R

@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var currentUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // CRITICAL FOR TV: Prevent the screen from going to sleep while a movie is playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.video_view)
        
        // Retrieve the URL passed from our Details Screen
        val videoUrl = intent.getStringExtra("VIDEO_URL")
        
        if (videoUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No video source provided.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentUrl = videoUrl
        initializePlayer(videoUrl)
    }

    private fun initializePlayer(url: String) {
        // Release any existing player first
        player?.release()
        player = null

        // Create a DefaultTrackSelector to enable audio/subtitle switching
        val trackSelector = DefaultTrackSelector(this)
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setPreferredAudioLanguage("es") // Set Spanish as preferred if available
        )

        // Enable software decoder fallback for non-native audio formats (AC3, DTS, etc.)
        val renderersFactory = DefaultRenderersFactory(this)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setEnableDecoderFallback(true)

        // Require 5s to start playback, 10s after rebuffer, and keep 10s of back-buffer
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000, 
                50000, 
                5000,  // 5 seconds buffer to start playing
                10000  // 10 seconds buffer to resume after stalling
            )
            .setBackBuffer(10000, true) // 10 seconds backward buffer
            .build()

        // Build the ExoPlayer instance with the track selector, renderers, and load control
        player = ExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build().also { exoPlayer ->
            
            playerView.player = exoPlayer
            
            // Convert the raw URL string into a Media3 MediaItem
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)
            
            // Automatically start playing once enough data is buffered
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
            
            // Listener for errors: offer external player on codec errors
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    
                    val errorMsg = error.message ?: ""
                    val isCodecError = errorMsg.contains("EXCEEDS_CAPABILITIES", ignoreCase = true) ||
                        errorMsg.contains("MediaCodecVideoRenderer", ignoreCase = true) ||
                        errorMsg.contains("Decoder init failed", ignoreCase = true) ||
                        errorMsg.contains("format_supported=NO", ignoreCase = true) ||
                        error.errorCode == androidx.media3.common.PlaybackException.ERROR_CODE_DECODING_FAILED ||
                        error.errorCode == androidx.media3.common.PlaybackException.ERROR_CODE_DECODER_INIT_FAILED
                    
                    if (isCodecError) {
                        // Offer to open in external player (VLC/MX Player)
                        showExternalPlayerDialog(
                            "⚠️ Codec Unsupported",
                            "This video uses a codec your device can't decode internally (HEVC 4K).\n\nOpen in an external player like VLC or MX Player?"
                        )
                    } else {
                        Toast.makeText(
                            this@VideoPlayerActivity,
                            "Stream failed: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }
    }

    /**
     * Shows a dialog offering to open the current video in an external player.
     * Falls back to a direct intent if only one player is available.
     */
    private fun showExternalPlayerDialog(title: String, message: String) {
        val url = currentUrl ?: return

        android.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Open External Player") { _, _ ->
                launchExternalPlayer(url)
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Launches the video in an external player using ACTION_VIEW.
     * Works with VLC, MX Player, Just Player, mpv, etc.
     */
    private fun launchExternalPlayer(url: String) {
        try {
            // Release internal player resources
            player?.release()
            player = null

            val uri = if (url.startsWith("/")) {
                // Local file path (torrent stream) — use FileProvider or file:// URI
                val file = java.io.File(url)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    androidx.core.content.FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        file
                    )
                } else {
                    Uri.fromFile(file)
                }
            } else {
                // Remote URL (http/https)
                Uri.parse(url)
            }

            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/*")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Check if any app can handle this
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                Toast.makeText(this, "🎬 Opening in external player...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "❌ No external player found. Install VLC from the Fire TV app store.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching player: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        super.onStop()
        // Pause the video if the user hits the "Home" button on the FireStick
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Free up FireStick memory when the activity is closed
        player?.release()
        player = null
    }

    // Forward FireStick remote keys to the player controls
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            playerView.showController()
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // Long-press Menu: offer to open in external player
            currentUrl?.let { url ->
                showExternalPlayerDialog(
                    "🎬 External Player",
                    "Open this video in VLC or another installed player?"
                )
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
