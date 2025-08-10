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
import com.github.korblu.astrud.data.room.MIGRATION_1_2
import com.github.korblu.astrud.data.room.MIGRATION_2_3
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
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3)
            .build()
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
    fun provideRoomRecentsDao(appDatabase: AppDatabase) : RecentsDao {
        return appDatabase.recentsDao()
    }

    @Singleton
    @Provides
    fun provideSongsPlayedDao(appDatabase: AppDatabase) : SongsPlayedDao {
        return appDatabase.songsPlayedDao()
    }

    @Singleton
    @Provides
    fun provideAlbumsPlayedDao(appDatabase: AppDatabase) : AlbumsPlayedDao {
        return appDatabase.albumsPlayedDao()
    }

    @Singleton
    @Provides
    fun provideArtistsPlayedDao(appDatabase: AppDatabase) : ArtistsPlayedDao {
        return appDatabase.artistsPlayedDao()
    }

    @Singleton
    @Provides
    fun provideOtherStatsDao(appDatabase: AppDatabase) : OtherStatsDao {
        return appDatabase.otherStatsDao()
    }
}