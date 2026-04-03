// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/VlcPlayerActivity.kt
// ACTION: CREATE
// DESCRIPTION: LibVLC-based video player for Cloud videos (MKV, AC3, EAC3, DTS, HEVC)
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.epicgera.vtrae.R
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IVLCVout

class VlcPlayerActivity : AppCompatActivity(), IVLCVout.Callback {

    private var libVlc: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var progressBar: ProgressBar
    private lateinit var titleText: TextView
    private lateinit var rootLayout: FrameLayout

    private var videoUrl: String? = null
    private var videoTitle: String? = null

    // ── Controller auto-hide ──
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { titleText.visibility = View.GONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Build layout programmatically (no XML needed)
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        surfaceView = SurfaceView(this)
        rootLayout.addView(surfaceView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        progressBar = ProgressBar(this).apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }
        val pbParams = FrameLayout.LayoutParams(120, 120).apply {
            gravity = android.view.Gravity.CENTER
        }
        rootLayout.addView(progressBar, pbParams)

        titleText = TextView(this).apply {
            setTextColor(android.graphics.Color.WHITE)
            textSize = 18f
            setPadding(32, 24, 32, 24)
            setBackgroundColor(android.graphics.Color.parseColor("#AA000000"))
            visibility = View.GONE
        }
        val titleParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = android.view.Gravity.TOP }
        rootLayout.addView(titleText, titleParams)

        setContentView(rootLayout)

        // Read intent
        videoUrl = intent.getStringExtra("VIDEO_URL")
        videoTitle = intent.getStringExtra("TITLE")

        if (videoUrl.isNullOrEmpty()) {
            finish()
            return
        }

        titleText.text = videoTitle ?: ""

        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initVlcPlayer()
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                mediaPlayer?.vlcVout?.setWindowSize(width, height)
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    private fun initVlcPlayer() {
        val vlcOptions = arrayListOf(
            "--no-drop-late-frames",
            "--no-skip-frames",
            "--rtsp-tcp",
            "-vvv",                          // verbose for debugging
            "--network-caching=3000",        // 3s network cache
            "--file-caching=3000",
            "--live-caching=3000",
            "--codec=all",                   // use ALL codecs (AC3, EAC3, DTS, etc.)
            "--avcodec-skiploopfilter=0",
            "--sout-keep",
        )

        libVlc = LibVLC(this, vlcOptions)
        mediaPlayer = MediaPlayer(libVlc!!).apply {
            // Attach to surface
            vlcVout.setVideoSurface(surfaceView.holder.surface, surfaceView.holder)
            vlcVout.setWindowSize(surfaceView.width, surfaceView.height)
            vlcVout.addCallback(this@VlcPlayerActivity)
            vlcVout.attachViews()

            // Event listener
            setEventListener { event ->
                when (event.type) {
                    MediaPlayer.Event.Playing -> {
                        runOnUiThread { progressBar.visibility = View.GONE }
                    }
                    MediaPlayer.Event.Buffering -> {
                        val pct = event.buffering
                        runOnUiThread {
                            progressBar.visibility = if (pct < 100f) View.VISIBLE else View.GONE
                        }
                    }
                    MediaPlayer.Event.EncounteredError -> {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            android.widget.Toast.makeText(
                                this@VlcPlayerActivity,
                                "VLC Error: Unable to play this file",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                    MediaPlayer.Event.EndReached -> {
                        runOnUiThread { finish() }
                    }
                    MediaPlayer.Event.Vout -> {
                        // Video output started → adjust aspect ratio
                        runOnUiThread { updateVideoSurface() }
                    }
                }
            }
        }

        // Create media and play
        val media = Media(libVlc!!, Uri.parse(videoUrl))
        media.setHWDecoderEnabled(true, false)  // prefer HW decode, fallback to SW
        media.addOption(":network-caching=3000")
        media.addOption(":clock-jitter=0")
        media.addOption(":clock-synchro=0")
        mediaPlayer!!.media = media
        media.release()
        mediaPlayer!!.play()
    }

    private fun updateVideoSurface() {
        val mp = mediaPlayer ?: return
        val vw = mp.vlcVout

        val videoWidth = mp.currentVideoTrack?.width ?: return
        val videoHeight = mp.currentVideoTrack?.height ?: return
        if (videoWidth == 0 || videoHeight == 0) return

        val screenWidth = surfaceView.width
        val screenHeight = surfaceView.height
        if (screenWidth == 0 || screenHeight == 0) return

        val videoAR = videoWidth.toFloat() / videoHeight.toFloat()
        val screenAR = screenWidth.toFloat() / screenHeight.toFloat()

        val layoutW: Int
        val layoutH: Int
        if (videoAR > screenAR) {
            layoutW = screenWidth
            layoutH = (screenWidth / videoAR).toInt()
        } else {
            layoutW = (screenHeight * videoAR).toInt()
            layoutH = screenHeight
        }

        val lp = surfaceView.layoutParams as FrameLayout.LayoutParams
        lp.width = layoutW
        lp.height = layoutH
        lp.gravity = android.view.Gravity.CENTER
        surfaceView.layoutParams = lp

        vw.setWindowSize(layoutW, layoutH)
    }

    // ── D-PAD CONTROLS ─────────────────────────────────────────

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val mp = mediaPlayer ?: return super.dispatchKeyEvent(event)

            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (mp.isPlaying) mp.pause() else mp.play()
                    showTitle()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    mp.time = mp.time + 10_000  // +10s
                    showTitle()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    mp.time = (mp.time - 10_000).coerceAtLeast(0)  // -10s
                    showTitle()
                    return true
                }
                KeyEvent.KEYCODE_BACK -> {
                    finish()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun showTitle() {
        titleText.visibility = View.VISIBLE
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 3000)
    }

    // ── IVLCVout.Callback ──────────────────────────────────────

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {}
    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {}

    // ── LIFECYCLE ──────────────────────────────────────────────

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacksAndMessages(null)
        mediaPlayer?.let {
            it.stop()
            it.vlcVout.detachViews()
            it.release()
        }
        libVlc?.release()
        mediaPlayer = null
        libVlc = null
    }
}
