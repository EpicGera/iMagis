package com.example.imagis.ui

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.imagis.api.Episode
import com.example.imagis.api.TmdbApiClient
import com.example.imagis.utils.MediaScraperEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener

class TvSeasonsFragment : BrowseSupportFragment(), TorrentListener {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var tvShowId: Int = 0
    private var tvShowName: String = ""
    private var torrentStream: TorrentStream? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Assume we passed the TMDB TV Show ID and Name via Intent
        tvShowId = requireActivity().intent.getIntExtra("TV_SHOW_ID", 0)
        tvShowName = requireActivity().intent.getStringExtra("TV_SHOW_NAME") ?: "Unknown Series"

        title = tvShowName
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        setupEventListeners()
        loadSeasons()
    }

    private fun loadSeasons() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Fetch the TV Show details to get the list of Seasons
                val showDetails = TmdbApiClient.service.getTvShowDetails(tvShowId, TmdbApiClient.API_KEY)
                
                // 2. Filter out "Specials" (Season 0) if desired, and loop through seasons
                val validSeasons = showDetails.seasons.filter { it.season_number > 0 }
                
                withContext(Dispatchers.Main) {
                    for (season in validSeasons) {
                        val episodePresenter = EpisodeCardPresenter()
                        val episodeAdapter = ArrayObjectAdapter(episodePresenter)
                        val header = HeaderItem(season.season_number.toLong(), "Season ${season.season_number}")
                        
                        val row = ListRow(header, episodeAdapter)
                        rowsAdapter.add(row)

                        // 3. Fetch episodes for this specific season in the background
                        fetchEpisodesForSeason(season.season_number, episodeAdapter)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load TV Show data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchEpisodesForSeason(seasonNumber: Int, adapter: ArrayObjectAdapter) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val seasonData = TmdbApiClient.service.getSeasonDetails(tvShowId, seasonNumber, TmdbApiClient.API_KEY)
                withContext(Dispatchers.Main) {
                    adapter.addAll(0, seasonData.episodes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Episode) {
                val episodeTag = String.format("S%02dE%02d", item.season_number, item.episode_number)
                Toast.makeText(requireContext(), "🔍 Searching sources for $tvShowName $episodeTag...", Toast.LENGTH_SHORT).show()
                
                lifecycleScope.launch(Dispatchers.IO) {
                    // Progress callback for per-source toasts
                    val progressCallback: (String) -> Unit = { status ->
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    // 1. Try P2P torrent search first (EZTV, Apibay, LimeTorrents)
                    val magnetUrl = MediaScraperEngine.findMagnetForEpisode(
                        tvShowName,
                        item.season_number,
                        item.episode_number,
                        progressCallback
                    )
                    
                    if (magnetUrl != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "✅ Torrent found! Starting P2P stream...", Toast.LENGTH_SHORT).show()
                            startTorrentStream(magnetUrl, "$tvShowName $episodeTag")
                        }
                        return@launch
                    }
                    
                    // 2. Fallback: search local Room DB
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "🔍 No torrents found. Checking local database...", Toast.LENGTH_SHORT).show()
                    }
                    
                    val streamUrl = MediaScraperEngine.findEpisodeStream(
                        requireContext(), 
                        tvShowName, 
                        item.season_number, 
                        item.episode_number
                    )
                    
                    withContext(Dispatchers.Main) {
                        if (streamUrl != null) {
                            Toast.makeText(requireContext(), "✅ Stream found! Starting playback...", Toast.LENGTH_SHORT).show()
                            val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                            intent.putExtra("VIDEO_URL", streamUrl)
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "❌ No sources found for $tvShowName $episodeTag.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // --- P2P Torrent Streaming ---

    private fun startTorrentStream(magnetUrl: String, title: String) {
        if (torrentStream?.isStreaming == true) {
            torrentStream?.stopStream()
        }

        val torrentOptions = TorrentOptions.Builder()
            .saveLocation(requireContext().cacheDir)
            .removeFilesAfterStop(true)
            .autoDownload(true)
            .build()

        torrentStream = TorrentStream.init(torrentOptions)
        torrentStream?.addListener(this)
        
        Toast.makeText(requireContext(), "Conectando al enjambre P2P...", Toast.LENGTH_SHORT).show()
        torrentStream?.startStream(magnetUrl)
    }

    override fun onStreamPrepared(torrent: Torrent?) {
        Log.d("P2P_TV", "Stream Prepared!")
    }

    override fun onStreamStarted(torrent: Torrent?) {
        Log.d("P2P_TV", "Stream Started!")
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), "Conectando al enjambre (Swarm)...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Log.e("P2P_TV", "Stream Error: ${e?.message}")
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), "Error en P2P: ${e?.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStreamReady(torrent: Torrent?) {
        Log.d("P2P_TV", "Stream Ready! Local File: ${torrent?.videoFile?.absolutePath}")
        
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "¡Servidor Local Listo! Lanzando ExoPlayer...", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", torrent?.videoFile?.absolutePath)
            startActivity(intent)
        }
    }

    override fun onStreamProgress(torrent: Torrent?, status: com.github.se_bastiaan.torrentstream.StreamStatus?) {
        // Log.d("P2P_TV", "Progress: ${status?.progress}%")
    }

    override fun onStreamStopped() {
        Log.d("P2P_TV", "Stream Stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        torrentStream?.removeListener(this)
        torrentStream?.stopStream()
    }
}
