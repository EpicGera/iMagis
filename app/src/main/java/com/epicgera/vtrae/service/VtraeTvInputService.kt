package com.epicgera.vtrae.service

import android.media.tv.TvInputService
import android.net.Uri
import android.view.Surface

/**
 * A stub TvInputService to allow inserting channels into the Android TV Provider.
 * This satisfies the TvProvider database requirement that the Input ID belongs to 
 * a registered TvInputService in the app's manifest, avoiding SecurityExceptions.
 */
class VtraeTvInputService : TvInputService() {
    override fun onCreateSession(inputId: String): Session {
        return object : Session(this) {
            override fun onRelease() {}
            override fun onSetSurface(surface: Surface?): Boolean = false
            override fun onSetStreamVolume(volume: Float) {}
            override fun onTune(channelUri: Uri?): Boolean = false
            override fun onSetCaptionEnabled(enabled: Boolean) {}
        }
    }
}

