package com.github.korblu.astrud.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.korblu.astrud.data.datastore.LayoutSongsList
import com.github.korblu.astrud.data.datastore.LayoutSongsListSerializer
import com.github.korblu.astrud.data.datastore.UserPreferences
import com.github.korblu.astrud.data.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

private val Context.layoutSongsListDataStore: DataStore<LayoutSongsList> by dataStore(
    fileName = "layout_songs_list.pb",
    serializer = LayoutSongsListSerializer
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context
    ) : DataStore<UserPreferences> {
        return context.userPreferencesDataStore
    }

    fun providesLayoutSongsListDataStore(
        @ApplicationContext context: Context
    ) : DataStore<LayoutSongsList> {
        return context.layoutSongsListDataStore
    }
}