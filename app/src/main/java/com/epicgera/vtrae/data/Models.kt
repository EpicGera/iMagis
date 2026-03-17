package com.epicgera.vtrae.data

/**
 * Data class representing a single Anime Series.
 */
data class AnimeSeries(
    val title: String,
    val type: String, // "TV", "OVA", "Movie", etc.
    val imageUrl: String,
    val seriesUrl: String
)

/**
 * Data class representing a single Anime Episode.
 */
data class AnimeEpisode(
    val title: String,
    val episodeNumber: String,
    val imageUrl: String,
    val episodeUrl: String
)

/**
 * Data class representing a Video Stream source.
 */
data class VideoServer(
    val serverName: String,
    val embedUrl: String
)

/**
 * Data class representing an IPTV Channel.
 */
data class IptvChannel(
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val fallbackUrls: List<String> = emptyList(),
    val group: String?,
    val isSeries: Boolean = false,
    val season: Int? = null,
    val episode: Int? = null,
    val referrer: String? = null,
    val userAgent: String? = null,
    val daiEventId: String? = null
)

/**
 * Data class representing a Playlist Source.
 */
data class PlaylistSource(
    val name: String, 
    val url: String, 
    val description: String
)

/**
 * Data class representing VOD Content (Movie/Series).
 */
data class VodContent(
    val title: String,
    val posterUrl: String?,
    val streamUrl: String,
    val category: String, // e.g., "Action", "Drama"
    val isSeries: Boolean = false
)

