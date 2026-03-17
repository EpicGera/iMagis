package com.epicgera.vtrae.data

object ChannelStore {
    // Centralized list of all loaded VOD content for the Scraper Engine to access
    val globalVodList = java.util.Collections.synchronizedList(mutableListOf<VodContent>())

    // ── ARGENTINA M3U SOURCES (local optimized copy) ──────────
    // Load our pre-validated local copy from res/raw to ensure 100% working channels
    val argentinaPlaylistSources = listOf(
        "android.resource://com.epicgera.vtrae/raw/ar_working"
    )

    // ── CURATED ARGENTINA CHANNELS (hardcoded with fallbacks) ────────
    val curatedArgentinaChannels = listOf(
        
        // ── NOSTALGIA / RETRO ────────────────────────────────────────
        IptvChannel(
            name = "Locomotion",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Locomotion_logo.svg/512px-Locomotion_logo.svg.png",
            streamUrl = "http://51.222.85.85:81/hls/loco/index.m3u8",
            fallbackUrls = emptyList(),
            group = "Animación"
        ),
        
        // ── NOTICIAS ─────────────────────────────────────────────────
        IptvChannel(
            name = "TN - Todo Noticias",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/TN_logo.svg/512px-TN_logo.svg.png",
            streamUrl = "http://190.104.226.30/Live/870787012c00961adaf9b2304d704b57/tn_720.m3u8",
            fallbackUrls = listOf(
                "https://media.neuquen.gov.ar/rtn/television/media.m3u8"
            ),
            group = "Noticias"
        ),
        IptvChannel(
            name = "Canal 26",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Canal_26_logo.png/512px-Canal_26_logo.png",
            streamUrl = "https://stream-gtlc.telecentro.net.ar/hls/canal26hls/main.m3u8",
            fallbackUrls = emptyList(),
            group = "Noticias"
        ),

        // ── ENTRETENIMIENTO ──────────────────────────────────────────
        IptvChannel(
            name = "América TV",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/America_TV_2020.svg/512px-America_TV_2020.svg.png",
            streamUrl = "dai://OY2i_lL4SMyXE5Zaj4ULEg",  // Resolved at runtime via Google DAI
            fallbackUrls = emptyList(),
            group = "Entretenimiento",
            daiEventId = "OY2i_lL4SMyXE5Zaj4ULEg"
        ),
        IptvChannel(
            name = "Telefe",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cc/Telefe_%28nuevo_logo%29.png/512px-Telefe_%28nuevo_logo%29.png",
            streamUrl = "http://190.104.226.30/Live/870787012c00961adaf9b2304d704b57/telefe_720.m3u8",
            fallbackUrls = emptyList(),
            group = "Entretenimiento"
        ),

        IptvChannel(
            name = "Canal 9",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Canal_9_logo.svg/512px-Canal_9_logo.svg.png",
            streamUrl = "https://stream.arcast.live/ahora/ahora/playlist.m3u8",
            fallbackUrls = listOf(
                "https://unlimited1-saopaulo.dps.live/televidaar/televidaar.smil/playlist.m3u8",
                "http://coninfo.net:1935/9linklivert/smil:9linkmultibr.smil/playlist.m3u8"
            ),
            group = "Entretenimiento"
        ),

        // ── DEPORTES ─────────────────────────────────────────────────
        IptvChannel(
            name = "TyC Sports",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/TyC_Sports_logo.svg/512px-TyC_Sports_logo.svg.png",
            streamUrl = "https://live-04-11-tyc24.vodgc.net/tyc24/index_tyc24_1080.m3u8",
            fallbackUrls = emptyList(),
            group = "Deportes"
        )
    )

    val preLoadedPlaylists = listOf(
        // 0. Argentina Exclusive (VIP) - Validated
        PlaylistSource(
            "Argentina Live", 
            "android.resource://com.epicgera.vtrae/raw/ar_working",
            "Canales en vivo de Argentina. Validado y sin enlaces caídos."
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


