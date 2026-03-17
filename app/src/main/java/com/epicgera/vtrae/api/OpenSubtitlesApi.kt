// FILE_PATH: app/src/main/java/com/epicgera/vtrae/api/OpenSubtitlesApi.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.api

import com.epicgera.vtrae.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// ── Data Classes: Search ──

data class OsSearchResponse(
    val total_count: Int?,
    val data: List<OsSubtitleResult>?
)

data class OsSubtitleResult(
    val id: String?,
    val attributes: OsSubtitleAttributes?
)

data class OsSubtitleAttributes(
    val language: String?,
    val download_count: Int?,
    val release: String?,
    val files: List<OsFileInfo>?
)

data class OsFileInfo(
    val file_id: Int?,
    val file_name: String?
)

// ── Data Classes: Download ──

data class OsDownloadRequest(
    val file_id: Int,
    val sub_format: String = "srt"
)

data class OsDownloadResponse(
    val link: String?,        // temporary download URL (valid ~3 hours)
    val file_name: String?,
    val requests: Int?,       // remaining downloads today
    val remaining: Int?,
    val message: String?
)

// ── Retrofit Service ──

interface OpenSubtitlesService {

    /**
     * Search for subtitles by TMDB ID and language.
     * For TV episodes, pass season_number and episode_number.
     */
    @GET("subtitles")
    suspend fun searchSubtitles(
        @Query("tmdb_id") tmdbId: Int,
        @Query("languages") languages: String = "es",
        @Query("season_number") seasonNumber: Int? = null,
        @Query("episode_number") episodeNumber: Int? = null,
        @Query("order_by") orderBy: String = "download_count",
        @Query("order_direction") orderDirection: String = "desc"
    ): OsSearchResponse

    /**
     * Request a temporary download link for a subtitle file.
     * With anonymous consumer access, only the API-Key header is needed (injected by interceptor).
     */
    @POST("download")
    suspend fun download(
        @Body request: OsDownloadRequest
    ): OsDownloadResponse
}

// ── Singleton Client ──

object OpenSubtitlesClient {
    private const val BASE_URL = "https://api.opensubtitles.com/api/v1/"

    val service: OpenSubtitlesService by lazy {
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Api-Key", BuildConfig.OPENSUBTITLES_API_KEY)
                .addHeader("User-Agent", "iMagis/1.0")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenSubtitlesService::class.java)
    }
}
