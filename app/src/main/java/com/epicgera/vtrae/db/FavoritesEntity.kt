package com.epicgera.vtrae.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey
    val id: String, // TMDB ID, Anime slug, or VOD URL
    val title: String,
    val posterUrl: String,
    val type: String, // "MOVIE", "SERIES", "ANIME", "VOD"
    val timestamp: Long = System.currentTimeMillis()
)

