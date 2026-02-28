package com.example.imagis.ui

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.imagis.R
import com.example.imagis.utils.JkanimeScraper
import kotlinx.coroutines.launch

class PlayerActivity : FragmentActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var videoUrl: String? = null
    private var isVodPage: Boolean = false

    @OptIn(UnstableApi::class) 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.player_view)
        
        try {
            videoUrl = intent.getStringExtra("VIDEO_URL")
            isVodPage = intent.getBooleanExtra("IS_VOD_PAGE", false)

            if (videoUrl == null) {
                Toast.makeText(this, "Error: No Video URL provided", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            Toast.makeText(this, "Loading: $videoUrl", Toast.LENGTH_SHORT).show()

            if (isVodPage) {
                resolveVodStream(videoUrl!!)
            } else {
                initializePlayer(videoUrl!!)
            }
        } catch (e: Exception) {
             e.printStackTrace()
             Toast.makeText(this, "Player Error: ${e.message}", Toast.LENGTH_LONG).show()
             finish()
        }
    }

    private fun resolveVodStream(pageUrl: String) {
        Toast.makeText(this, "Resolving video servers...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            val servers = JkanimeScraper.getVideoServers(pageUrl)
            if (servers.isNotEmpty()) {
                val target = servers.first().embedUrl
                
                // Check if it's a direct video file we can play natively
                if (target.endsWith(".mp4") || target.endsWith(".m3u8")) {
                    initializePlayer(target)
                } else {
                    // It's a web embed (iframe/page), Use WebView
                    Toast.makeText(this@PlayerActivity, "Launching Web Player...", Toast.LENGTH_SHORT).show()
                    val intent = android.content.Intent(this@PlayerActivity, WebViewActivity::class.java)
                    intent.putExtra("VIDEO_URL", target)
                    startActivity(intent)
                    finish() // Close PlayerActivity
                }
            } else {
                Toast.makeText(this@PlayerActivity, "No video servers found", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        try {
            player = ExoPlayer.Builder(this).build()
            playerView.player = player

            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            
            player?.addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(this@PlayerActivity, "Playback Error: ${error.message}", Toast.LENGTH_LONG).show()
                    error.printStackTrace()
                }
            })

            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
             Toast.makeText(this, "ExoPlayer Init Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
