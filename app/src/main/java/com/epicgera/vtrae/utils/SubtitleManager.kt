// FILE_PATH: app/src/main/java/com/epicgera/vtrae/utils/SubtitleManager.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.utils

import android.content.Context
import android.util.Log
import com.epicgera.vtrae.api.OpenSubtitlesClient
import com.epicgera.vtrae.api.OsDownloadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

/**
 * Orchestrates Spanish subtitle fetching for the video player.
 *
 * Flow: cache check → search by TMDB ID → download SRT → save to cache.
 * Uses anonymous consumer access (API-Key only, no login required).
 * All errors are swallowed silently — subtitle failure must never affect playback.
 */
object SubtitleManager {
    private const val TAG = "SubtitleManager"

    /**
     * Fetch a Spanish subtitle file (SRT) for the given content.
     *
     * @param context       Android context (for cache directory)
     * @param tmdbId        TMDB ID of the movie or TV show
     * @param contentType   "MOVIE" or "SERIES"
     * @param seasonNumber  Season number (null for movies)
     * @param episodeNumber Episode number (null for movies)
     * @return              A local File pointing to the cached SRT, or null if not available
     */
    suspend fun fetchSpanishSubtitle(
        context: Context,
        tmdbId: Int,
        contentType: String,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null
    ): File? = withContext(Dispatchers.IO) {
        try {
            // 1. Build cache key
            val cacheKey = buildCacheKey(tmdbId, seasonNumber, episodeNumber)
            val cacheDir = File(context.cacheDir, "subtitles")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val cachedFile = File(cacheDir, "$cacheKey.srt")

            // 2. Return from cache if already downloaded
            if (cachedFile.exists() && cachedFile.length() > 0) {
                Log.d(TAG, "Subtitle cache hit: ${cachedFile.name}")
                return@withContext cachedFile
            }

            // 3. Search for Spanish subtitles
            Log.d(TAG, "Searching subtitles: tmdbId=$tmdbId, type=$contentType, S=$seasonNumber E=$episodeNumber")
            val searchResponse = OpenSubtitlesClient.service.searchSubtitles(
                tmdbId = tmdbId,
                languages = "es",
                seasonNumber = if (contentType == "SERIES") seasonNumber else null,
                episodeNumber = if (contentType == "SERIES") episodeNumber else null
            )

            val results = searchResponse.data
            if (results.isNullOrEmpty()) {
                Log.d(TAG, "No Spanish subtitles found for tmdbId=$tmdbId")
                return@withContext null
            }

            // 4. Pick the best file (first result = highest download count due to ordering)
            val bestFile = results.firstNotNullOfOrNull { result ->
                result.attributes?.files?.firstOrNull()
            }
            val fileId = bestFile?.file_id
            if (fileId == null) {
                Log.w(TAG, "No file_id in search results")
                return@withContext null
            }

            Log.d(TAG, "Best subtitle match: file_id=$fileId, name=${bestFile.file_name}")

            // 5. Request download link (anonymous access — only API-Key header needed)
            val downloadResponse = OpenSubtitlesClient.service.download(
                request = OsDownloadRequest(file_id = fileId)
            )

            val downloadUrl = downloadResponse.link
            if (downloadUrl.isNullOrBlank()) {
                Log.w(TAG, "Download failed: ${downloadResponse.message}")
                return@withContext null
            }

            Log.d(TAG, "Downloading SRT from: $downloadUrl (remaining: ${downloadResponse.remaining})")

            // 6. Download the SRT file to cache
            val srtBytes = URL(downloadUrl).readBytes()
            cachedFile.writeBytes(srtBytes)

            Log.d(TAG, "Subtitle saved: ${cachedFile.name} (${srtBytes.size} bytes)")
            return@withContext cachedFile

        } catch (e: Exception) {
            Log.e(TAG, "Subtitle fetch failed (non-fatal)", e)
            return@withContext null
        }
    }

    /**
     * Build a unique cache filename from TMDB ID and optional season/episode.
     * Examples: "27205" (movie), "1396_S01E01" (TV episode)
     */
    private fun buildCacheKey(tmdbId: Int, season: Int?, episode: Int?): String {
        return if (season != null && episode != null) {
            "${tmdbId}_S${"%02d".format(season)}E${"%02d".format(episode)}"
        } else {
            tmdbId.toString()
        }
    }

    /**
     * Parse an episode label like "S01E02" into (season, episode) pair.
     * Returns null if the label doesn't match the expected format.
     */
    fun parseEpisodeLabel(label: String?): Pair<Int, Int>? {
        if (label == null) return null
        val match = Regex("""S(\d+)E(\d+)""", RegexOption.IGNORE_CASE).find(label) ?: return null
        val season = match.groupValues[1].toIntOrNull() ?: return null
        val episode = match.groupValues[2].toIntOrNull() ?: return null
        return Pair(season, episode)
    }
}
