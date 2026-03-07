package com.example.imagis.utils

import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.tv.TvContract
import android.net.Uri
import android.util.Log

object TvProviderEngine {
    private const val TAG = "TvProviderEngine"
    
    // Replace this with your actual TvInputService component if named differently
    private const val TV_INPUT_SERVICE_CLASS = "com.example.imagis.service.ImagisTvInputService"

    /**
     * Generates the Input ID specific to our app. This is the key to bypassing the SecurityException.
     */
    private fun getInputId(context: Context): String {
        val componentName = ComponentName(context.packageName, TV_INPUT_SERVICE_CLASS)
        return TvContract.buildInputId(componentName)
    }

    /**
     * Safely queries the Android TV database ONLY for channels owned by our app.
     */
    fun getChannelId(context: Context): Long {
        val inputId = getInputId(context)
        
        // CRITICAL FIX: Scoping the URI to our specific Input ID prevents the SecurityException
        // We use TvContract.buildChannelsUriForInput() to query only our app's channels
        val channelsUri = TvContract.buildChannelsUriForInput(inputId)
        
        val projection = arrayOf(TvContract.Channels._ID)
        
        context.contentResolver.query(
            channelsUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndex(TvContract.Channels._ID)
                if (idIndex != -1) {
                    return cursor.getLong(idIndex)
                }
            }
        }
        return -1L
    }

    /**
     * Publishes a master channel for iMagis to the Android TV Home Screen.
     */
    suspend fun setupCustomChannel(context: Context) {
        val channelId = getChannelId(context)
        
        if (channelId != -1L) {
            Log.d(TAG, "Channel already exists with ID: $channelId")
            return
        }

        val inputId = getInputId(context)
        val values = ContentValues().apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, "iMagis Live TV")
            put(TvContract.Channels.COLUMN_DESCRIPTION, "Global IPTV Channels")
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_OTHER)
            // Required for the channel to show up on the home screen
            put(TvContract.Channels.COLUMN_BROWSABLE, 1) 
            put(TvContract.Channels.COLUMN_SEARCHABLE, 1)
        }

        try {
            // We insert into the overarching content URI, but it's bound by COLUMN_INPUT_ID 
            // under the hood, so this is permitted without SecurityException.
            val uri = context.contentResolver.insert(TvContract.Channels.CONTENT_URI, values)
            Log.d(TAG, "Successfully created custom Android TV channel: $uri")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException during channel insertion: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup custom channel: ${e.message}", e)
        }
    }
}
