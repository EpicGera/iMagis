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
            logoUrl = "",
            streamUrl = "serenotv://locomotion",
            fallbackUrls = emptyList(),
            group = "Animación"
        ),
        IptvChannel(
            name = "Magic Kids",
            logoUrl = "",
            streamUrl = "https://kiiroilabs.ddns.net/magickids/stream/stream.m3u8",
            fallbackUrls = emptyList(),
            group = "Animación"
        ),
        
        // ── NOTICIAS ─────────────────────────────────────────────────
        IptvChannel(
            name = "TN - Todo Noticias",
            logoUrl = "",
            streamUrl = "dai://5OEEtA9FR-yrvhNE5K8PQQ",
            fallbackUrls = emptyList(),
            group = "Noticias",
            daiEventId = "5OEEtA9FR-yrvhNE5K8PQQ",
            localLogoRes = "logo_tn"
        ),
        IptvChannel(
            name = "Canal 26",
            logoUrl = "",
            streamUrl = "https://stream-gtlc.telecentro.net.ar/hls/canal26hls/main.m3u8",
            fallbackUrls = emptyList(),
            group = "Noticias",
            localLogoRes = "logo_canal26"
        ),

        // ── ENTRETENIMIENTO ──────────────────────────────────────────
        IptvChannel(
            name = "América TV",
            logoUrl = "",
            streamUrl = "dai://OY2i_lL4SMyXE5Zaj4ULEg",
            fallbackUrls = emptyList(),
            group = "Entretenimiento",
            daiEventId = "OY2i_lL4SMyXE5Zaj4ULEg",
            localLogoRes = "logo_america"
        ),
        IptvChannel(
            name = "Telefe",
            logoUrl = "",
            streamUrl = "https://telefeappmitelefe1.akamaized.net/hls/live/2037985/appmitelefe/TOK/master.m3u8",
            fallbackUrls = emptyList(),
            group = "Entretenimiento",
            referrer = "https://mitelefe.com/",
            tokenizeUrl = "https://mitelefe.com/vidya/tokenize",
            localLogoRes = "logo_telefe"
        ),

        IptvChannel(
            name = "Canal 9",
            logoUrl = "",
            streamUrl = "https://stream.arcast.live/ahora/ahora/playlist.m3u8",
            fallbackUrls = listOf(
                "https://unlimited1-saopaulo.dps.live/televidaar/televidaar.smil/playlist.m3u8",
                "http://coninfo.net:1935/9linklivert/smil:9linkmultibr.smil/playlist.m3u8"
            ),
            group = "Entretenimiento",
            localLogoRes = "logo_canal9"
        ),

        // ── DEPORTES ─────────────────────────────────────────────────
        IptvChannel(
            name = "TyC Sports",
            logoUrl = "",
            streamUrl = "https://live-04-11-tyc24.vodgc.net/tyc24/index_tyc24_1080.m3u8",
            fallbackUrls = emptyList(),
            group = "Deportes",
            localLogoRes = "logo_tycsports"
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


