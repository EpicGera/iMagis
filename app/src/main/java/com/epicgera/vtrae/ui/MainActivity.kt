// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/MainActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import com.epicgera.vtrae.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.lifecycleScope
import com.epicgera.vtrae.api.DriveVideoFile
import com.epicgera.vtrae.api.GoogleDriveApiClient
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.api.TmdbApiClient
import com.epicgera.vtrae.data.AnimeSeries
import com.epicgera.vtrae.db.AppDatabase
import com.epicgera.vtrae.db.FavoritesEntity
import com.epicgera.vtrae.db.WatchHistoryEntity
import com.epicgera.vtrae.ui.screens.CatalogScreen
import com.epicgera.vtrae.ui.screens.ContentSubTab
import com.epicgera.vtrae.ui.screens.NavCategory
import com.epicgera.vtrae.ui.screens.contentKey
import com.epicgera.vtrae.ui.theme.FlixTheme
import com.epicgera.vtrae.utils.JkanimeScraper
import com.epicgera.vtrae.utils.SyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main entry point — Compose for TV with Brutalist theme.
 * Replaces the old FragmentActivity + BrowseSupportFragment.
 */
class MainActivity : ComponentActivity() {

    // Reactive map: composite key → movie list (observed by Compose)
    // Keys: "TRENDING_MOVIES", "TRENDING_TV_SERIES", "TRENDING_ANIME",
    //        "POPULAR_MOVIES", "POPULAR_TV_SERIES", "POPULAR_ANIME",
    //        "TOP_RATED_MOVIES", "TOP_RATED_TV_SERIES", "TOP_RATED_ANIME",
    //        "MOVIES_ALL" (standalone movies tab)
    //        "SERIES_ALL" (standalone series tab)
    private val contentMap = mutableStateMapOf<String, List<Movie>>()
    private val animeDirectory = mutableStateListOf<AnimeSeries>()

