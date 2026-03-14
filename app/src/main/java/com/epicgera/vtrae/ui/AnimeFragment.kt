package com.epicgera.vtrae.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.epicgera.vtrae.R
import com.epicgera.vtrae.data.AnimeEpisode
import com.epicgera.vtrae.utils.JkanimeScraper
import kotlinx.coroutines.launch

class AnimeFragment : VerticalGridSupportFragment() {

    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_latest_anime)
        setupFragment()
        loadData()
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = 5
        setGridPresenter(gridPresenter)

        mAdapter = ArrayObjectAdapter(AnimeCardPresenter())
        adapter = mAdapter

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is AnimeEpisode) {
                val intent = android.content.Intent(requireContext(), AnimeDetailsActivity::class.java)
                intent.putExtra("ANIME_TITLE", item.title)
                intent.putExtra("ANIME_EPISODE", item.episodeNumber)
                intent.putExtra("ANIME_IMAGE", item.imageUrl)
                intent.putExtra("ANIME_URL", item.episodeUrl)
                startActivity(intent)
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val episodes = JkanimeScraper.getLatestEpisodes()
                if (episodes.isEmpty()) {
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.error_no_episodes_found))
                } else {
                    mAdapter.addAll(0, episodes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.error_generic, e.message))
            }
        }
    }

    private inner class AnimeCardPresenter : Presenter() {
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
            cardView.contentText = episode.episodeNumber
            cardView.setMainImageDimensions(200, 300) // Poster style
            
            // Using Glide to load image safely for Firestick TVs
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(episode.imageUrl)
                .format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565)
                .override(200, 300)
                .centerCrop()
                .placeholder(R.drawable.ic_anime_placeholder)
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}

