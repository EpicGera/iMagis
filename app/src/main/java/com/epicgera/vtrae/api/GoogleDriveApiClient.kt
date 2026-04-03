// FILE_PATH: app/src/main/java/com/epicgera/vtrae/api/GoogleDriveApiClient.kt
// ACTION: CREATE
// DESCRIPTION: Google Drive API v3 client for listing video files in a public folder
// ---------------------------------------------------------
package com.epicgera.vtrae.api

import android.content.Context
import com.epicgera.vtrae.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

// ── DATA MODELS ────────────────────────────────────────────

data class DriveFileListResponse(
    val files: List<DriveFile> = emptyList(),
    val nextPageToken: String? = null
)

data class DriveFile(
    val id: String,
    val name: String,
    val mimeType: String?,
    val size: String?,           // bytes as string
    val thumbnailLink: String?,
    val webContentLink: String?  // direct download URL
)

/**
 * UI-friendly wrapper for a Drive video file.
 */
data class DriveVideoFile(
    val id: String,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val thumbnailUrl: String?,
    val streamUrl: String     // direct playback URL
) {
    val displayTitle: String
        get() = name
            .substringBeforeLast(".")  // strip extension
            .replace("_", " ")
            .replace(".", " ")
            .trim()

    val displaySize: String
        get() {
            val mb = sizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1024) {
                String.format("%.1f GB", mb / 1024.0)
            } else {
                String.format("%.0f MB", mb)
            }
        }
}

// ── RETROFIT SERVICE ───────────────────────────────────────

interface GoogleDriveApi {
    @GET("files")
    suspend fun listFiles(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("fields") fields: String = "files(id,name,mimeType,size,thumbnailLink,webContentLink),nextPageToken",
        @Query("pageSize") pageSize: Int = 100,
        @Query("orderBy") orderBy: String = "name",
        @Query("pageToken") pageToken: String? = null
    ): DriveFileListResponse
}

// ── SINGLETON CLIENT ───────────────────────────────────────

object GoogleDriveApiClient {

    private const val BASE_URL = "https://www.googleapis.com/drive/v3/"

    val API_KEY: String = BuildConfig.GOOGLE_DRIVE_API_KEY
    val FOLDER_ID: String = BuildConfig.GOOGLE_DRIVE_FOLDER_ID

    lateinit var service: GoogleDriveApi
        private set

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return

        val cacheDir = File(context.cacheDir, "drive_cache")
        val cache = Cache(cacheDir, 5L * 1024 * 1024) // 5MB

        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDriveApi::class.java)

        initialized = true
    }

    /**
     * Fetches all video files from the configured Drive folder.
     */
    suspend fun listVideos(): List<DriveVideoFile> {
        val query = "'$FOLDER_ID' in parents and mimeType contains 'video' and trashed = false"
        val response = service.listFiles(query = query, apiKey = API_KEY)

        return response.files.mapNotNull { file ->
            val streamUrl = "https://drive.google.com/uc?export=download&id=${file.id}"
            DriveVideoFile(
                id = file.id,
                name = file.name,
                mimeType = file.mimeType ?: "video/mp4",
                sizeBytes = file.size?.toLongOrNull() ?: 0L,
                thumbnailUrl = file.thumbnailLink,
                streamUrl = streamUrl
            )
        }
    }
}
