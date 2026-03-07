package com.example.imagis.data

import android.content.Context
import android.util.Log
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import com.example.imagis.service.TorrentDownloadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class DownloadState(
    val title: String,
    val progress: Float,
    val seeds: Int,
    val downloadSpeedBytes: Float,
    val videoFile: File?,
    val buffering: Boolean,
    val error: String? = null
)

object TorrentRepository : TorrentListener {
    private var torrentStream: TorrentStream? = null
    private var currentTitle: String = ""

    private val _downloadState = MutableStateFlow<DownloadState?>(null)
    val downloadState: StateFlow<DownloadState?> = _downloadState.asStateFlow()

    fun initialize(context: Context) {
        if (torrentStream != null) return

        val torrentOptions = TorrentOptions.Builder()
            .saveLocation(context.cacheDir)
            .removeFilesAfterStop(true)
            .autoDownload(true)
            .prepareSize(15 * 1024 * 1024L) // Require 15MB buffered before firing onStreamPrepared
            .build()

        torrentStream = TorrentStream.init(torrentOptions)
        torrentStream?.addListener(this)
    }

    fun startStream(context: Context, magnetUrl: String, title: String) {
        currentTitle = title
        
        // Ensure service is running
        TorrentDownloadService.startService(context)
        
        if (torrentStream?.isStreaming == true) {
            torrentStream?.stopStream()
        }
        
        _downloadState.value = DownloadState(
            title = currentTitle,
            progress = 0f,
            seeds = 0,
            downloadSpeedBytes = 0f,
            videoFile = null,
            buffering = true
        )
        
        torrentStream?.startStream(magnetUrl)
    }

    fun stopStream(context: Context) {
        torrentStream?.stopStream()
        currentTitle = ""
        _downloadState.value = null
        TorrentDownloadService.stopService(context)
    }

    fun currentTorrent(): Torrent? {
        return torrentStream?.currentTorrent
    }

    override fun onStreamPrepared(torrent: Torrent?) {
        Log.d("TorrentRepository", "Stream Prepared!")
        val current = _downloadState.value
        if (current != null) {
            _downloadState.value = current.copy(videoFile = torrent?.videoFile)
        }
    }

    override fun onStreamStarted(torrent: Torrent?) {
        Log.d("TorrentRepository", "Stream Started!")
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Log.e("TorrentRepository", "Stream Error: ${e?.message}")
        val current = _downloadState.value
        if (current != null) {
            _downloadState.value = current.copy(error = e?.message)
        }
    }

    override fun onStreamReady(torrent: Torrent?) {
        Log.d("TorrentRepository", "Stream Ready! Local File: ${torrent?.videoFile?.absolutePath}")
        val current = _downloadState.value
        if (current != null) {
            _downloadState.value = current.copy(videoFile = torrent?.videoFile, buffering = false)
        }
    }

    override fun onStreamProgress(torrent: Torrent?, status: StreamStatus?) {
        val current = _downloadState.value ?: return
        _downloadState.value = current.copy(
            progress = status?.progress?.toFloat() ?: current.progress,
            seeds = status?.seeds ?: current.seeds,
            downloadSpeedBytes = status?.downloadSpeed?.toFloat() ?: current.downloadSpeedBytes
        )
    }

    override fun onStreamStopped() {
        Log.d("TorrentRepository", "Stream Stopped")
        _downloadState.value = null
    }
}
