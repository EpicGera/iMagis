package com.example.imagis.data

import java.io.Serializable

/**
 * Represents a single torrent search result from any provider.
 * Shown to the user so they can pick which one to stream.
 */
data class TorrentResult(
    val title: String,
    val magnetUrl: String,
    val source: String,       // e.g. "YTS", "EZTV", "PirateBay", "LimeTorrents", "Nyaa", "1337x", "MSearch"
    val seeds: Int = 0,
    val sizeDisplay: String = "Unknown"  // e.g. "1.4 GB", "720 MB"
) : Serializable
