package com.example.imagis.utils

import com.example.imagis.data.AnimeEpisode
import com.example.imagis.data.VideoServer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object JkanimeScraper {

    private const val BASE_URL = "https://jkanime.net"
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    /**
     * Fetches the latest added episodes from the homepage.
     */
    suspend fun getLatestEpisodes(): List<AnimeEpisode> = withContext(Dispatchers.IO) {
        val episodesList = mutableListOf<AnimeEpisode>()
        // We propagate exceptions now to show them in UI
        val doc: Document = Jsoup.connect(BASE_URL)
            .userAgent(USER_AGENT)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .timeout(15000) // Increased timeout
            .get()

            // Jkanime's new structure uses Cards (Bootstrap style)
            val items = doc.select(".card a") 

            // Fallback strategy
            val candidates = if (items.isEmpty()) doc.select("div.maximo a") else items

            if (candidates.isEmpty()) {
                 throw Exception("Parse Error: No anime items found (checked .card and .maximo)")
            }

            for (element in candidates) {
                val href = element.attr("href")
                // We only want actual episode links, usually ending in numbers (e.g., /anime-name/1/)
                if (href.matches(Regex(".*\\/\\d+\\/$"))) {
                    
                    // Try to find title in parent card
                    val card = element.parents().firstOrNull { it.className().contains("card") }
                    
                    val title = card?.select("h5, .title")?.text()?.ifEmpty { null } 
                        ?: element.attr("title").ifEmpty { null }
                        ?: href.trimEnd('/').split("/").dropLast(1).last().replace("-", " ").capitalize()
                    
                    val epNumber = card?.select("h6, .episode")?.text()?.ifEmpty { null }
                        ?: "Ep " + href.trimEnd('/').split("/").last()

                    val img = card?.select("img")?.attr("src") 
                        ?: element.select("img").attr("src")
                        
                    val fullImgUrl = if (img.startsWith("http")) img else "$BASE_URL$img"

                    episodesList.add(AnimeEpisode(title, epNumber, fullImgUrl, href))
                }
            }
        return@withContext episodesList
    }

    /**
     * Extracts video embed links for a specific episode URL.
     */
    suspend fun getVideoServers(url: String): List<VideoServer> = withContext(Dispatchers.IO) {
        val serverList = mutableListOf<VideoServer>()
        try {
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get()

            // 1. Script array parsing
            val scripts = doc.select("script")
            for (script in scripts) {
                val html = script.html()
                if (html.contains("var video = []")) {
                    val regex = "video\\[\\d+\\] = '.*src=\"([^\"]+)\"".toRegex()
                    val matches = regex.findAll(html)
                    matches.forEachIndexed { index, matchResult ->
                        val embedUrl = matchResult.groupValues[1]
                        serverList.add(VideoServer("Server ${index + 1}", embedUrl))
                    }
                }
            }
            
            // 2. Fallback: Iframe elements
            if (serverList.isEmpty()) {
                val iframes = doc.select(".player_conte iframe, .video_player iframe")
                iframes.forEachIndexed { index, iframe ->
                     val src = iframe.attr("src")
                     if (src.isNotEmpty()) {
                         serverList.add(VideoServer("Direct Server ${index + 1}", src))
                     }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext serverList
    }

    /**
     * Fetches the full anime directory.
     * Page 1: https://jkanime.net/directorio/
     * Page 2: https://jkanime.net/directorio/2/
     */
    suspend fun getAnimeDirectory(page: Int): List<AnimeEpisode> = withContext(Dispatchers.IO) {
        val animeList = mutableListOf<AnimeEpisode>()
        try {
            val url = if (page <= 1) "$BASE_URL/directorio/" else "$BASE_URL/directorio/$page/"
            
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get()

            // Directory usually uses the same .card or .anime__item structure
            val items = doc.select(".card a, .anime__item a")
            
            for (element in items) {
                val href = element.attr("href")
                // Directory links are like /anime-name/ (no numbers at end usually, or just /)
                if (href.startsWith(BASE_URL) && !href.contains("/directorio/") && !href.contains("/politica")) {
                    
                    val card = element.parents().firstOrNull { it.className().contains("card") }
                    
                    val title = card?.select("h5, .title")?.text() ?: element.attr("title").ifEmpty { "Unknown" }
                    val type = card?.select(".card-text, .type")?.text() ?: "Anime"
                    
                    val img = card?.select("img")?.attr("src") ?: element.select("img").attr("src")
                    val fullImgUrl = if (img.startsWith("http")) img else "$BASE_URL$img"

                    // Reusing AnimeEpisode model, but using 'type' as episode number text
                    animeList.add(AnimeEpisode(title, type, fullImgUrl, href))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext animeList
    }
}
