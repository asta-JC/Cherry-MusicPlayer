package com.asta.cherrymusicplayer.di

import android.content.Context
import androidx.room.Room
import com.asta.cherrymusicplayer.data.dao.MusicDao
import com.asta.cherrymusicplayer.data.database.CherryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCherryDatabase(
        @ApplicationContext context: Context
    ): CherryDatabase {
        return Room.databaseBuilder(
            context,
            CherryDatabase::class.java,
            CherryDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideMusicDao(database: CherryDatabase): MusicDao {
        return database.musicDao()
    }
}
