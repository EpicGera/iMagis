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

class MainFragment : BrowseSupportFragment() {

    companion object {
        const val TYPE_ANIME = 1
        const val TYPE_LIVETV = 2
        const val TYPE_DIRECTORY = 3
        const val TYPE_SETTINGS = 4
        const val TYPE_DOWNLOADS = 5
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

        // ** Restored: Latest Episodes Row **
        val latestEpisodesAdapter = ArrayObjectAdapter(CardPresenter()) // New Presenter for episodes
        val headerLatest = HeaderItem(0, "Latest Episodes")
        rowsAdapter.add(ListRow(headerLatest, latestEpisodesAdapter))

        // Add Menu rows
        val header1 = HeaderItem(1, "Menu")
        rowsAdapter.add(ListRow(header1, vodAdapter))

        val header2 = HeaderItem(2, "Live TV")
        rowsAdapter.add(ListRow(header2, liveAdapter))

        val header3 = HeaderItem(3, "Preferences")
        rowsAdapter.add(ListRow(header3, settingsAdapter))

        adapter = rowsAdapter
        
        // Load data in background
        loadLatestEpisodes(latestEpisodesAdapter)
        loadVodContent(rowsAdapter)
        loadTmdbContent(rowsAdapter)
        loadFavorites(rowsAdapter)
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

    // ... loadRows ...

    private fun loadVodContent(adapter: ArrayObjectAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Clear previous items in global store
                com.example.imagis.data.ChannelStore.globalVodList.clear()
                
                // Use a local ref for this function scope to populate adapters
                val localAllItems = mutableListOf<com.example.imagis.data.VodContent>()

                // Fetch Argentina Live (Official Sesteva List)
                val urlArgentina = java.net.URL("https://sesteva.github.io/m3u/argentina.m3u")
                val contentArgentina = urlArgentina.readText()
                val argentinaItems = com.example.imagis.utils.VodParser.parse(contentArgentina) 
                localAllItems.addAll(argentinaItems)

                // Fetch Movies (General)
                val urlMovies = java.net.URL("https://iptv-org.github.io/iptv/categories/movies.m3u")
                val contentMovies = urlMovies.readText()
                val movieItems = com.example.imagis.utils.VodParser.parse(contentMovies)
                localAllItems.addAll(movieItems)

                // Filter for "Latino/Spanish" content (approximate "Cuevana" style)
                val latinoMovies = movieItems.filter { 
                    it.title.contains("Spanish", ignoreCase = true) || 
                    it.title.contains("Latino", ignoreCase = true) ||
                    it.title.contains("ES", ignoreCase = true) ||
                    it.category.contains("Spanish", ignoreCase = true)
                }

                // Fetch Series
                val urlSeries = java.net.URL("https://iptv-org.github.io/iptv/categories/series.m3u")
                val contentSeries = urlSeries.readText()
                val seriesItems = com.example.imagis.utils.VodParser.parse(contentSeries)
                localAllItems.addAll(seriesItems)
                
                // Update Global Store
                com.example.imagis.data.ChannelStore.globalVodList.addAll(localAllItems)

                // UI Updates on Main Thread
                launch(Dispatchers.Main) {
                    // 1. Featured/Hero Row
                    val heroItem = latinoMovies.randomOrNull() ?: movieItems.randomOrNull()
                    if (heroItem != null) {
                        val heroAdapter = ArrayObjectAdapter(HeroPresenter())
                        heroAdapter.add(heroItem)
                        adapter.add(0, ListRow(HeaderItem(0, "Featured Movie"), heroAdapter)) 
                    }
                    
                    // 2. Argentina Live (Top Priority)
                    if (argentinaItems.isNotEmpty()) {
                        val listRowAdapter = ArrayObjectAdapter(VodPresenter()) // Use VodPresenter (handles filtered items)
                        argentinaItems.take(50).forEach { 
                             listRowAdapter.add(it) 
                        }
                        val header = HeaderItem(10, "Argentina Live TV")
                        adapter.add(ListRow(header, listRowAdapter))
                    }

                    // 3. Latino Movies (Cuevana Style)
                    if (latinoMovies.isNotEmpty()) {
                         val listRowAdapter = ArrayObjectAdapter(VodPresenter())
                         latinoMovies.shuffled().take(50).forEach { listRowAdapter.add(it) } 
                         val header = HeaderItem(100, "Peliculas (Cuevana Style)")
                         adapter.add(ListRow(header, listRowAdapter))
                    } else {
                        // Fallback if no specific latino content found
                         val listRowAdapter = ArrayObjectAdapter(VodPresenter())
                         movieItems.shuffled().take(50).forEach { listRowAdapter.add(it) } 
                         val header = HeaderItem(100, "Popular Movies")
                         adapter.add(ListRow(header, listRowAdapter))
                    }

                    // 4. Series Row
                    if (seriesItems.isNotEmpty()) {
                         val listRowAdapter = ArrayObjectAdapter(VodPresenter())
                         seriesItems.shuffled().take(50).forEach { listRowAdapter.add(it) } 
                         val header = HeaderItem(101, "Top TV Series")
                         adapter.add(ListRow(header, listRowAdapter))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is MenuOption) {
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
                // TMDB Selection -> Launch Details Activity
                val intent = android.content.Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("MOVIE_EXTRA", item)
                startActivity(intent)
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
            val option = item as MenuOption
            
            cardView.titleText = option.title
            cardView.contentText = option.description
            cardView.setMainImageDimensions(300, 170)
            
            cardView.mainImageView.setImageResource(option.iconResId) 
            cardView.setBackgroundColor(Color.DKGRAY)
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
}
