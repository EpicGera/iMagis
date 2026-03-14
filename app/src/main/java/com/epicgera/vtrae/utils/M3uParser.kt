package com.epicgera.vtrae.utils

import com.epicgera.vtrae.data.IptvChannel
import java.io.BufferedReader
import java.io.StringReader

object M3uParser {

    // Regex pattern 1: Looks for S01E04, s1e4, Season 1 Episode 4
    private val standardRegex = Regex("(?i)(?:s|season)\\s*(\\d+)\\s*(?:e|ep|episode)\\s*(\\d+)")
    // Regex pattern 2: Looks for formats like 1x04
    private val altRegex = Regex("(?i)(\\d+)x(\\d+)")

    fun parse(playlistContent: String): List<IptvChannel> {
        val channels = mutableListOf<IptvChannel>()
        val reader = BufferedReader(StringReader(playlistContent))
        
        var line: String? = reader.readLine()
        
        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null

        while (line != null) {
            val trimmed = line.trim()

            if (trimmed.startsWith("#EXTINF")) {
                val logoRegex = Regex("tvg-logo=\"([^\"]+)\"")
                currentLogo = logoRegex.find(trimmed)?.groupValues?.get(1)

                val groupRegex = Regex("group-title=\"([^\"]+)\"")
                currentGroup = groupRegex.find(trimmed)?.groupValues?.get(1) ?: "Uncategorized"

                currentName = trimmed.substringAfterLast(",").trim()
            }
            else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                if (currentName != null) {
                    
                    // --- TV SHOW DETECTION LOGIC ---
                    var isTvShow = false
                    var extractedSeason: Int? = null
                    var extractedEpisode: Int? = null
                    var cleanSeriesName = currentName

                    // Test Regex 1 (S01E04)
                    var matchResult = standardRegex.find(currentName)
                    if (matchResult == null) {
                        // Test Regex 2 (1x04)
                        matchResult = altRegex.find(currentName)
                    }

                    if (matchResult != null) {
                        isTvShow = true
                        extractedSeason = matchResult.groupValues[1].toIntOrNull()
                        extractedEpisode = matchResult.groupValues[2].toIntOrNull()
                        
                        // Clean the name: "Breaking Bad S01E04 1080p" -> "Breaking Bad"
                        // We slice the string right before the Regex match
                        val matchStartIndex = matchResult.range.first
                        cleanSeriesName = currentName.substring(0, matchStartIndex).trim()
                        
                        // Strip trailing characters like hyphens or dots
                        cleanSeriesName = cleanSeriesName.replace(Regex("[-._]+$"), "").trim()
                    }

                    channels.add(
                        IptvChannel(
                            name = if (isTvShow) cleanSeriesName else currentName,
                            logoUrl = currentLogo,
                            streamUrl = trimmed,
                            group = currentGroup,
                            isSeries = isTvShow,
                            season = extractedSeason,
                            episode = extractedEpisode
                        )
                    )
                    
                    currentName = null
                    currentLogo = null
                    currentGroup = null
                }
            }
            line = reader.readLine()
        }
        return channels
    }
}

