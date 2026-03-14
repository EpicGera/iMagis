package com.epicgera.vtrae.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChannelDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    // For searching Movies or Live TV (single result — legacy)
    @Query("SELECT streamUrl FROM channels WHERE lowercaseName LIKE '%' || :searchQuery || '%' LIMIT 1")
    suspend fun findStreamByTitle(searchQuery: String): String?

    // Smart search: returns multiple candidates for relevance scoring
    @Query("SELECT lowercaseName, streamUrl FROM channels WHERE lowercaseName LIKE '%' || :searchQuery || '%' LIMIT 20")
    suspend fun findStreamCandidates(searchQuery: String): List<StreamCandidate>

    // NEW: For searching exact TV Episodes
    @Query("SELECT streamUrl FROM channels WHERE isSeries = 1 AND lowercaseName LIKE '%' || :seriesName || '%' AND season = :targetSeason AND episode = :targetEpisode LIMIT 1")
    suspend fun findEpisodeStream(seriesName: String, targetSeason: Int, targetEpisode: Int): String?

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelCount(): Int
    
    @Query("DELETE FROM channels")
    suspend fun nukeTable()
}

