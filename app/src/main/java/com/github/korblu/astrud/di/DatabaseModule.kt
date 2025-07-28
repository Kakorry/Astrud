package com.github.korblu.astrud.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.github.korblu.astrud.data.datastore.LayoutSongsList
import com.github.korblu.astrud.data.datastore.LayoutSongsListSerializer
import com.github.korblu.astrud.data.room.AppDatabase
import com.github.korblu.astrud.data.room.dao.RoomRecentsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "astrud_app_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideLayoutSongsListDataStore(@ApplicationContext appContext: Context): DataStore<LayoutSongsList> {
        return DataStoreFactory.create(
            serializer = LayoutSongsListSerializer,
            produceFile = { appContext.dataStoreFile("layout_songs_list.pb") },
            corruptionHandler = ReplaceFileCorruptionHandler {
                LayoutSongsList.getDefaultInstance()
            },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Singleton
    @Provides
    fun provideRoomRecentsDao(appDatabase: AppDatabase): RoomRecentsDao {
        return appDatabase.roomRecentsDao()
    }
}