    // ── CLOUD (Google Drive) STATE ─────────────────────────
    private val driveVideos = mutableStateListOf<DriveVideoFile>()
    private val driveLoading = androidx.compose.runtime.mutableStateOf(false)
    private var driveLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlixTheme {
                CatalogScreen(
                    contentMap = contentMap,
                    animeDirectory = animeDirectory,
                    driveVideos = driveVideos,
                    driveLoading = driveLoading.value,
                    onMovieClick = { movie -> handleMovieClick(movie) },
                    onAnimeSeriesClick = { series -> handleAnimeSeriesClick(series) },
                    onSearchAnime = { query -> handleSearchAnime(query) },
                    onSearchContent = { query -> handleSearchContent(query) },
                    onNavClick = { category -> handleNavClick(category) },
                    onDriveVideoClick = { video -> handleDriveVideoClick(video) },
                )
            }
        }

        // Background work
        GoogleDriveApiClient.init(this)
        initializeDatabase()
        loadTmdbContent()
        loadLocalContent()
    }

    override fun onResume() {
        super.onResume()
        // Refresh favorites & history every time user returns (e.g. after toggling a favorite)
        loadLocalContent()
    }

    // ── TMDB DATA LOADING ──────────────────────────────────
    // Animation genre ID on TMDB
    private val ANIMATION_GENRE = "16"

    private fun loadTmdbContent() {
        val api = TmdbApiClient.service
        val key = TmdbApiClient.API_KEY

        lifecycleScope.launch(Dispatchers.IO) {
            // ── TRENDING ──
            val trendingMovies = async { tryFetch { api.getTrendingMovies(key) }?.results.orEmpty() }
            val trendingTv     = async { tryFetch { api.getTrendingTv(key) }?.results.orEmpty() }
            val trendingAnime  = async {
                // Trending animation: discover movies + TV with animation genre, sorted by popularity
                val movies = tryFetch { api.discoverMovies(key, genres = ANIMATION_GENRE) }?.results.orEmpty()
                val tv     = tryFetch { api.discoverTv(key, genres = ANIMATION_GENRE) }?.results.orEmpty()
                (movies + tv).distinctBy { it.id }
            }

            // ── POPULAR ──
            val popularMovies = async { tryFetch { api.getPopularMovies(key) }?.results.orEmpty() }
            val popularTv     = async { tryFetch { api.getPopularSeries(key) }?.results.orEmpty() }
            val popularAnime  = async {
                val movies = tryFetch { api.discoverMovies(key, genres = ANIMATION_GENRE, sortBy = "popularity.desc") }?.results.orEmpty()
                val tv     = tryFetch { api.discoverTv(key, genres = ANIMATION_GENRE, sortBy = "popularity.desc") }?.results.orEmpty()
                (movies + tv).distinctBy { it.id }
            }

            // ── TOP RATED ──
            val topRatedMovies = async { tryFetch { api.getTopRatedMovies(key) }?.results.orEmpty() }
            val topRatedTv     = async { tryFetch { api.getTopRatedSeries(key) }?.results.orEmpty() }
            val topRatedAnime  = async {
                val movies = tryFetch { api.discoverMovies(key, genres = ANIMATION_GENRE, sortBy = "vote_average.desc") }?.results.orEmpty()
                val tv     = tryFetch { api.discoverTv(key, genres = ANIMATION_GENRE, sortBy = "vote_average.desc") }?.results.orEmpty()
                (movies + tv).distinctBy { it.id }
            }

            // ── MOVIES standalone ──
            val moviesAll = async {
                val popular  = tryFetch { api.getPopularMovies(key) }?.results.orEmpty()
                val topRated = tryFetch { api.getTopRatedMovies(key) }?.results.orEmpty()
                (popular + topRated).distinctBy { it.id }
            }

            // ── SERIES standalone ──
            val seriesAll = async {
                val popular  = tryFetch { api.getPopularSeries(key) }?.results.orEmpty()
                val topRated = tryFetch { api.getTopRatedSeries(key) }?.results.orEmpty()
                (popular + topRated).distinctBy { it.id }
            }

            // Await all and publish to UI
            withContext(Dispatchers.Main) {
                contentMap[contentKey(NavCategory.TRENDING, ContentSubTab.MOVIES)]    = trendingMovies.await()
                contentMap[contentKey(NavCategory.TRENDING, ContentSubTab.TV_SERIES)] = trendingTv.await()
                contentMap[contentKey(NavCategory.TRENDING, ContentSubTab.ANIME)]     = trendingAnime.await()

                contentMap[contentKey(NavCategory.POPULAR, ContentSubTab.MOVIES)]    = popularMovies.await()
                contentMap[contentKey(NavCategory.POPULAR, ContentSubTab.TV_SERIES)] = popularTv.await()
                contentMap[contentKey(NavCategory.POPULAR, ContentSubTab.ANIME)]     = popularAnime.await()

                contentMap[contentKey(NavCategory.TOP_RATED, ContentSubTab.MOVIES)]    = topRatedMovies.await()
                contentMap[contentKey(NavCategory.TOP_RATED, ContentSubTab.TV_SERIES)] = topRatedTv.await()
                contentMap[contentKey(NavCategory.TOP_RATED, ContentSubTab.ANIME)]     = topRatedAnime.await()

                contentMap["MOVIES_ALL"] = moviesAll.await()
                contentMap["SERIES_ALL"] = seriesAll.await()
            }

            // ── ANIME (Jkanime) ──
            tryFetch { JkanimeScraper.getAnimeDirectory(1) }?.let { response ->
                withContext(Dispatchers.Main) {
                    animeDirectory.clear()
                    animeDirectory.addAll(response)
                }
            }
        }
    }

    // ── ANIME SEARCH (Jkanime) ─────────────────────────────

    private fun handleSearchAnime(query: String) {
        if (query.trim().isEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                tryFetch { JkanimeScraper.getAnimeDirectory(1) }?.let { response ->
                    withContext(Dispatchers.Main) {
                        animeDirectory.clear()
                        animeDirectory.addAll(response)
                    }
                }
            }
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            tryFetch { JkanimeScraper.searchAnime(query) }?.let { response ->
                withContext(Dispatchers.Main) {
                    animeDirectory.clear()
                    animeDirectory.addAll(response)
                }
            }
        }
    }

    // ── TMDB CONTENT SEARCH ────────────────────────────────

    private var searchJob: Job? = null

    private fun handleSearchContent(query: String) {
        searchJob?.cancel()

        if (query.trim().isEmpty()) {
            loadTmdbContent()
            return
        }

        searchJob = lifecycleScope.launch(Dispatchers.IO) {
            delay(350) // debounce

            val api = TmdbApiClient.service
            val key = TmdbApiClient.API_KEY

            // Multi-search across all content types
            tryFetch { api.searchMulti(key, query) }?.let { response ->
                val allResults = response.results
                val tvShows = allResults.filter { it.title == null && it.name != null }
                val movies  = allResults.filter { it.title != null }
                val anime   = allResults.filter { it.genre_ids?.contains(16) == true }

                withContext(Dispatchers.Main) {
                    // Populate all sub-tab keys with search results
                    for (cat in listOf(NavCategory.TRENDING, NavCategory.POPULAR, NavCategory.TOP_RATED)) {
                        contentMap[contentKey(cat, ContentSubTab.MOVIES)]    = movies
                        contentMap[contentKey(cat, ContentSubTab.TV_SERIES)] = tvShows
                        contentMap[contentKey(cat, ContentSubTab.ANIME)]     = anime
                    }
                    contentMap["MOVIES_ALL"] = movies
                    contentMap["SERIES_ALL"] = tvShows
                }
            }
        }
    }

    private suspend fun <T> tryFetch(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ── NAVIGATION HANDLERS ────────────────────────────────

    private fun handleMovieClick(movie: Movie) {
        val isTvShow = !movie.name.isNullOrBlank() && movie.title.isNullOrBlank()
        
        if (isTvShow) {
            // TV Show → Seasons
            val intent = Intent(this, TvSeasonsActivity::class.java)
            intent.putExtra("TV_SHOW_ID", movie.id)
            intent.putExtra("TV_SHOW_NAME", movie.name)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            // Movie → Details
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("MOVIE_EXTRA", movie)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun handleAnimeSeriesClick(series: AnimeSeries) {
        val intent = Intent(this, AnimeSeriesActivity::class.java)
        intent.putExtra("SERIES_URL", series.seriesUrl)
        intent.putExtra("SERIES_TITLE", series.title)
        intent.putExtra("SERIES_TYPE", series.type)
        intent.putExtra("SERIES_IMAGE", series.imageUrl)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun handleNavClick(category: NavCategory) {
        when (category) {
            NavCategory.LIVE_TV -> {
                startActivity(Intent(this, IptvActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            NavCategory.DOWNLOADS -> {
                // Cloud tab: lazy-load Drive videos on first click
                loadDriveVideos()
            }
            NavCategory.SETTINGS -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            NavCategory.FAVORITES,
            NavCategory.HISTORY -> {
                // Refresh from Room when user clicks these sidebar entries
                loadLocalContent()
            }
            else -> { /* content categories update the grid via state */ }
        }
    }

    // ── GOOGLE DRIVE CLOUD ─────────────────────────────────

    private fun loadDriveVideos(forceRefresh: Boolean = false) {
        if (driveLoaded && !forceRefresh) return
        driveLoading.value = true

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val videos = GoogleDriveApiClient.listVideos()
                withContext(Dispatchers.Main) {
                    driveVideos.clear()
                    driveVideos.addAll(videos)
                    driveLoaded = true
                    driveLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    driveLoading.value = false
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(
                        getString(R.string.cloud_video_error, e.message)
                    )
                }
            }
        }
    }

    private fun handleDriveVideoClick(video: DriveVideoFile) {
        val intent = Intent(this, VlcPlayerActivity::class.java)
        intent.putExtra("VIDEO_URL", video.streamUrl)
        intent.putExtra("TITLE", video.displayTitle)
        intent.putExtra("FILENAME", video.name)  // original filename for subtitle search
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // ── LOCAL DB CONTENT (Favorites + History) ──────────────

    private fun loadLocalContent() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MainActivity)

            // Favorites → Movie stubs
            val favorites = db.favoritesDao().getAllFavorites().map { it.toMovie() }

            // Watch History → Movie stubs (most recent first)
            val history = db.watchHistoryDao().getAll().map { it.toMovie() }

            withContext(Dispatchers.Main) {
                contentMap["FAVORITES_ALL"] = favorites
                contentMap["HISTORY_ALL"] = history
            }
        }
    }

    // ── Entity → Movie Converters ───────────────────────────

    private fun FavoritesEntity.toMovie(): Movie {
        val relativePath = posterUrl
            .substringAfter("/t/p/w500", "")
            .takeIf { it.isNotEmpty() }
        return Movie(
            id = id.toIntOrNull() ?: id.hashCode(),
            title = title,
            name = null,
            overview = "",
            poster_path = relativePath,
            backdrop_path = null,
            genre_ids = null
        )
    }

    private fun WatchHistoryEntity.toMovie(): Movie {
        val relativePath = posterUrl
            ?.substringAfter("/t/p/w500", "")
            ?.takeIf { it.isNotEmpty() }
        return Movie(
            id = id.toIntOrNull() ?: id.hashCode(),
            title = title,
            name = null,
            overview = episodeLabel ?: "",
            poster_path = relativePath,
            backdrop_path = null,
            genre_ids = null
        )
    }

    // ── DATABASE SYNC ──────────────────────────────────────

    private fun initializeDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val success = SyncEngine.syncPlaylists(this@MainActivity, forceSync = false)
            if (!success) {
                withContext(Dispatchers.Main) {
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(
                        getString(R.string.error_sync_failed)
                    )
                }
            }
        }
    }
}

