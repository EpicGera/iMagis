package com.example.imagis.data

object ChannelStore {
    // Centralized list of all loaded VOD content for the Scraper Engine to access
    val globalVodList = java.util.Collections.synchronizedList(mutableListOf<VodContent>())

    val preLoadedPlaylists = listOf(
        // 0. Argentina Exclusive (VIP)
        PlaylistSource(
            "Argentina Live", 
            "https://iptv-org.github.io/iptv/countries/ar.m3u",
            "Canales en vivo de Argentina. Noticias, Deportes, Cultura."
        ),

        // 1. Specific Playlists (Categories) - High Reliability
        PlaylistSource(
            "Movies", 
            "https://iptv-org.github.io/iptv/categories/movies.m3u",
            "Films from around the world. Public domain and indie."
        ),
        
        PlaylistSource(
            "TV Series", 
            "https://iptv-org.github.io/iptv/categories/series.m3u",
            "Marathon-worthy TV shows and series."
        ),
        
        PlaylistSource(
            "Animation", 
            "https://iptv-org.github.io/iptv/categories/animation.m3u",
            "Cartoons and Anime."
        ),

        PlaylistSource(
            "Comedy", 
            "https://iptv-org.github.io/iptv/categories/comedy.m3u",
            "Stand-up, sitcoms, and funny clips."
        ),

        PlaylistSource(
            "Documentary", 
            "https://iptv-org.github.io/iptv/categories/documentary.m3u",
            "Educational and nature documentaries."
        ),
        
        PlaylistSource(
            "Music", 
            "https://iptv-org.github.io/iptv/categories/music.m3u",
            "Music videos and live performances."
        ),

        PlaylistSource(
            "News", 
            "https://iptv-org.github.io/iptv/categories/news.m3u",
            "Live news broadcasts from major networks."
        ),

        // 2. Regional Playlists - Extensive
        PlaylistSource(
            "United States", 
            "https://iptv-org.github.io/iptv/countries/us.m3u",
            "Local and national channels from the USA."
        ),
        
        PlaylistSource(
            "United Kingdom", 
            "https://iptv-org.github.io/iptv/countries/uk.m3u",
            "Channels from the UK (BBC, ITV, etc.)."
        ),
        
        PlaylistSource(
            "Canada", 
            "https://iptv-org.github.io/iptv/countries/ca.m3u",
            "Canadian broadcast TV."
        )
    )
}


