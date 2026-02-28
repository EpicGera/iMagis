package com.example.imagis.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lowercaseName: String,
    val streamUrl: String,
    val logoUrl: String?,
    val groupName: String?,
    
    // NEW FIELDS FOR TV SHOWS
    val isSeries: Boolean = false,
    val season: Int? = null,
    val episode: Int? = null
)
