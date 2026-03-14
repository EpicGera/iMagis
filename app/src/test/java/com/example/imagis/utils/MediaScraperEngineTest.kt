package com.epicgera.vtrae.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLEncoder

class MediaScraperEngineTest {

    @Test
    fun `formatSize formats bytes to megabytes and gigabytes correctly`() {
        // Test zero or negative
        assertEquals("?", MediaScraperEngine.formatSize(0))
        assertEquals("?", MediaScraperEngine.formatSize(-100))

        // Test MB (less than 1 GB)
        val oneMB = 1024L * 1024L
        assertEquals("1 MB", MediaScraperEngine.formatSize(oneMB))
        assertEquals("500 MB", MediaScraperEngine.formatSize(oneMB * 500))

        // Test GB (1 GB or more)
        val oneGB = 1024L * 1024L * 1024L
        assertEquals("1.0 GB", MediaScraperEngine.formatSize(oneGB))
        assertEquals("1.5 GB", MediaScraperEngine.formatSize(oneGB + oneMB * 512))
    }

    @Test
    fun `buildMagnetLink creates valid magnet URI with trackers`() {
        val infoHash = "1234567890abcdef1234567890abcdef12345678"
        val displayName = "Test Movie 2024"
        val encodedName = URLEncoder.encode(displayName, "UTF-8")

        val magnetLink = MediaScraperEngine.buildMagnetLink(infoHash, encodedName)

        assertTrue(magnetLink.startsWith("magnet:?xt=urn:btih:$infoHash"))
        assertTrue(magnetLink.contains("&dn=Test+Movie+2024"))
        
        // Check if trackers are encoded and present
        val expectedTracker = URLEncoder.encode("udp://tracker.opentrackr.org:1337/announce", "UTF-8")
        assertTrue(magnetLink.contains("&tr=$expectedTracker"))
    }
}

