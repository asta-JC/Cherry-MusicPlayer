package com.asta.cherrymusicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asta.cherrymusicplayer.data.dao.MusicDao
import com.asta.cherrymusicplayer.data.entity.PlaylistEntity
import com.asta.cherrymusicplayer.data.entity.PlaylistSongCrossRef
import com.asta.cherrymusicplayer.data.entity.SongEntity

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CherryDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        const val DATABASE_NAME = "cherry_music_database"
    }
}
