// FILE_PATH: app/src/main/java/com/epicgera/vtrae/db/WatchHistoryEntity.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lightweight watch history entry.
 * Stores ONLY metadata — no partial video files.
 * Max 10 entries enforced by DAO trim query.
 */
@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val id: String,              // TMDB ID, anime slug, or VOD URL
    val title: String,           // Display title (e.g. "Breaking Bad")
    val episodeLabel: String?,   // e.g. "S02E05", "Episode 12", null for movies
    val status: String,          // "ONGOING" or "WATCHED"
    val positionMs: Long,        // Playback position in milliseconds
    val durationMs: Long,        // Total duration in milliseconds
    val posterUrl: String?,      // Thumbnail URL for the card
    val videoUrl: String?,       // Last played stream URL (for resume)
    val type: String,            // "MOVIE", "SERIES", "ANIME", "VOD"
    val timestamp: Long = System.currentTimeMillis()
)

