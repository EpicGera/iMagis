package com.example.imagis.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChannelEntity::class, FavoritesEntity::class, WatchHistoryEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun channelDao(): ChannelDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun watchHistoryDao(): WatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mediahub_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
