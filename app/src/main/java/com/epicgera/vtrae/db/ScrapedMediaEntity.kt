package com.epicgera.vtrae.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scraped_media")
data class ScrapedMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String, // Identifies the search, e.g., series title + season/episode
    val mediaType: String, // "TORRENT" or "DDL"
    val title: String,
    val source: String, // Torrent source or DDL hostFullName
    val sizeDisplay: String,
    
    // Torrent-specific
    val magnetUrl: String? = null,
    val seeds: Int = 0,
    
    // DDL-specific
    val quality: String? = null,
    val downloadUrl: String? = null
)

