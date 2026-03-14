// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/PlatformBrowseFragment.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.epicgera.vtrae.R
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.api.TmdbApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlatformBrowseFragment : BrowseSupportFragment() {

    private var platformId: Int = 8
    private var platformName: String = "Netflix"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        platformId = arguments?.getInt("PLATFORM_ID", 8) ?: 8
        platformName = arguments?.getString("PLATFORM_NAME") ?: "Netflix"

        title = "$platformName Catalog"
        brandColor = Color.parseColor("#1A1A1A")
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        loadPlatformContent()
        setupEventListeners()
    }

    private fun loadPlatformContent() {
        val listRowPresenter = object : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM) {
            override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(holder, item)
                val vh = holder as ListRowPresenter.ViewHolder
                val gridView = vh.gridView
                gridView.windowAlignment = BaseGridView.WINDOW_ALIGN_LOW_EDGE
                gridView.windowAlignmentOffsetPercent = 2.0f
                gridView.itemAlignmentOffsetPercent = 0.0f
                gridView.setFocusScrollStrategy(BaseGridView.FOCUS_SCROLL_ITEM)
            }
        }
        listRowPresenter.shadowEnabled = false

        val rowsAdapter = ArrayObjectAdapter(listRowPresenter)
        adapter = rowsAdapter

        val presenter = TmdbCardPresenter()
        val api = TmdbApiClient.service
        val key = TmdbApiClient.API_KEY
        val providerStr = platformId.toString()
        val region = "US"

        GlobalScope.launch(Dispatchers.IO) {
            // --- Movies on this platform ---
            try {
                val moviesResponse = api.discoverMovies(
                    apiKey = key,
                    watchProviders = providerStr,
                    watchRegion = region
                )
                if (moviesResponse.results.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val rowAdapter = ArrayObjectAdapter(presenter)
                        moviesResponse.results.forEach { rowAdapter.add(it) }
                        rowsAdapter.add(
                            ListRow(
                                HeaderItem(500, "\uD83C\uDFAC Movies on $platformName"),
                                rowAdapter
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // --- TV Series on this platform ---
            try {
                val tvResponse = api.discoverTv(
                    apiKey = key,
                    watchProviders = providerStr,
                    watchRegion = region
                )
                if (tvResponse.results.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val rowAdapter = ArrayObjectAdapter(presenter)
                        tvResponse.results.forEach { rowAdapter.add(it) }
                        rowsAdapter.add(
                            ListRow(
                                HeaderItem(501, "\uD83D\uDCFA Series on $platformName"),
                                rowAdapter
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // --- Top Rated Movies on this platform ---
            try {
                val topMovies = api.discoverMovies(
                    apiKey = key,
                    watchProviders = providerStr,
                    watchRegion = region,
                    sortBy = "vote_average.desc"
                )
                if (topMovies.results.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val rowAdapter = ArrayObjectAdapter(presenter)
                        topMovies.results.forEach { rowAdapter.add(it) }
                        rowsAdapter.add(
                            ListRow(
                                HeaderItem(502, "⭐ Top Rated Movies on $platformName"),
                                rowAdapter
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // --- Top Rated Series on this platform ---
            try {
                val topTv = api.discoverTv(
                    apiKey = key,
                    watchProviders = providerStr,
                    watchRegion = region,
                    sortBy = "vote_average.desc"
                )
                if (topTv.results.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val rowAdapter = ArrayObjectAdapter(presenter)
                        topTv.results.forEach { rowAdapter.add(it) }
                        rowsAdapter.add(
                            ListRow(
                                HeaderItem(503, "⭐ Top Rated Series on $platformName"),
                                rowAdapter
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Movie) {
                if (item.title == null && item.name != null) {
                    // TV Show
                    val intent = Intent(requireContext(), TvSeasonsActivity::class.java)
                    intent.putExtra("TV_SHOW_ID", item.id)
                    intent.putExtra("TV_SHOW_NAME", item.name)
                    startActivity(intent)
                } else {
                    // Movie
                    val intent = Intent(requireContext(), DetailsActivity::class.java)
                    intent.putExtra("MOVIE_EXTRA", item)
                    startActivity(intent)
                }
            }
        }
    }
}

