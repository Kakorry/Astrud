package com.github.korblu.astrud.data.repos

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.github.korblu.astrud.data.room.SongsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SongRepositoryModule {
    @Provides
    @Singleton
    fun provideSongRepository(songDao: SongsDao): SongsRepo {
        return SongsRepo(songDao)
    }
}