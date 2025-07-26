package com.github.korblu.astrud.data.room

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.github.korblu.astrud.data.datastore.LayoutSongsList
import com.github.korblu.astrud.data.datastore.LayoutSongsListSerializer
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
        return androidx.datastore.core.DataStoreFactory.create(
            serializer = LayoutSongsListSerializer,
            produceFile = { appContext.dataStoreFile("layout_songs_list.pb") },
            corruptionHandler = ReplaceFileCorruptionHandler {
                LayoutSongsList.getDefaultInstance()
            },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    @Provides
    @Singleton
    fun provideSongsDao(appDatabase: AppDatabase): SongsDao {
        return appDatabase.songDao()
    }
}