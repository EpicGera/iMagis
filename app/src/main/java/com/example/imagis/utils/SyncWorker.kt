package com.example.imagis.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val TAG = "SyncWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting Database Sync from Worker. Clearing old data...")
            
            // Replaced manual M3U downloads loop with SyncEngine's robust handling logic
            val success = SyncEngine.syncPlaylists(appContext, forceSync = true)
            
            Log.d(TAG, "Sync Complete. SyncEngine returned: $success")

            // Update the Android TV Home Screen safely
            updateTvHomeScreen()

            Log.d(TAG, "SyncWorker completed successfully.")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker failed: ${e.localizedMessage}")
            Result.failure()
        }
    }

    private suspend fun updateTvHomeScreen() {
        try {
            TvProviderEngine.setupCustomChannel(appContext)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to update Android TV Home Screen: ${e.message}")
        }
    }
}
