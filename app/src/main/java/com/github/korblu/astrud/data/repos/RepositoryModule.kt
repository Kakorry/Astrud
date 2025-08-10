package com.github.korblu.astrud.data.repos

import android.content.Context
import com.github.korblu.astrud.data.room.dao.AlbumsPlayedDao
import com.github.korblu.astrud.data.room.dao.ArtistsPlayedDao
import com.github.korblu.astrud.data.room.dao.OtherStatsDao
import com.github.korblu.astrud.data.room.dao.RecentsDao
import com.github.korblu.astrud.data.room.dao.SongsPlayedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRoomRecentsRepository(recentsDao: RecentsDao) : RoomRecentsRepo {
        return RoomRecentsRepo(recentsDao)
    }

    @Provides
    @Singleton
    fun provideUserStatsRepository(
        songsPlayedDao: SongsPlayedDao,
        albumsPlayedDao: AlbumsPlayedDao,
        artistsPlayedDao: ArtistsPlayedDao,
        otherStatsDao: OtherStatsDao,
        @ApplicationContext context: Context
        ) : UserStatsRepo {
        return UserStatsRepo(
            songsPlayedDao,
            albumsPlayedDao,
            artistsPlayedDao,
            otherStatsDao,
            context
        )
    }
}