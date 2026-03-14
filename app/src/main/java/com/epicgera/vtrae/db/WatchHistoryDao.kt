// FILE_PATH: app/src/main/java/com/epicgera/vtrae/db/WatchHistoryDao.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchHistoryDao {

    /**
     * Returns the most recent 10 entries, ordered newest-first.
     */
    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 10")
    suspend fun getAll(): List<WatchHistoryEntity>

    /**
     * Single lookup by ID (TMDB ID / slug / URL).
     */
    @Query("SELECT * FROM watch_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): WatchHistoryEntity?

    /**
     * Insert or replace an entry (upsert pattern).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: WatchHistoryEntity)

    /**
     * Remove a specific entry.
     */
    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Enforce the 10-title cap.
     * Deletes all rows except the 10 most recent by timestamp.
     */
    @Query("""
        DELETE FROM watch_history 
        WHERE id NOT IN (
            SELECT id FROM watch_history ORDER BY timestamp DESC LIMIT 10
        )
    """)
    suspend fun trimToMax()
}

