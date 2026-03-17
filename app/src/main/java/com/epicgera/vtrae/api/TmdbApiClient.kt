package com.epicgera.vtrae.api

import com.epicgera.vtrae.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable

// --- Streaming Platform Definitions ---
enum class StreamingPlatform(
    val providerId: Int,
    val displayName: String,
    val emoji: String,
    val iconRes: Int
) {
    NETFLIX(8, "Netflix", "\uD83D\uDD34", R.drawable.ic_netflix),
    AMAZON_PRIME(9, "Amazon Prime", "\uD83D\uDD35", R.drawable.ic_prime),
    HULU(15, "Hulu", "\uD83D\udfe2", R.drawable.ic_hulu),
    APPLE_TV_PLUS(350, "Apple TV+", "\u26aa", R.drawable.ic_appletv),
    DISNEY_PLUS(337, "Disney+", "\uD83D\uDFE3", R.drawable.ic_disneyplus),
    MAX(1899, "Max", "\uD83D\uDFE1", R.drawable.ic_max),
    PARAMOUNT_PLUS(531, "Paramount+", "\uD83D\uDD35", R.drawable.ic_paramount),
    PEACOCK(386, "Peacock", "\uD83D\uDFE0", R.drawable.ic_peacock),
    CRUNCHYROLL(283, "Crunchyroll", "\uD83D\uDFE0", R.drawable.ic_crunchyroll),
    STARZ(43, "Starz", "\u26AB", R.drawable.ic_starz);
}

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
    val genre_ids: List<Int>?,
    val release_date: String? = null,
    val first_air_date: String? = null,
    val vote_average: Double? = null,
) : Serializable {
    // TMDB only returns the image path, we need to append the base URL
    val fullPosterUrl: String
        get() = if (poster_path != null) "https://image.tmdb.org/t/p/w500$poster_path" else ""

    val fullBackdropUrl: String
        get() = if (backdrop_path != null) "https://image.tmdb.org/t/p/w1280$backdrop_path" else fullPosterUrl

    val displayTitle: String
        get() = title ?: name ?: "Unknown"

    val displayYear: String
        get() = (release_date ?: first_air_date)?.take(4) ?: ""

    val displayRating: String
        get() = vote_average?.let { String.format("%.1f", it) } ?: ""
}

// --- API Interface ---
interface TmdbService {
    @GET("trending/all/week")
    suspend fun getTrending(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("trending/tv/week")
    suspend fun getTrendingTv(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_watch_providers") watchProviders: String? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("page") page: Int = 1
    ): TmdbResponse

    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_watch_providers") watchProviders: String? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("page") page: Int = 1
    ): TmdbResponse

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbResponse

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): TmdbResponse
    
    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("api_key") apiKey: String
    ): TmdbResponse

    @GET("tv/airing_today")
    suspend fun getAiringTodaySeries(
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
    
    @GET("tv/{tv_id}/external_ids")
    suspend fun getTvExternalIds(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): ExternalIdsResponse
}

// --- TV Show Data Models ---

data class ExternalIdsResponse(
    val imdb_id: String?
)

// --- TV Show Data Models ---

data class TvShowDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val seasons: List<Season>,
    val backdrop_path: String? = null
) {
    val fullBackdropUrl: String
        get() = if (backdrop_path != null) "https://image.tmdb.org/t/p/w1280$backdrop_path" else ""
}

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

