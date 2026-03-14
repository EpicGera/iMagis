package com.epicgera.vtrae.utils

import com.epicgera.vtrae.data.AnimeEpisode
import com.epicgera.vtrae.data.AnimeSeries
import com.epicgera.vtrae.data.VideoServer
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
                // We only want actual episode links, usually ending in numbers (e.g., /anime-name/1 or /anime-name/1/)
                if (href.matches(Regex(".*\\/\\d+/?$"))) {
                    
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
     * Fetches recent anime series by looking at the homepage updates.
     * The /directorio/ page is dynamically loaded via JS, so we use the homepage as a "recent series" directory.
     */
    suspend fun getAnimeDirectory(page: Int): List<AnimeSeries> = withContext(Dispatchers.IO) {
        val animeList = mutableListOf<AnimeSeries>()
        try {
            val doc = Jsoup.connect(BASE_URL)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get()

            val items = doc.select(".card a")
            val candidates = if (items.isEmpty()) doc.select("div.maximo a") else items

            for (element in candidates) {
                val href = element.attr("href")
                if (href.matches(Regex(".*\\/\\d+/?$"))) {
                    val card = element.parents().firstOrNull { it.className().contains("card") }
                    
                    val title = card?.select("h5, .title")?.text()?.ifEmpty { null } 
                        ?: element.attr("title").ifEmpty { null }
                        ?: href.trimEnd('/').split("/").dropLast(1).last().replace("-", " ").capitalize()
                    
                    val img = card?.select("img")?.attr("src") 
                        ?: element.select("img").attr("src")
                    val fullImgUrl = if (img.startsWith("http")) img else "$BASE_URL$img"

                    // Convert episode url (e.g. /naruto/1/) to series url (/naruto/)
                    val seriesUrl = href.trimEnd('/').split("/").dropLast(1).joinToString("/") + "/"
                    
                    // Add if not already in list (since multiple episodes of same series might drop at once)
                    if (animeList.none { it.seriesUrl == seriesUrl }) {
                        animeList.add(AnimeSeries(title, "ANIME", fullImgUrl, seriesUrl))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext animeList
    }

    suspend fun searchAnime(query: String): List<AnimeSeries> = withContext(Dispatchers.IO) {
        val animeList = mutableListOf<AnimeSeries>()
        try {
            val formattedQuery = query.replace(" ", "_")
            val url = "$BASE_URL/buscar/$formattedQuery/"
            
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get()

            val items = doc.select(".card a, .anime__item a")
            
            for (element in items) {
                val href = element.attr("href")
                if (href.startsWith(BASE_URL) && !href.contains("/buscar/") && !href.contains("/politica")) {
                    
                    var title = element.parents().firstOrNull { it.className().contains("card") }?.select("h5, .title")?.text() 
                    var type = "Anime"
                    var img = element.select("img").attr("src")
                    
                    // Try anime__item markup logic
                    val animeItem = element.parents().firstOrNull { it.className().contains("anime__item") }
                    if (animeItem != null) {
                        title = animeItem.select(".anime__item__text h5 a").text()
                        type = animeItem.select(".anime__item__text ul li.anime").text().ifEmpty { "Anime" }
                        img = animeItem.select(".anime__item__pic").attr("data-setbg")
                    }
                    
                    val finalTitle = if (!title.isNullOrEmpty()) title else element.attr("title").ifEmpty { "Unknown" }
                    if (img.isEmpty()) img = element.parents().firstOrNull { it.className().contains("card") }?.select("img")?.attr("src") ?: ""

                    val fullImgUrl = if (img.isNotEmpty() && !img.startsWith("http")) "$BASE_URL$img" else img

                    if (animeList.none { it.seriesUrl == href }) {
                        animeList.add(AnimeSeries(finalTitle, type, fullImgUrl, href))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext animeList
    }

    /**
     * Fetches all episodes for a specific anime series URL using the AJAX JSON API.
     * Steps:
     * 1. Load the series page to extract the anime ID and CSRF token
     * 2. POST to /ajax/episodes/{id}/ with the CSRF token
     * 3. Parse the paginated JSON response
     */
    suspend fun getSeriesEpisodes(seriesUrl: String): List<AnimeEpisode> = withContext(Dispatchers.IO) {
        val episodesList = mutableListOf<AnimeEpisode>()
        try {
            // Step 1: Load the series page
            val response = Jsoup.connect(seriesUrl)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .execute()

            val doc = response.parse()
            val cookies = response.cookies()

            val animeDetailsContent = doc.select(".anime__details__content").firstOrNull() ?: doc
            val seriesTitle = animeDetailsContent.select("h3, h3[itemprop=name]").firstOrNull()?.text() ?: "Unknown Series"
            
            val picElement = animeDetailsContent.select(".anime__details__pic, .series-img img")
            var seriesImg = picElement.attr("data-setbg").ifEmpty { picElement.attr("src") }
            seriesImg = if (seriesImg.isNotEmpty() && !seriesImg.startsWith("http")) "$BASE_URL$seriesImg" else seriesImg

            // Step 2: Extract Anime ID from the ajax endpoint references in the page
            val rawHtml = doc.html()
            val idMatch = Regex("""/ajax/episodes/(\d+)/""").find(rawHtml)
            val animeId = idMatch?.groupValues?.get(1)

            // Extract CSRF token
            val csrfToken = doc.select("meta[name=csrf-token]").attr("content")

            if (animeId != null && csrfToken.isNotEmpty()) {
                // Step 3: Fetch episodes via AJAX API (paginated)
                var currentPage = 1
                var lastPage = 1

                do {
                    try {
                        val ajaxUrl = "$BASE_URL/ajax/episodes/$animeId/$currentPage"
                        val ajaxResponse = Jsoup.connect(ajaxUrl)
                            .userAgent(USER_AGENT)
                            .ignoreContentType(true)
                            .ignoreHttpErrors(true)
                            .timeout(15000)
                            .header("X-Requested-With", "XMLHttpRequest")
                            .header("X-CSRF-TOKEN", csrfToken)
                            .header("Referer", seriesUrl)
                            .cookies(cookies)
                            .data("_token", csrfToken)
                            .method(org.jsoup.Connection.Method.POST)
                            .execute()

                        val jsonBody = ajaxResponse.body()
                        val jsonObj = org.json.JSONObject(jsonBody)
                        
                        lastPage = jsonObj.optInt("last_page", 1)
                        val dataArray = jsonObj.optJSONArray("data")

                        if (dataArray != null) {
                            for (i in 0 until dataArray.length()) {
                                val ep = dataArray.getJSONObject(i)
                                val epNumber = ep.optInt("number", 0)
                                val epTitle = ep.optString("title", "Episodio $epNumber")
                                val epImage = ep.optString("image", "")
                                val fullEpImg = if (epImage.isNotEmpty()) "https://cdn.jkdesa.com/assets/images/animes/video/image/$epImage" else seriesImg
                                val cleanUrl = seriesUrl.trimEnd('/')
                                val epUrl = "$cleanUrl/$epNumber/"

                                episodesList.add(AnimeEpisode(epTitle, "Episodio $epNumber", fullEpImg, epUrl))
                            }
                        }
                        currentPage++
                    } catch (e: Exception) {
                        e.printStackTrace()
                        break
                    }
                } while (currentPage <= lastPage)
            } else {
                // Fallback: try to get episode count from the static page text
                val countMatch = Regex("""Episodios:\s*(?:</span>)?\s*(\d+)""").find(rawHtml)
                val totalEps = countMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0

                if (totalEps > 0) {
                    val cleanUrl = seriesUrl.trimEnd('/')
                    for (i in totalEps downTo 1) {
                        episodesList.add(AnimeEpisode(seriesTitle, "Episodio $i", seriesImg, "$cleanUrl/$i/"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext episodesList.sortedByDescending { 
            it.episodeNumber.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 
        }
    }

    /**
     * Extracts direct streaming server URLs from a Jkanime episode page.
     * Jkanime embeds player URLs like jkplayer/um and jkplayer/umv in the HTML.
     */
    suspend fun getEpisodeServers(episodeUrl: String): List<VideoServer> = withContext(Dispatchers.IO) {
        val servers = mutableListOf<VideoServer>()
        try {
            val doc = Jsoup.connect(episodeUrl)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(15000)
                .get()

            val rawHtml = doc.html()

            // Extract server tab names
            val serverElements = doc.select(".bg-servers a")
            val serverNames = serverElements.map { it.text().trim() }

            // Extract jkplayer embed URLs from the raw HTML
            val playerPattern = Regex("""https://jkanime\.net/jkplayer/[^\s"'<>]+""")
            val playerMatches = playerPattern.findAll(rawHtml).map { it.value }.toList()

            // Map server names to player URLs
            for ((index, playerUrl) in playerMatches.withIndex()) {
                val serverName = if (index < serverNames.size) serverNames[index] else "Server ${index + 1}"
                servers.add(VideoServer(serverName = serverName, embedUrl = playerUrl))
            }

            // If no named servers found but we have player URLs, add them generically
            if (servers.isEmpty() && playerMatches.isNotEmpty()) {
                for ((index, playerUrl) in playerMatches.withIndex()) {
                    servers.add(VideoServer(serverName = "JKAnime Server ${index + 1}", embedUrl = playerUrl))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext servers
    }
}

