package com.epicgera.vtrae.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.epicgera.vtrae.R
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.data.DdlResult
import com.epicgera.vtrae.data.TorrentResult
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.epicgera.vtrae.utils.MediaScraperEngine
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import android.util.Log

class MovieDetailsFragment : DetailsSupportFragment(), TorrentListener {

    // Moving to companion object to act as a global singleton Tracker
    // private var torrentStream: TorrentStream? = null

    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private var currentMovie: com.epicgera.vtrae.api.Movie? = null

    companion object {
        const val ACTION_PLAY = 1L
        const val ACTION_SEARCH_SOURCES = 2L
        const val ACTION_FAVORITE = 3L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

        val selectedMovie = requireActivity().intent.getSerializableExtra("MOVIE_EXTRA") as? Movie
        
        if (selectedMovie != null) {
            currentMovie = selectedMovie
            setupUI(selectedMovie)
            setupEventListeners(selectedMovie)
        } else {
            com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.error_loading_movie))
            requireActivity().finish()
        }
    }

    private fun setupUI(movie: Movie) {
        mPresenterSelector = ClassPresenterSelector()
        mAdapter = ArrayObjectAdapter(mPresenterSelector)

        // Setup the specialized Row Presenter for TV Details screens
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        
        // Define background colors
        detailsPresenter.backgroundColor = android.graphics.Color.parseColor("#1A1A1A")
        detailsPresenter.actionsBackgroundColor = android.graphics.Color.parseColor("#000000")
        
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)

        val detailsOverview = DetailsOverviewRow(movie)

        // Add action buttons
        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(Action(ACTION_PLAY, getString(R.string.action_play), getString(R.string.action_play_desc)))
        actionAdapter.add(Action(ACTION_SEARCH_SOURCES, getString(R.string.action_search_sources), getString(R.string.action_search_sources_desc)))
        actionAdapter.add(Action(ACTION_FAVORITE, getString(R.string.action_favorite), getString(R.string.action_favorite_desc)))
        detailsOverview.actionsAdapter = actionAdapter

        mAdapter.add(detailsOverview)
        adapter = mAdapter

        loadBackgroundAndPoster(movie, detailsOverview)
    }

    private fun loadBackgroundAndPoster(movie: Movie, row: DetailsOverviewRow) {
        // Load Poster into the Row
        Glide.with(requireContext())
            .asBitmap()
            .load(movie.fullPosterUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    row.imageDrawable = android.graphics.drawable.BitmapDrawable(resources, resource)
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        // Load Backdrop into the Full Screen Background
        val backdropUrl = if (movie.backdrop_path != null) {
            "https://image.tmdb.org/t/p/original${movie.backdrop_path}"
        } else {
            movie.fullPosterUrl // Fallback
        }

        Glide.with(requireContext())
            .asBitmap()
            .load(backdropUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    mDetailsBackground.coverBitmap = resource
                    mDetailsBackground.enableParallax()
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun setupEventListeners(movie: Movie) {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Action) {
                Log.d("VTRAE_Details", "Action clicked: id=${item.id}, movie=${movie.displayTitle}")
                when (item.id) {
                    ACTION_PLAY -> {
                        Log.d("VTRAE_Details", "ACTION_PLAY triggered for: ${movie.displayTitle}")
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_scanning_all_sources, movie.displayTitle))

                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val title = movie.displayTitle

                                // Progress callback shows per-source toast on UI thread
                                val progressCallback: (String) -> Unit = { status ->
                                    Log.d("VTRAE_Details", "Progress: $status")
                                    requireActivity().runOnUiThread {
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(status)
                                    }
                                }

                                // Search ALL torrent sources and Pahe DDL — let user pick
                                val torrentResults = MediaScraperEngine.searchAllSources(title, progressCallback)
                                val ddlResults = MediaScraperEngine.searchPahe(title, progressCallback)

                                Log.d("VTRAE_Details", "ACTION_PLAY searchAllSources returned ${torrentResults.size} results, searchPahe returned ${ddlResults.size} DDLs")

                                withContext(Dispatchers.Main) {
                                    if (torrentResults.isNotEmpty() || ddlResults.isNotEmpty()) {
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Found ${ddlResults.size} DDLs and ${torrentResults.size} Torrents")
                                        showTorrentSelectionDialog(torrentResults, ddlResults, title)
                                    } else {
                                        // Fallback: try local Room DB
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_no_results_checking_local))
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val streamUrl = MediaScraperEngine.findStreamForTitle(requireContext(), title)
                                            withContext(Dispatchers.Main) {
                                                if (streamUrl != null) {
                                                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_stream_found_local))
                                                    val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                                                    intent.putExtra("VIDEO_URL", streamUrl)
                                                    intent.putExtra("TITLE", movie.displayTitle)
                                                    intent.putExtra("CONTENT_ID", movie.id.toString())
                                                    intent.putExtra("CONTENT_TYPE", "MOVIE")
                                                    intent.putExtra("POSTER_URL", movie.fullPosterUrl)
                                                    startActivity(intent)
                                                } else {
                                                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_no_sources_available))
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("VTRAE_Details", "ACTION_PLAY crashed: ${e.message}", e)
                                withContext(Dispatchers.Main) {
                                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_p2p_error_toast, e.message ?: ""))
                                }
                            }
                        }
                    }


                    ACTION_SEARCH_SOURCES -> {
                        Log.d("VTRAE_Details", "ACTION_SEARCH_SOURCES triggered for: ${movie.displayTitle}")
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_scanning_all_sources, movie.displayTitle))

                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val title = movie.displayTitle
                                
                                // Progress callback shows per-source toast on UI thread
                                val progressCallback: (String) -> Unit = { status ->
                                    Log.d("VTRAE_Details", "Search progress: $status")
                                    requireActivity().runOnUiThread {
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(status)
                                    }
                                }
                                
                                // Search ALL torrent sources and Pahe DDL
                                val torrentResults = MediaScraperEngine.searchAllSources(title, progressCallback)
                                val ddlResults = MediaScraperEngine.searchPahe(title, progressCallback)
                                
                                Log.d("VTRAE_Details", "searchAllSources returned ${torrentResults.size} results, searchPahe returned ${ddlResults.size} DDLs")

                                withContext(Dispatchers.Main) {
                                    if (torrentResults.isNotEmpty() || ddlResults.isNotEmpty()) {
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("Found ${ddlResults.size} DDLs and ${torrentResults.size} Torrents")
                                        showTorrentSelectionDialog(torrentResults, ddlResults, title)
                                    } else {
                                        // Fallback: try local Room DB
                                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_no_results_checking_local))
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val streamUrl = MediaScraperEngine.findStreamForTitle(requireContext(), title)
                                            withContext(Dispatchers.Main) {
                                                if (streamUrl != null) {
                                                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_stream_found_local))
                                                    val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                                                    intent.putExtra("VIDEO_URL", streamUrl)
                                                    intent.putExtra("TITLE", movie.displayTitle)
                                                    intent.putExtra("CONTENT_ID", movie.id.toString())
                                                    intent.putExtra("CONTENT_TYPE", "MOVIE")
                                                    intent.putExtra("POSTER_URL", movie.fullPosterUrl)
                                                    startActivity(intent)
                                                } else {
                                                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_no_sources_available))
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("VTRAE_Details", "ACTION_SEARCH_SOURCES crashed: ${e.message}", e)
                                withContext(Dispatchers.Main) {
                                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_p2p_error_toast, e.message ?: ""))
                                }
                            }
                        }
                    }

                    ACTION_FAVORITE -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val db = com.epicgera.vtrae.db.AppDatabase.getDatabase(requireContext())
                            val existing = db.favoritesDao().getFavoriteById(movie.id.toString())
                            
                            if (existing != null) {
                                db.favoritesDao().deleteFavoriteById(movie.id.toString())
                                withContext(Dispatchers.Main) {
                                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_removed_favorite))
                                }
                            } else {
                                val fav = com.epicgera.vtrae.db.FavoritesEntity(
                                    id = movie.id.toString(),
                                    title = movie.displayTitle,
                                    posterUrl = movie.fullPosterUrl,
                                    type = "MOVIE"
                                )
                                db.favoritesDao().insertFavorite(fav)
                                withContext(Dispatchers.Main) {
                                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_added_favorite))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Inner class to format the text inside the Details screen
     */
    private inner class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
        override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
            val movie = item as Movie
            viewHolder.title.text = movie.displayTitle
            viewHolder.subtitle.text = "TMDB Rating: TBD" // Placeholder
            viewHolder.body.text = movie.overview
        }
    }

    private fun startTorrentStream(magnetUrl: String, title: String) {
        com.epicgera.vtrae.data.TorrentRepository.initialize(requireContext())
        
        // Show the buffering overlay
        showBufferingOverlay(title)
        streamStartTime = System.currentTimeMillis()
        hasLaunchedPlayer = false
        isStreamReady = false
        
        // Start streaming
        com.epicgera.vtrae.data.TorrentRepository.startStream(requireContext(), magnetUrl, title)
        
        // Observe the flow and update the overlay in real time
        viewLifecycleOwner.lifecycleScope.launch {
            com.epicgera.vtrae.data.TorrentRepository.downloadState.collect { state ->
                if (state == null) return@collect
                
                if (state.error != null) {
                    requireActivity().runOnUiThread {
                        hideBufferingOverlay()
                        com.epicgera.vtrae.ui.components.VtrToastManager.showError(
                            getString(R.string.msg_p2p_error_toast, state.error)
                        )
                    }
                    return@collect
                }
                
                // Update the buffering overlay with live progress
                val progress = state.progress.toInt()
                val seeds = state.seeds
                val downloadSpeed = if (state.downloadSpeedBytes > 1024) {
                    String.format("%.1f MB/s", state.downloadSpeedBytes / 1024f)
                } else {
                    String.format("%d KB/s", state.downloadSpeedBytes.toInt())
                }
                
                requireActivity().runOnUiThread {
                    bufferingProgress?.progress = progress
                    bufferingPercent?.text = "$progress%"
                    bufferingPeers?.text = getString(R.string.peers_count, seeds)
                    bufferingSpeed?.text = "⚡ $downloadSpeed"
                    
                    // Update title based on phase
                    val titleView = requireActivity().findViewById<TextView>(R.id.buffering_title)
                    if (progress > 0) {
                        titleView?.text = getString(R.string.status_downloading)
                    } else if (state.buffering) {
                        titleView?.text = getString(R.string.status_buffering_stream)
                    }
                    
                    // Update color based on progress
                    val color = when {
                        progress >= 75 -> "#4CAF50"  // Green
                        progress >= 40 -> "#FFC107"  // Yellow
                        else -> "#FF9800"            // Orange
                    }
                    bufferingPercent?.setTextColor(Color.parseColor(color))
                }
                
                // Check if stream is ready and we can launch the player
                if (!state.buffering && state.videoFile != null) {
                    isStreamReady = true
                    checkAndLaunchPlayer(
                        com.epicgera.vtrae.data.TorrentRepository.currentTorrent(),
                        state.progress
                    )
                }
            }
        }
    }

    // --- BUFFERING OVERLAY MANAGEMENT ---

    private var bufferingOverlay: View? = null
    private var bufferingProgress: android.widget.ProgressBar? = null
    private var bufferingPercent: TextView? = null
    private var bufferingPeers: TextView? = null
    private var bufferingSpeed: TextView? = null
    private var bufferingTitle: TextView? = null

    private fun showBufferingOverlay(title: String) {
        bufferingOverlay = requireActivity().findViewById(R.id.buffering_overlay)
        bufferingProgress = requireActivity().findViewById(R.id.buffering_progress)
        bufferingPercent = requireActivity().findViewById(R.id.buffering_percent)
        bufferingPeers = requireActivity().findViewById(R.id.buffering_peers)
        bufferingSpeed = requireActivity().findViewById(R.id.buffering_speed)
        bufferingTitle = requireActivity().findViewById(R.id.buffering_name)
        
        bufferingOverlay?.visibility = View.VISIBLE
        bufferingProgress?.isIndeterminate = false
        bufferingProgress?.progress = 0
        bufferingPercent?.text = "0%"
        bufferingPeers?.text = "🌐 Connecting..."
        bufferingSpeed?.text = ""
        bufferingTitle?.text = title
        
        // Update the header
        requireActivity().findViewById<TextView>(R.id.buffering_title)?.text = getString(R.string.connecting_p2p)
    }

    private fun hideBufferingOverlay() {
        bufferingOverlay?.visibility = View.GONE
    }

    private var isStreamReady = false
    private var hasLaunchedPlayer = false
    private var streamStartTime = 0L

    override fun onStreamPrepared(torrent: Torrent?) {
        Log.d("P2P_Stream", "Stream Prepared!")
        requireActivity().runOnUiThread {
            requireActivity().findViewById<TextView>(R.id.buffering_title)?.text = getString(R.string.status_downloading)
        }
    }

    override fun onStreamStarted(torrent: Torrent?) {
        Log.d("P2P_Stream", "Stream Started!")
        streamStartTime = System.currentTimeMillis()
        hasLaunchedPlayer = false
        isStreamReady = false
        
        requireActivity().runOnUiThread {
            requireActivity().findViewById<TextView>(R.id.buffering_title)?.text = getString(R.string.status_buffering_stream)
        }
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Log.e("P2P_Stream", "Stream Error: ${e?.message}")
        requireActivity().runOnUiThread {
            hideBufferingOverlay()
            com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_p2p_error_toast, e?.message ?: ""))
        }
    }

    override fun onStreamReady(torrent: Torrent?) {
        Log.d("P2P_Stream", "Stream Ready! Local File: ${torrent?.videoFile?.absolutePath}")
        isStreamReady = true
        checkAndLaunchPlayer(torrent, 0f)
    }

    private fun checkAndLaunchPlayer(torrent: Torrent?, currentProgress: Float) {
        if (hasLaunchedPlayer || !isStreamReady || torrent == null) return
        
        // Wait until we have at least 1.5% progress or 15 seconds have elapsed
        val elapsed = System.currentTimeMillis() - streamStartTime
        if (currentProgress >= 1.5f || elapsed >= 15_000) {
            hasLaunchedPlayer = true
            lifecycleScope.launch(Dispatchers.Main) {
                hideBufferingOverlay()
                val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", torrent.videoFile?.absolutePath)
                intent.putExtra("TITLE", currentMovie?.displayTitle ?: "")
                intent.putExtra("CONTENT_ID", currentMovie?.id?.toString() ?: "")
                intent.putExtra("CONTENT_TYPE", "MOVIE")
                intent.putExtra("POSTER_URL", currentMovie?.fullPosterUrl ?: "")
                startActivity(intent)
            }
        } else {
            requireActivity().runOnUiThread {
                requireActivity().findViewById<TextView>(R.id.buffering_title)?.text = getString(R.string.status_pre_buffering)
            }
        }
    }

    override fun onStreamProgress(torrent: Torrent?, status: com.github.se_bastiaan.torrentstream.StreamStatus?) {
        val progress = status?.progress?.toInt() ?: return
        val seeds = status?.seeds ?: 0
        val downloadSpeed = status?.downloadSpeed?.let {
            if (it > 1024) String.format("%.1f MB/s", it / 1024f)
            else String.format("%d KB/s", it.toInt())
        } ?: ""
        
        requireActivity().runOnUiThread {
            bufferingProgress?.progress = progress
            bufferingPercent?.text = "$progress%"
            bufferingPeers?.text = getString(R.string.peers_count, seeds)
            bufferingSpeed?.text = "⚡ $downloadSpeed"
            
            // Update color based on progress
            val color = when {
                progress >= 75 -> "#4CAF50"  // Green
                progress >= 40 -> "#FFC107"  // Yellow
                else -> "#FF9800"            // Orange
            }
            bufferingPercent?.setTextColor(Color.parseColor(color))
        }
        
        // Continually evaluate if we have enough buffer to launch
        checkAndLaunchPlayer(torrent, status?.progress ?: 0f)
    }

    override fun onStreamStopped() {
        Log.d("P2P_Stream", "Stream Stopped")
    }

    // --- TORRENT + DDL SELECTION DIALOG ---

    /**
     * Combined source selection dialog showing both Torrent results and DDL results.
     * Torrent items start P2P streaming. DDL items open in WebView.
     */
    private fun showTorrentSelectionDialog(
        torrentResults: List<TorrentResult>, 
        ddlResults: List<DdlResult>,
        movieTitle: String
    ) {
        // Build a unified list with type markers
        // Type 0 = section header, Type 1 = torrent, Type 2 = DDL
        data class DialogItem(
            val type: Int, // 0=header, 1=torrent, 2=ddl
            val torrent: TorrentResult? = null,
            val ddl: DdlResult? = null,
            val headerText: String = ""
        )

        val items = mutableListOf<DialogItem>()

        if (torrentResults.isNotEmpty()) {
            items.add(DialogItem(type = 0, headerText = getString(R.string.torrents_count_format, torrentResults.size)))
            torrentResults.forEach { items.add(DialogItem(type = 1, torrent = it)) }
        }
        if (ddlResults.isNotEmpty()) {
            items.add(DialogItem(type = 0, headerText = getString(R.string.ddl_count_format, ddlResults.size)))
            ddlResults.forEach { items.add(DialogItem(type = 2, ddl = it)) }
        }

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_torrent_select, null)
        val titleView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val listView = dialogView.findViewById<ListView>(R.id.torrent_list)
        
        val totalCount = torrentResults.size + ddlResults.size
        titleView.text = getString(R.string.dialog_sources_title, movieTitle, totalCount)
        
        val adapter = object : BaseAdapter() {
            override fun getCount() = items.size
            override fun getItem(pos: Int) = items[pos]
            override fun getItemId(pos: Int) = pos.toLong()
            override fun getViewTypeCount() = 3
            override fun getItemViewType(position: Int) = items[position].type
            override fun isEnabled(position: Int) = items[position].type != 0 // Headers not clickable
            
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val item = items[position]
                
                return when (item.type) {
                    // Section header
                    0 -> {
                        val header = convertView ?: TextView(requireContext()).apply {
                            setPadding(16, 24, 16, 8)
                            setTextColor(Color.parseColor("#FFB74D"))
                            textSize = 16f
                            setTypeface(null, android.graphics.Typeface.BOLD)
                        }
                        (header as TextView).text = item.headerText
                        header
                    }
                    
                    // Torrent result
                    1 -> {
                        val view = convertView ?: LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_torrent_result, parent, false)
                        val result = item.torrent!!
                        
                        val badge = view.findViewById<TextView>(R.id.source_badge)
                        val title = view.findViewById<TextView>(R.id.torrent_title)
                        val seeds = view.findViewById<TextView>(R.id.torrent_seeds)
                        val size = view.findViewById<TextView>(R.id.torrent_size)
                        
                        badge.text = result.source
                        title.text = result.title
                        seeds.text = getString(R.string.seeds_count, result.seeds)
                        size.text = getString(R.string.size_format, result.sizeDisplay)
                        
                        // Color-code source badges
                        val badgeBg = GradientDrawable()
                        badgeBg.cornerRadius = 8f
                        when (result.source) {
                            "YTS" -> badgeBg.setColor(Color.parseColor("#4CAF50"))
                            "PirateBay" -> badgeBg.setColor(Color.parseColor("#2196F3"))
                            "LimeTorrents" -> badgeBg.setColor(Color.parseColor("#FF9800"))
                            "Nyaa" -> badgeBg.setColor(Color.parseColor("#E91E63"))
                            "1337x" -> badgeBg.setColor(Color.parseColor("#D32F2F"))
                            "EZTV" -> badgeBg.setColor(Color.parseColor("#00897B"))
                            "MSearch" -> badgeBg.setColor(Color.parseColor("#7C4DFF"))
                            else -> badgeBg.setColor(Color.parseColor("#9E9E9E"))
                        }
                        badge.background = badgeBg
                        
                        // Color seeds based on health
                        seeds.setTextColor(when {
                            result.seeds >= 50 -> Color.parseColor("#4CAF50")
                            result.seeds >= 10 -> Color.parseColor("#FFC107")
                            else -> Color.parseColor("#F44336")
                        })
                        
                        view
                    }
                    
                    // DDL result
                    2 -> {
                        val view = convertView ?: LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_ddl_result, parent, false)
                        val result = item.ddl!!
                        
                        val hostBadge = view.findViewById<TextView>(R.id.ddl_host_badge)
                        val qualityBadge = view.findViewById<TextView>(R.id.ddl_quality_badge)
                        val title = view.findViewById<TextView>(R.id.ddl_title)
                        val size = view.findViewById<TextView>(R.id.ddl_size)
                        val source = view.findViewById<TextView>(R.id.ddl_source)
                        
                        hostBadge.text = result.host
                        qualityBadge.text = result.quality
                        title.text = result.title
                        size.text = getString(R.string.size_format, result.sizeDisplay)
                        source.text = getString(R.string.host_format, result.hostFullName)
                        
                        // Host badge color
                        val hostBg = GradientDrawable()
                        hostBg.cornerRadius = 8f
                        when (result.host) {
                            "PD" -> hostBg.setColor(Color.parseColor("#00BCD4"))
                            "MG" -> hostBg.setColor(Color.parseColor("#D50000"))
                            "GD" -> hostBg.setColor(Color.parseColor("#4285F4"))
                            "VF" -> hostBg.setColor(Color.parseColor("#8BC34A"))
                            "1F" -> hostBg.setColor(Color.parseColor("#FFB300"))
                            "1D" -> hostBg.setColor(Color.parseColor("#0078D4"))
                            "MF" -> hostBg.setColor(Color.parseColor("#2979FF"))
                            else -> hostBg.setColor(Color.parseColor("#78909C"))
                        }
                        hostBadge.background = hostBg
                        
                        // Quality badge color
                        val qualBg = GradientDrawable()
                        qualBg.cornerRadius = 6f
                        when {
                            result.quality.contains("2160p", true) || result.quality.contains("4K", true) ->
                                qualBg.setColor(Color.parseColor("#FFD600"))
                            result.quality.contains("1080p", true) ->
                                qualBg.setColor(Color.parseColor("#4CAF50"))
                            result.quality.contains("720p", true) ->
                                qualBg.setColor(Color.parseColor("#FF9800"))
                            else ->
                                qualBg.setColor(Color.parseColor("#78909C"))
                        }
                        qualityBadge.background = qualBg
                        
                        view
                    }
                    
                    else -> convertView ?: View(requireContext())
                }
            }
        }
        
        listView.adapter = adapter
        listView.isFocusable = true
        listView.isFocusableInTouchMode = true
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()
        
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]
            when (item.type) {
                1 -> {
                    // Torrent: start P2P stream
                    val selected = item.torrent!!
                    Log.d("VTRAE_Details", "User selected torrent: [${selected.source}] ${selected.title}")
                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.connecting_p2p))
                    startTorrentStream(selected.magnetUrl, selected.title)
                    dialog.dismiss()
                }
                2 -> {
                    // DDL: open in WebView for user to complete download
                    val selected = item.ddl!!
                    Log.d("VTRAE_Details", "User selected DDL: [${selected.host}] ${selected.title} (${selected.quality})")
                    com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_opening_download, selected.hostFullName))
                    val intent = android.content.Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("VIDEO_URL", selected.downloadUrl)
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
        }
        
        // Show dialog, style it, and force focus to the list
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.75).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        // Force focus to the list so D-pad works immediately
        listView.requestFocus()
        listView.setSelection(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        // We do not stop the global stream here so background downloads continue
        // The user can stop them from the downloads fragment or notification
    }
}

