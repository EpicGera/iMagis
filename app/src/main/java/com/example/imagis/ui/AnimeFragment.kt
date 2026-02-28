package com.example.imagis.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.imagis.R
import com.example.imagis.data.AnimeEpisode
import com.example.imagis.utils.JkanimeScraper
import kotlinx.coroutines.launch

class AnimeFragment : VerticalGridSupportFragment() {

    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Latest Anime"
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
                // TODO: Show Server Selection Dialog or Play directly
                // For now, we launch player with the episode URL to extract video there
                val intent = android.content.Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", item.episodeUrl) 
                intent.putExtra("IS_VOD_PAGE", true) // Flag to tell player to scrape this page
                startActivity(intent)
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val episodes = JkanimeScraper.getLatestEpisodes()
                if (episodes.isEmpty()) {
                    Toast.makeText(requireContext(), "No episodes found (Parse Empty)", Toast.LENGTH_LONG).show()
                } else {
                    episodes.forEach { mAdapter.add(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
            
            // Using Glide to load image
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(episode.imageUrl)
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
