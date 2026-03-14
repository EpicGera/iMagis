package com.epicgera.vtrae.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TorrentDownloadService : Service() {

    private val TAG = "TorrentService"
    private var currentMagnet: String? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_MAGNET = "EXTRA_MAGNET"

        fun startService(context: Context, magnetUri: String? = null) {
            val intent = Intent(context, TorrentDownloadService::class.java).apply {
                action = ACTION_START
                if (magnetUri != null) putExtra(EXTRA_MAGNET, magnetUri)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, TorrentDownloadService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification("Streaming Engine Active"))
        Log.d(TAG, "TorrentDownloadService Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val magnet = intent.getStringExtra(EXTRA_MAGNET)
                if (magnet != null && magnet.startsWith("magnet:?xt=urn:btih:") && magnet != currentMagnet) {
                    currentMagnet = magnet
                    Log.d(TAG, "Foreground service active for magnet: ${magnet.substring(0, 30)}...")
                } else if (magnet != null && !magnet.startsWith("magnet:?xt=urn:btih:")) {
                    Log.e(TAG, "Security error: Invalid magnet payload received in Intent.")
                }
            }
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "TorrentDownloadService Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "torrent_service",
                "P2P Streaming Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): android.app.Notification {
        return NotificationCompat.Builder(this, "torrent_service")
            .setContentTitle("VTR Æ Streaming")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()
    }
}

