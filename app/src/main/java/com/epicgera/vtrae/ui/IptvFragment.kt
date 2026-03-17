package com.epicgera.vtrae.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.epicgera.vtrae.R
import com.epicgera.vtrae.data.ChannelStore
import com.epicgera.vtrae.data.IptvChannel
import com.epicgera.vtrae.utils.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class IptvFragment : VerticalGridSupportFragment() {

    private lateinit var mAdapter: ArrayObjectAdapter
    private var playlistUrl: String? = null
    private var playlistName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        playlistUrl = activity?.intent?.getStringExtra("PLAYLIST_URL")
        playlistName = activity?.intent?.getStringExtra("PLAYLIST_NAME")

        title = playlistName ?: "Live TV Categories"
        
        setupFragment()
        
        if (playlistUrl != null) {
            loadChannels(playlistUrl!!)
        } else {
            loadPlaylists()
        }
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        // PresenterSelector to handle both Playlists and Channels
        val presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(com.epicgera.vtrae.data.PlaylistSource::class.java, PlaylistPresenter())
        presenterSelector.addClassPresenter(IptvChannel::class.java, ChannelCardPresenter())
        
        mAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = mAdapter

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is com.epicgera.vtrae.data.PlaylistSource) {
                // Open Category
                val intent = android.content.Intent(requireContext(), IptvActivity::class.java)
                intent.putExtra("PLAYLIST_URL", item.url)
                intent.putExtra("PLAYLIST_NAME", item.name)
                startActivity(intent)
            } else if (item is IptvChannel) {
                // Play Channel
                val intent = android.content.Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", item.streamUrl)
                intent.putExtra("IS_VOD_PAGE", false)
                // Pass HTTP headers for CDNs that require Referer/UA
                item.referrer?.let { intent.putExtra("HTTP_REFERRER", it) }
                item.userAgent?.let { intent.putExtra("HTTP_USER_AGENT", it) }
                // Pass Google DAI event ID for session-based streams
                item.daiEventId?.let { intent.putExtra("DAI_EVENT_ID", it) }
                startActivity(intent)
            }
        }
    }

    private fun loadPlaylists() {
        // Load categories from ChannelStore
        ChannelStore.preLoadedPlaylists.forEach { mAdapter.add(it) }
    }

    private fun loadChannels(urlStr: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Update UI state
                withContext(Dispatchers.Main) {
                     // potentially show loading spinner here
                }

                val url = URL(urlStr)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                
                if (connection.responseCode != 200) {
                     throw Exception("HTTP Error: ${connection.responseCode}")
                }
                
                val playlistContent = connection.inputStream.bufferedReader().use { it.readText() }
                val channels = M3uParser.parse(playlistContent)

                withContext(Dispatchers.Main) {
                    if (channels.isEmpty()) {
                        com.epicgera.vtrae.ui.components.VtrToastManager.showInfo("No channels found in this playlist.")
                    } else {
                        channels.forEach { mAdapter.add(it) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    com.epicgera.vtrae.ui.components.VtrToastManager.showError("Error: ${e.message}")
                }
            }
        }
    }

    // Presenter for Categories/Playlists
    private inner class PlaylistPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val playlist = item as com.epicgera.vtrae.data.PlaylistSource
            
            cardView.titleText = playlist.name
            cardView.contentText = playlist.description
            cardView.setMainImageDimensions(300, 150) // Wider for categories
            cardView.mainImage = viewHolder.view.context.getDrawable(R.drawable.ic_tv_placeholder)
            cardView.setBackgroundColor(android.graphics.Color.DKGRAY)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    // Presenter for Channels
    private inner class ChannelCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val channel = item as IptvChannel
            
            cardView.titleText = channel.name
            cardView.contentText = channel.group
            cardView.setMainImageDimensions(200, 200) // Square logos
            
            com.bumptech.glide.Glide.with(viewHolder.view.context)
                .load(channel.logoUrl)
                .centerInside()
                .placeholder(R.drawable.ic_tv_placeholder)
                .error(R.drawable.ic_tv_placeholder)
                .into(cardView.mainImageView)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}

