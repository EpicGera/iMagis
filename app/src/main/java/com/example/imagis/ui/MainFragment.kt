package com.example.imagis.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import com.example.imagis.R
import com.example.imagis.data.AnimeEpisode
import com.example.imagis.utils.JkanimeScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Simple data object for our Menu options
data class MenuOption(
    val title: String, 
    val description: String, 
    val iconResId: Int,
    val type: Int
)

// Data class for platform-specific menu cards
data class PlatformMenuOption(
    val title: String,
    val description: String,
    val iconResId: Int,
    val platformId: Int
)

class MainFragment : BrowseSupportFragment() {

    companion object {
        const val TYPE_ANIME = 1
        const val TYPE_LIVETV = 2
        const val TYPE_DIRECTORY = 3
        const val TYPE_SETTINGS = 4
        const val TYPE_DOWNLOADS = 5
        const val TYPE_PLATFORM = 6
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // UI Setup
        title = "MediaHub TV"
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.app_icon_placeholder)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.parseColor("#1A1A1A")

        loadRows()
        setupEventListeners()
    }

    private fun loadRows() {
        // Custom ListRowPresenter to fix "jump back" scrolling behavior
        val listRowPresenter = object : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM) {
            override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(holder, item)
                val vh = holder as ListRowPresenter.ViewHolder
                val gridView = vh.gridView
                
                // Align items to the start (left) instead of center
                gridView.windowAlignment = BaseGridView.WINDOW_ALIGN_LOW_EDGE
                gridView.windowAlignmentOffsetPercent = 2.0f // Small padding from edge
                gridView.itemAlignmentOffsetPercent = 0.0f
                
                // Prevent focus from looping/jumping
                gridView.setFocusScrollStrategy(BaseGridView.FOCUS_SCROLL_ITEM)
            }
        }
        
        // Disable shadow to make UI cleaner
        listRowPresenter.shadowEnabled = false
        
        val rowsAdapter = ArrayObjectAdapter(listRowPresenter)
        val cardPresenter = MenuCardPresenter()

        // Row 1: VOD / Anime Options
        val vodAdapter = ArrayObjectAdapter(cardPresenter)
        vodAdapter.add(MenuOption("Latest Anime", "New Episodes", R.drawable.ic_anime_placeholder, TYPE_ANIME))
        vodAdapter.add(MenuOption("Browse All", "Full Catalog", R.drawable.ic_anime_placeholder, TYPE_DIRECTORY))
        
        // Row 2: Live TV Options
        val liveAdapter = ArrayObjectAdapter(cardPresenter)
        liveAdapter.add(MenuOption("Live TV", "IPTV Channels", R.drawable.ic_tv_placeholder, TYPE_LIVETV))

        // Row 3: Settings
        val settingsAdapter = ArrayObjectAdapter(cardPresenter)
        settingsAdapter.add(MenuOption("Downloads", "Active P2P Transfers", android.R.drawable.stat_sys_download, TYPE_DOWNLOADS))
        settingsAdapter.add(MenuOption("Settings", "Add Custom M3U Playlists", android.R.drawable.ic_menu_preferences, TYPE_SETTINGS))

        // Row: Browse by Platform
        val platformAdapter = ArrayObjectAdapter(cardPresenter)
        platformAdapter.add(PlatformMenuOption("\uD83D\uDD34 Netflix", "Browse Netflix", R.drawable.ic_netflix, 8))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDD35 Amazon Prime", "Browse Prime Video", R.drawable.ic_prime, 9))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDFE2 Hulu", "Browse Hulu", R.drawable.ic_hulu, 15))
        platformAdapter.add(PlatformMenuOption("\u26AA Apple TV+", "Browse Apple TV+", R.drawable.ic_appletv, 350))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDFE3 Disney+", "Browse Disney+", R.drawable.ic_disneyplus, 337))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDFE1 Max", "Browse Max (HBO)", R.drawable.ic_max, 1899))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDD35 Paramount+", "Browse Paramount+", R.drawable.ic_paramount, 531))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDFE0 Peacock", "Browse Peacock", R.drawable.ic_peacock, 386))
        platformAdapter.add(PlatformMenuOption("\uD83D\uDFE0 Crunchyroll", "Browse Crunchyroll", R.drawable.ic_crunchyroll, 283))
        platformAdapter.add(PlatformMenuOption("\u26AB Starz", "Browse Starz", R.drawable.ic_starz, 43))

        // ** Restored: Latest Episodes Row **
        val latestEpisodesAdapter = ArrayObjectAdapter(CardPresenter()) // New Presenter for episodes
        val headerLatest = HeaderItem(0, "Latest Episodes")
        rowsAdapter.add(ListRow(headerLatest, latestEpisodesAdapter))

        // Add Platform row
        val headerPlatform = HeaderItem(4, "Browse by Platform")
        rowsAdapter.add(ListRow(headerPlatform, platformAdapter))

        // Add Menu rows
        val header1 = HeaderItem(1, "Menu")
        rowsAdapter.add(ListRow(header1, vodAdapter))

        val header2 = HeaderItem(2, "Live TV")
        rowsAdapter.add(ListRow(header2, liveAdapter))

        val header3 = HeaderItem(3, "Preferences")
        rowsAdapter.add(ListRow(header3, settingsAdapter))

        adapter = rowsAdapter
        
        // Load data in background
        loadWatchHistory(rowsAdapter)
        loadLatestEpisodes(latestEpisodesAdapter)
        loadVodContent(rowsAdapter)
        loadTmdbContent(rowsAdapter)
        loadFavorites(rowsAdapter)
    }

    private fun loadWatchHistory(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val db = com.example.imagis.db.AppDatabase.getDatabase(requireContext())
                val history = db.watchHistoryDao().getAll()

                if (history.isNotEmpty()) {
                    launch(Dispatchers.Main) {
                        val listRowAdapter = ArrayObjectAdapter(WatchHistoryCardPresenter())
                        history.forEach { listRowAdapter.add(it) }
                        val header = HeaderItem(400, "▶️ Continue Watching")
                        adapter.add(0, ListRow(header, listRowAdapter))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadLatestEpisodes(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.Main) {
            val episodes = JkanimeScraper.getLatestEpisodes()
            for (episode in episodes) {
                adapter.add(episode)
            }
        }
    }
    
    private fun loadTmdbContent(adapter: ArrayObjectAdapter) {
         GlobalScope.launch(Dispatchers.IO) {
             val api = com.example.imagis.api.TmdbApiClient.service
             val key = com.example.imagis.api.TmdbApiClient.API_KEY
             val presenter = com.example.imagis.ui.TmdbCardPresenter()

             // Helper to add a row safely
             suspend fun addRow(headerTitle: String, headerId: Long, fetcher: suspend () -> com.example.imagis.api.TmdbResponse) {
                 try {
                     val response = fetcher()
                     if (response.results.isNotEmpty()) {
                         launch(Dispatchers.Main) {
                             val rowAdapter = ArrayObjectAdapter(presenter)
                             response.results.forEach { rowAdapter.add(it) }
                             adapter.add(ListRow(HeaderItem(headerId, headerTitle), rowAdapter))
                         }
                     }
                 } catch (e: Exception) {
                     e.printStackTrace()
                 }
             }

             // Fetch all categories
             addRow("🔥 Trending Now", 200) { api.getTrending(key) }
             addRow("🎬 Popular Movies", 201) { api.getPopularMovies(key) }
             addRow("📺 Popular Series", 202) { api.getPopularSeries(key) }
             addRow("🆕 Now Playing", 203) { api.getNowPlayingMovies(key) }
             addRow("⭐ Top Rated", 204) { api.getTopRatedMovies(key) }
         }
    }

    private fun loadFavorites(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val db = com.example.imagis.db.AppDatabase.getDatabase(requireContext())
                val favorites = db.favoritesDao().getAllFavorites()

                if (favorites.isNotEmpty()) {
                    launch(Dispatchers.Main) {
                        val listRowAdapter = ArrayObjectAdapter(FavoriteCardPresenter())
                        favorites.forEach { listRowAdapter.add(it) }
                        val header = HeaderItem(300, "⭐ My Favorites")
                        adapter.add(0, ListRow(header, listRowAdapter)) // Add at top
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // List for Search/"Magic Linkage" is now in ChannelStore
    // private val allVodItems = java.util.Collections.synchronizedList(mutableListOf<com.example.imagis.data.VodContent>())

    // --- TMDB Rows Integration ---
    private fun loadTMDBRows(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. Trending
                val trending = com.example.imagis.api.TmdbApiClient.service.getTrending(com.example.imagis.api.TmdbApiClient.API_KEY)
                addTmdbRow(adapter, "🔥 Trending Now", trending.results, 100)

                // 2. Popular Movies
                val movies = com.example.imagis.api.TmdbApiClient.service.getPopularMovies(com.example.imagis.api.TmdbApiClient.API_KEY)
                addTmdbRow(adapter, "🍿 Popular Movies", movies.results, 101)

                // 3. Top TV Series
                val series = com.example.imagis.api.TmdbApiClient.service.getPopularSeries(com.example.imagis.api.TmdbApiClient.API_KEY)
                addTmdbRow(adapter, "📺 Top TV Series", series.results, 102)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun addTmdbRow(
        mainAdapter: ArrayObjectAdapter, 
        title: String, 
        movies: List<com.example.imagis.api.Movie>, 
        headerId: Long
    ) {
        kotlinx.coroutines.withContext(Dispatchers.Main) {
            val listRowAdapter = ArrayObjectAdapter(TmdbCardPresenter())
            movies.forEach { listRowAdapter.add(it) }
            val header = HeaderItem(headerId, title)
            mainAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun loadVodContent(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                com.example.imagis.data.ChannelStore.globalVodList.clear()
                val localAllItems = mutableListOf<com.example.imagis.data.VodContent>()

                fun fetchUrlSafe(urlString: String): String {
                    return try { java.net.URL(urlString).readText() } catch (e: Exception) { "" }
                }

                // Argentina Live as priority VOD fallback
                val contentArgentina = fetchUrlSafe("https://sesteva.github.io/m3u/argentina.m3u")
                val argentinaItems = if (contentArgentina.isNotEmpty()) com.example.imagis.utils.VodParser.parse(contentArgentina) else emptyList()
                localAllItems.addAll(argentinaItems)
                
                com.example.imagis.data.ChannelStore.globalVodList.addAll(localAllItems)

                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    if (argentinaItems.isNotEmpty()) {
                        val listRowAdapter = ArrayObjectAdapter(VodPresenter())
                        argentinaItems.take(50).forEach { listRowAdapter.add(it) }
                        val header = HeaderItem(200, "Argentina Live TV")
                        adapter.add(ListRow(header, listRowAdapter))
                    }
                }
            } catch(e: Exception) { e.printStackTrace() }
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is PlatformMenuOption) {
                val intent = android.content.Intent(requireContext(), PlatformBrowseActivity::class.java)
                intent.putExtra("PLATFORM_ID", item.platformId)
                intent.putExtra("PLATFORM_NAME", item.title.replace(Regex("^[^A-Za-z]+"), "").trim())
                startActivity(intent)
            } else if (item is MenuOption) {
                when (item.type) {
                    TYPE_ANIME -> startActivity(android.content.Intent(requireContext(), AnimeActivity::class.java))
                    TYPE_DIRECTORY -> {
                         val intent = android.content.Intent(requireContext(), WebViewActivity::class.java)
                         intent.putExtra("VIDEO_URL", "https://jkanime.net/directorio/")
                         startActivity(intent)
                    }
                    TYPE_LIVETV -> startActivity(android.content.Intent(requireContext(), IptvActivity::class.java))
                    TYPE_DOWNLOADS -> startActivity(android.content.Intent(requireContext(), DownloadsActivity::class.java))
                    TYPE_SETTINGS -> startActivity(android.content.Intent(requireContext(), SettingsActivity::class.java))
                }
            } else if (item is AnimeEpisode) {
                 val intent = android.content.Intent(requireContext(), WebViewActivity::class.java)
                 intent.putExtra("VIDEO_URL", item.episodeUrl)
                 startActivity(intent)
            } else if (item is com.example.imagis.data.VodContent) {
                // Play VOD
                val intent = android.content.Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", item.streamUrl)
                intent.putExtra("IS_VOD_PAGE", false) // Direct stream
                startActivity(intent)
            } else if (item is com.example.imagis.api.Movie) {
                // Determine if it's a TV Show or Movie based on TMDB logic
                // TMDB API returns 'name' for TV Shows and 'title' for Movies
                if (item.title == null && item.name != null) {
                    // It's a TV Show
                    val intent = android.content.Intent(requireContext(), TvSeasonsActivity::class.java)
                    intent.putExtra("TV_SHOW_ID", item.id)
                    intent.putExtra("TV_SHOW_NAME", item.name)
                    startActivity(intent)
                } else {
                    // It's a Movie -> Launch Details Activity
                    val intent = android.content.Intent(requireContext(), DetailsActivity::class.java)
                    intent.putExtra("MOVIE_EXTRA", item)
                    startActivity(intent)
                }
            } else if (item is com.example.imagis.db.WatchHistoryEntity) {
                // Resume watching from saved position
                if (!item.videoUrl.isNullOrEmpty()) {
                    val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                    intent.putExtra("VIDEO_URL", item.videoUrl)
                    intent.putExtra("TITLE", item.title)
                    intent.putExtra("EPISODE_LABEL", item.episodeLabel)
                    intent.putExtra("CONTENT_ID", item.id)
                    intent.putExtra("CONTENT_TYPE", item.type)
                    intent.putExtra("POSTER_URL", item.posterUrl)
                    intent.putExtra("RESUME_POSITION_MS", item.positionMs)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "No saved stream URL for this title.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

     // ... MenuCardPresenter ...
     
    /**
     * Inner class to render VOD Posters (Netflix Style - Wide)
     */
    private inner class VodPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val vod = item as com.example.imagis.data.VodContent
            
            cardView.titleText = vod.title
            cardView.contentText = vod.category
            // WIDER Aspect Ratio (16:9) - Netflix Style
            cardView.setMainImageDimensions(320, 180) 
            cardView.setInfoAreaBackgroundColor(Color.parseColor("#1A1A1A"))
            
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(vod.posterUrl)
                .centerCrop()
                .placeholder(R.drawable.app_icon_placeholder)
                .error(R.drawable.app_icon_placeholder)
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
             (viewHolder.view as ImageCardView).mainImage = null
        }
    }

    /**
     * Inner class for Hero Banner (Large Landscape)
     */
    private inner class HeroPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val vod = item as com.example.imagis.data.VodContent
            
            cardView.titleText = "Featured: " + vod.title
            cardView.contentText = "Watch Now"
            // Hero Dimensions (Wide)
            cardView.setMainImageDimensions(600, 340) 
            cardView.setInfoAreaBackgroundColor(Color.parseColor("#000000"))
            
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(vod.posterUrl)
                .centerCrop()
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
             (viewHolder.view as ImageCardView).mainImage = null
        }
    }

    // ... CardPresenter (for Anime) ...
    private inner class MenuCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            
            when (item) {
                is MenuOption -> {
                    cardView.titleText = item.title
                    cardView.contentText = item.description
                    cardView.setMainImageDimensions(300, 170)
                    cardView.mainImageView.setImageResource(item.iconResId)
                    cardView.setBackgroundColor(Color.DKGRAY)
                }
                is PlatformMenuOption -> {
                    cardView.titleText = item.title
                    cardView.contentText = item.description
                    cardView.setMainImageDimensions(300, 170)
                    cardView.mainImageView.setImageResource(item.iconResId)
                    cardView.setBackgroundColor(Color.parseColor("#1A1A1A"))
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    /**
     * Inner class to render Anime Episodes with Glide
     */
    private inner class CardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val episode = item as AnimeEpisode
            
            cardView.titleText = episode.title
            cardView.contentText = null // User requested to remove this ("purple link")
            cardView.setMainImageDimensions(300, 170)
            cardView.setInfoAreaBackgroundColor(Color.parseColor("#1A1A1A")) // Hub Aesthetic (Dark Grey)
            
            // Load image using Glide
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(episode.imageUrl)
                .centerCrop()
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    /**
     * Inner class to render Favorites saved in Room DB
     */
    private inner class FavoriteCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val fav = item as com.example.imagis.db.FavoritesEntity
            
            cardView.titleText = fav.title
            cardView.contentText = "⭐ ${fav.type}"
            cardView.setMainImageDimensions(200, 300) // Poster aspect ratio
            cardView.setInfoAreaBackgroundColor(android.graphics.Color.parseColor("#1A1A1A"))
            
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(fav.posterUrl)
                .centerCrop()
                .placeholder(R.drawable.app_icon_placeholder)
                .error(R.drawable.app_icon_placeholder)
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    /**
     * Inner class to render Continue Watching cards with progress info
     */
    private inner class WatchHistoryCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val entry = item as com.example.imagis.db.WatchHistoryEntity
            
            cardView.titleText = entry.title
            
            // Build subtitle: episode label + progress
            val parts = mutableListOf<String>()
            if (!entry.episodeLabel.isNullOrEmpty()) parts.add(entry.episodeLabel)
            if (entry.durationMs > 0) {
                val posMin = (entry.positionMs / 1000 / 60).toInt()
                val posSec = ((entry.positionMs / 1000) % 60).toInt()
                val durMin = (entry.durationMs / 1000 / 60).toInt()
                val durSec = ((entry.durationMs / 1000) % 60).toInt()
                parts.add(String.format("%d:%02d / %d:%02d", posMin, posSec, durMin, durSec))
            }
            if (entry.status == "WATCHED") parts.add("✅")
            cardView.contentText = parts.joinToString(" · ")
            
            cardView.setMainImageDimensions(200, 300) // Poster aspect ratio
            cardView.setInfoAreaBackgroundColor(Color.parseColor("#1A1A1A"))
            
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(entry.posterUrl)
                .centerCrop()
                .placeholder(R.drawable.app_icon_placeholder)
                .error(R.drawable.app_icon_placeholder)
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}
