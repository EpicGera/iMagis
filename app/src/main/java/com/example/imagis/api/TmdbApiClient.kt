package com.example.imagis.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable

// --- Data Models ---
data class TmdbResponse(
    val page: Int,
    val results: List<Movie>
)



data class Movie(
    val id: Int,
    val title: String?,
    val name: String?, // TMDB uses 'name' instead of 'title' for TV Shows
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val genre_ids: List<Int>?
) : Serializable {
    // TMDB only returns the image path, we need to append the base URL
    val fullPosterUrl: String
        get() = if (poster_path != null) "https://image.tmdb.org/t/p/w500$poster_path" else ""
        
    val displayTitle: String
        get() = title ?: name ?: "Unknown"
}

// --- API Interface ---
interface TmdbService {
    @GET("trending/all/week")
    suspend fun getTrending(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): TmdbResponse
    
    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): TvShowDetails

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String
    ): SeasonDetails
}

// --- TV Show Data Models ---

data class TvShowDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val seasons: List<Season>
)

data class Season(
    val id: Int,
    val name: String,
    val season_number: Int,
    val episode_count: Int
)

data class SeasonDetails(
    val _id: String,
    val episodes: List<Episode>
)

data class Episode(
    val id: Int,
    val name: String,
    val episode_number: Int,
    val season_number: Int,
    val overview: String,
    val still_path: String?
) {
    val fullStillUrl: String
        get() = if (still_path != null) "https://image.tmdb.org/t/p/w500$still_path" else ""
    
    val displayTitle: String
        get() = "E$episode_number: $name"
}

// --- Retrofit Client ---
object TmdbApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    
    // NOTE: You must register at themoviedb.org to get a free API Key
    const val API_KEY = "2e64c781bb9e6a77b4fbe70bf66afcac"

    val service: TmdbService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbService::class.java)
    }
}
