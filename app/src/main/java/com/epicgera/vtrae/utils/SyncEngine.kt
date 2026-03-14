package com.epicgera.vtrae.utils

import android.content.Context
import android.util.Log
import com.epicgera.vtrae.db.AppDatabase
import com.epicgera.vtrae.db.ChannelEntity
import com.epicgera.vtrae.data.ChannelStore
import com.epicgera.vtrae.data.PlaylistSource
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object SyncEngine {

    private const val TAG = "SyncEngine"

    suspend fun syncPlaylists(context: Context, forceSync: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val channelDao = db.channelDao()

        val currentCount = channelDao.getChannelCount()
        if (currentCount > 0 && !forceSync) {
            Log.d(TAG, "Database already has $currentCount channels. Skipping sync.")
            return@withContext true
        }

        Log.d(TAG, "Starting Database Sync. Clearing old data...")
        if (forceSync) {
            channelDao.nukeTable()
        }

        var totalInserted = 0

        // 1. Gather our default "Safe" playlists
        val allPlaylists = ChannelStore.preLoadedPlaylists.toMutableList()
        
        // 2. Read the "Master Gist" URL the user typed in Settings
        // SharedPreferences for tracking sync state
        val sharedPref = context.getSharedPreferences("VtraePrefs", Context.MODE_PRIVATE)
        var masterUrl = sharedPref.getString("CUSTOM_M3U_URL", "")

        // Fallback to default if empty
        if (masterUrl.isNullOrEmpty()) {
             // Default Master List provided by user (EpicGera)
             masterUrl = "https://gist.githubusercontent.com/EpicGera/4a683986f4d675659599c79eee7bce5c/raw"
             Log.d(TAG, "Using Default Master URL (Fallback)")
        }
        
        if (!masterUrl.isNullOrEmpty() && masterUrl.startsWith("http")) {
            Log.d(TAG, "Fetching Master List from: $masterUrl")
            
            try {
                // Connect to your Pastebin/Gist
                val masterConnection = URL(masterUrl).openConnection() as HttpURLConnection
                masterConnection.connectTimeout = 5000
                masterConnection.readTimeout = 5000
                
                if (masterConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    // Read your text file line by line
                    val masterContent = masterConnection.inputStream.bufferedReader().readLines()
                    
                    var listCounter = 1
                    for (line in masterContent) {
                        val cleanLine = line.trim()
                        // If the line is a URL, add it to our download queue
                        if (cleanLine.startsWith("http")) {
                            allPlaylists.add(
                                PlaylistSource(
                                    name = "Remote List $listCounter", 
                                    url = cleanLine, 
                                    description = "Managed via Master URL"
                                )
                            )
                            listCounter++
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to reach Master URL. Code: ${masterConnection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing Master URL: ${e.message}")
            }
        }

        // 3. Loop through every single list (Defaults + Everything found in your Pastebin)
        for (playlist in allPlaylists) {
            try {
                Log.d(TAG, "Downloading M3U: ${playlist.name} from ${playlist.url}")
                
                val m3uContent = if (playlist.url.startsWith("android.resource://")) {
                    val uri = android.net.Uri.parse(playlist.url)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                } else {
                    val connection = URL(playlist.url).openConnection() as HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 15000 // 15 seconds for massive VOD lists
                    
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        Log.e(TAG, "HTTP Error ${connection.responseCode} for ${playlist.name}")
                        ""
                    }
                }

                if (m3uContent.isNotEmpty()) {
                    val parsedChannels = M3uParser.parse(m3uContent)
                    
                    val entitiesToInsert = parsedChannels.map { channel ->
                        ChannelEntity(
                            name = channel.name,
                            lowercaseName = channel.name.lowercase(Locale.getDefault()),
                            streamUrl = channel.streamUrl,
                            logoUrl = channel.logoUrl,
                            groupName = channel.group.takeIf { !it.isNullOrEmpty() } ?: playlist.name,
                            isSeries = channel.isSeries,
                            season = channel.season,
                            episode = channel.episode
                        )
                    }

                    db.withTransaction {
                        channelDao.insertAll(entitiesToInsert)
                    }
                    totalInserted += entitiesToInsert.size
                    
                    Log.d(TAG, "Successfully inserted ${entitiesToInsert.size} items from ${playlist.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync ${playlist.name}: ${e.message}")
            }
        }

        Log.d(TAG, "Sync Complete. Total channels/VODs in database: $totalInserted")
        return@withContext totalInserted > 0
    }
}

