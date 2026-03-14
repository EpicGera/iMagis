package com.epicgera.vtrae.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapedMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<ScrapedMediaEntity>)

    @Query("SELECT * FROM scraped_media WHERE query = :query ORDER BY seeds DESC")
    fun getMediaResultsFlow(query: String): Flow<List<ScrapedMediaEntity>>
    
    @Query("SELECT * FROM scraped_media WHERE query = :query ORDER BY seeds DESC")
    suspend fun getMediaResults(query: String): List<ScrapedMediaEntity>

    @Query("DELETE FROM scraped_media WHERE query = :query")
    suspend fun deleteByQuery(query: String)
}

