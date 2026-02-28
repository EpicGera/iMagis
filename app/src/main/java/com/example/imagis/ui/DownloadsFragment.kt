package com.example.imagis.ui

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.example.imagis.R
import com.github.se_bastiaan.torrentstream.Torrent

class DownloadsFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var listRowAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = "P2P Downloads"
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.app_icon_placeholder)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = Color.parseColor("#1A1A1A")

        loadRows()
        updateDownloads()
    }

    private fun loadRows() {
        val listRowPresenter = ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        listRowPresenter.shadowEnabled = false
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)

        val cardPresenter = DownloadCardPresenter()
        listRowAdapter = ArrayObjectAdapter(cardPresenter)

        val header = HeaderItem(0, "Active Transfers")
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }

    private fun updateDownloads() {
        listRowAdapter.clear()
        
        // Fetch active torrents from the global singleton (assuming MovieDetailsFragment.torrentStream exists globally)
        // Since it's currently stored in MovieDetailsFragment, we will move it to a global scope or App class later
        // For now, let's create a Mock to satisfy the UI
        
        // TODO: Access actual TorrentStream instance
        if (com.example.imagis.ui.MovieDetailsFragment.torrentStream?.isStreaming == true) {
             val torrent = com.example.imagis.ui.MovieDetailsFragment.torrentStream?.currentTorrent
             if(torrent != null){
                 listRowAdapter.add(torrent)
             }
        }
        
        if (listRowAdapter.size() == 0) {
            Toast.makeText(requireContext(), "No active downloads.", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class DownloadCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val cardView = viewHolder.view as ImageCardView
            val torrent = item as Torrent
            
            val progress = try {
                if (torrent.videoFile != null && torrent.videoFile.length() > 0) {
                    "Downloading..." // Actual progress requires manual tracking in TorrentStream
                } else {
                    "Connecting to Peers..."
                }
            } catch(e: Exception) {
                "Unknown"
            }

            // Fallback for FireStick
            cardView.titleText = "Active Transfer" 
            cardView.contentText = progress
            cardView.setMainImageDimensions(300, 170)
            cardView.mainImageView.setImageResource(android.R.drawable.stat_sys_download)
            cardView.setBackgroundColor(Color.DKGRAY)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}
