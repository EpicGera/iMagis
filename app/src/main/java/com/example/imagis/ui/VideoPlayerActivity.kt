// FILE_PATH: app/src/main/java/com/example/imagis/ui/VideoPlayerActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
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
import com.example.imagis.db.AppDatabase
import com.example.imagis.db.WatchHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var currentUrl: String? = null

    // ── Watch History metadata (passed via intent extras) ──
    private var contentId: String? = null
    private var contentTitle: String? = null
    private var episodeLabel: String? = null
    private var posterUrl: String? = null
    private var contentType: String? = null
    private var resumePositionMs: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // CRITICAL FOR TV: Prevent the screen from going to sleep while a movie is playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.video_view)
        
        // Retrieve the URL passed from our Details Screen
        val videoUrl = intent.getStringExtra("VIDEO_URL")
        
        if (videoUrl.isNullOrEmpty()) {
            Toast.makeText(this, R.string.error_no_video_source, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentUrl = videoUrl

        // Read watch history metadata
        contentId = intent.getStringExtra("CONTENT_ID") ?: videoUrl
        contentTitle = intent.getStringExtra("TITLE") ?: "Unknown"
        episodeLabel = intent.getStringExtra("EPISODE_LABEL")
        posterUrl = intent.getStringExtra("POSTER_URL")
        contentType = intent.getStringExtra("CONTENT_TYPE") ?: "MOVIE"
        resumePositionMs = intent.getLongExtra("RESUME_POSITION_MS", 0L)

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

            // Resume from saved position if available
            if (resumePositionMs > 0L) {
                exoPlayer.seekTo(resumePositionMs)
            }
            
            // Listener for errors: offer external player on codec errors
            exoPlayer.addListener(object : Player.Listener {
                
                override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                    super.onTracksChanged(tracks)
                    var hasAudioTrack = false
                    var isAudioSupported = false
                    
                    for (group in tracks.groups) {
                        if (group.type == androidx.media3.common.C.TRACK_TYPE_AUDIO) {
                            hasAudioTrack = true
                            if (group.isSupported) {
                                isAudioSupported = true
                            }
                        }
                    }
                    
                    // If the media has audio but the device hardware cannot decode it (e.g. Dolby EAC3 on Emulator)
                    if (hasAudioTrack && !isAudioSupported) {
                        showExternalPlayerDialog(
                            "🔇 Audio Codec Unsupported",
                            "This video uses an audio format (like Dolby AC3/EAC3) that this app cannot natively decode due to licensing limits on this device.\n\nPlease open this video in an external player like VLC or MX Player to hear the audio."
                        )
                    }
                }

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
                            getString(R.string.msg_player_error, error.message),
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
                Toast.makeText(this, R.string.msg_opening_external_player, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "❌ No external player found. Install VLC from the Fire TV app store.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.msg_error_launching_player, e.message), Toast.LENGTH_LONG).show()
        }
    }

    // ── WATCH HISTORY: Save position on pause/exit ──────────

    private fun saveWatchHistory() {
        val exo = player ?: return
        val id = contentId ?: return
        val title = contentTitle ?: return

        val positionMs = exo.currentPosition
        val durationMs = exo.duration.coerceAtLeast(1L)

        // Determine status: ≥90% played → WATCHED, else ONGOING
        val status = if (durationMs > 0 && positionMs >= durationMs * 0.9) "WATCHED" else "ONGOING"

        val entry = WatchHistoryEntity(
            id = id,
            title = title,
            episodeLabel = episodeLabel,
            status = status,
            positionMs = positionMs,
            durationMs = durationMs,
            posterUrl = posterUrl,
            videoUrl = currentUrl,
            type = contentType ?: "MOVIE",
            timestamp = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(this@VideoPlayerActivity).watchHistoryDao()
                dao.upsert(entry)
                dao.trimToMax()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Save watch history BEFORE pausing
        saveWatchHistory()
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
