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

    // ── FILENAME-BASED SUBTITLE SEARCH (for Cloud/Drive files) ──

    /**
     * Parsed video filename metadata.
     */
    data class ParsedFilename(
        val title: String,           // "Breaking Bad" or "Inception"
        val season: Int? = null,
        val episode: Int? = null,
        val year: Int? = null,
        val isEpisode: Boolean = false
    )

    /**
     * Parse a video filename into structured metadata.
     * Handles patterns:
     *   "Breaking.Bad.S01E02.720p.BluRay.mkv" → title="Breaking Bad", S=1, E=2
     *   "Inception.2010.1080p.mkv" → title="Inception", year=2010
     *   "The_Movie_Name.mp4" → title="The Movie Name"
     */
    fun parseVideoFilename(filename: String): ParsedFilename {
        // Strip extension
        val name = filename.substringBeforeLast(".")

        // Try to match S01E02 pattern
        val episodeMatch = Regex("""(.+?)[.\s_-]+[Ss](\d{1,2})[Ee](\d{1,2})""").find(name)
        if (episodeMatch != null) {
            val title = episodeMatch.groupValues[1]
                .replace(".", " ").replace("_", " ").trim()
            return ParsedFilename(
                title = title,
                season = episodeMatch.groupValues[2].toIntOrNull(),
                episode = episodeMatch.groupValues[3].toIntOrNull(),
                isEpisode = true
            )
        }

        // Try to match "1x02" pattern (alternative episode format)
        val altEpisodeMatch = Regex("""(.+?)[.\s_-]+(\d{1,2})x(\d{1,2})""").find(name)
        if (altEpisodeMatch != null) {
            val title = altEpisodeMatch.groupValues[1]
                .replace(".", " ").replace("_", " ").trim()
            return ParsedFilename(
                title = title,
                season = altEpisodeMatch.groupValues[2].toIntOrNull(),
                episode = altEpisodeMatch.groupValues[3].toIntOrNull(),
                isEpisode = true
            )
        }

        // Try to match year (movie)
        val yearMatch = Regex("""(.+?)[.\s_-]+((?:19|20)\d{2})""").find(name)
        if (yearMatch != null) {
            val title = yearMatch.groupValues[1]
                .replace(".", " ").replace("_", " ").trim()
            return ParsedFilename(
                title = title,
                year = yearMatch.groupValues[2].toIntOrNull()
            )
        }

        // Fallback: clean up filename as title, cut at quality tags
        val cleaned = name
            .replace(Regex("""[.\s_-]+(720p|1080p|2160p|4K|BluRay|WEBRip|HDRip|BRRip|HDTV|x264|x265|HEVC|AAC|AC3|DTS|REMUX).*""", RegexOption.IGNORE_CASE), "")
            .replace(".", " ").replace("_", " ").trim()

        return ParsedFilename(title = cleaned)
    }

    /**
     * Fetch subtitles by parsing the video filename.
     * Used for Cloud/Drive files where no TMDB ID is available.
     */
    suspend fun fetchSubtitleByFilename(
        context: Context,
        filename: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            val parsed = parseVideoFilename(filename)
            Log.d(TAG, "Parsed filename: title='${parsed.title}', S=${parsed.season}, E=${parsed.episode}, year=${parsed.year}")

            // Build cache key from parsed name
            val cacheKey = "cloud_${parsed.title.replace(" ", "_")}" +
                    (if (parsed.season != null) "_S${"%02d".format(parsed.season)}" else "") +
                    (if (parsed.episode != null) "E${"%02d".format(parsed.episode)}" else "") +
                    (if (parsed.year != null) "_${parsed.year}" else "")

            val cacheDir = File(context.cacheDir, "subtitles")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val cachedFile = File(cacheDir, "$cacheKey.srt")

            // Return from cache if already downloaded
            if (cachedFile.exists() && cachedFile.length() > 0) {
                Log.d(TAG, "Subtitle cache hit: ${cachedFile.name}")
                return@withContext cachedFile
            }

            // Search by query text
            val queryText = if (parsed.year != null) "${parsed.title} ${parsed.year}" else parsed.title

            Log.d(TAG, "Searching subtitles by query: '$queryText'")
            val searchResponse = OpenSubtitlesClient.service.searchByQuery(
                query = queryText,
                languages = "es",
                seasonNumber = parsed.season,
                episodeNumber = parsed.episode
            )

            val results = searchResponse.data
            if (results.isNullOrEmpty()) {
                Log.d(TAG, "No Spanish subtitles found for: $queryText")
                return@withContext null
            }

            // Pick the best file
            val bestFile = results.firstNotNullOfOrNull { it.attributes?.files?.firstOrNull() }
            val fileId = bestFile?.file_id ?: return@withContext null

            Log.d(TAG, "Best subtitle: file_id=$fileId, name=${bestFile.file_name}")

            // Download
            val downloadResponse = OpenSubtitlesClient.service.download(
                request = OsDownloadRequest(file_id = fileId)
            )
            val downloadUrl = downloadResponse.link ?: return@withContext null

            val srtBytes = URL(downloadUrl).readBytes()
            cachedFile.writeBytes(srtBytes)

            Log.d(TAG, "Subtitle saved: ${cachedFile.name} (${srtBytes.size} bytes)")
            return@withContext cachedFile

        } catch (e: Exception) {
            Log.e(TAG, "Filename-based subtitle fetch failed (non-fatal)", e)
            return@withContext null
        }
    }
}
