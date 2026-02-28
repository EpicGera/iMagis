package com.example.imagis.utils

import com.example.imagis.data.VodContent
import java.io.BufferedReader
import java.io.StringReader

object VodParser {

    fun parse(playlistContent: String): List<VodContent> {
        val contentList = mutableListOf<VodContent>()
        val reader = BufferedReader(StringReader(playlistContent))
        
        var line: String? = reader.readLine()
        
        var currentTitle: String? = null
        var currentPoster: String? = null
        var currentCategory: String? = null

        while (line != null) {
            val trimmed = line.trim()

            if (trimmed.startsWith("#EXTINF")) {
                // Example: #EXTINF:-1 tvg-logo="http://..." group-title="Action", Movie Title
                
                // Extract Logo (Poster)
                val logoRegex = Regex("tvg-logo=\"([^\"]+)\"")
                val logoMatch = logoRegex.find(trimmed)
                currentPoster = logoMatch?.groupValues?.get(1)

                // Extract Group (Category)
                val groupRegex = Regex("group-title=\"([^\"]+)\"")
                val groupMatch = groupRegex.find(trimmed)
                currentCategory = groupMatch?.groupValues?.get(1) ?: "Uncategorized"

                // Extract Title
                currentTitle = trimmed.substringAfterLast(",").trim()
            }
            else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                if (currentTitle != null) {
                    contentList.add(
                        VodContent(
                            title = currentTitle,
                            posterUrl = currentPoster,
                            streamUrl = trimmed,
                            category = currentCategory ?: "General"
                        )
                    )
                    // Reset
                    currentTitle = null
                    currentPoster = null
                    currentCategory = null
                }
            }
            
            line = reader.readLine()
        }

        return contentList
    }
}
