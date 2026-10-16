package com.asta.cherrymusicplayer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    val albumArtUri: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
