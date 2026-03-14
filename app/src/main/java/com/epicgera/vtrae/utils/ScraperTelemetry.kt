// FILE_PATH: app/src/main/java/com/epicgera/vtrae/utils/ScraperTelemetry.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.utils

import android.util.Log

object ScraperTelemetry {
    private const val TAG = "ScraperTelemetry"

    fun logParsingFailure(source: String, context: String, htmlSnippet: String? = null) {
        val snippetPreview = htmlSnippet?.take(200)?.replace(Regex("\\s+"), " ") ?: "null"
        val message = "Scrape failed on [$source]: $context | Snippet: $snippetPreview"
        Log.e(TAG, message)
        
        val exception = ScraperParsingException(message)
        // TODO: Integrate with Production Telemetry (FirebaseCrashlytics / Sentry / Bugsnag)
        // FirebaseCrashlytics.getInstance().recordException(exception)
    }
    
    fun logNetworkFailure(source: String, url: String, statusCode: Int) {
        val message = "Network block on [$source]: HTTP $statusCode for $url"
        Log.e(TAG, message)
        
        val exception = ScraperNetworkException(message)
        // TODO: Integrate with Production Telemetry
        // FirebaseCrashlytics.getInstance().recordException(exception)
    }
}

class ScraperParsingException(message: String) : Exception(message)
class ScraperNetworkException(message: String) : Exception(message)

