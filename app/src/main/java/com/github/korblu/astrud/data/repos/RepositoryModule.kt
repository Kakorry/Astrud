package com.github.korblu.astrud.data.repos

import com.github.korblu.astrud.data.room.dao.RoomRecentsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRoomRecentsRepository(recentsDao: RoomRecentsDao) : RoomRecentsRepo {
        return RoomRecentsRepo(recentsDao)
    }
}