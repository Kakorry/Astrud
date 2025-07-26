package com.github.korblu.astrud.data.repos

import android.text.Layout
import androidx.datastore.core.DataStore
import com.github.korblu.astrud.data.datastore.LayoutSongsList
import com.github.korblu.astrud.data.datastore.LayoutSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LayoutSongsRepo @Inject constructor(
    private val layoutSongsDataStore : DataStore<LayoutSongsList>
){
    suspend fun toLayoutSongObject(songsList : List<Map<String, String?>>) : List<LayoutSong> = withContext(
        Dispatchers.Default) {
        val newList = mutableListOf<LayoutSong>()
        var index = -1

        for(i in songsList) {
            index++

            val song = LayoutSong.newBuilder()
                .setId(index.toString())
                .setTitle(
                    if(i.containsKey("title")) {
                        i["title"]
                    } else {
                        ""
                    })
                .setArtist(
                    if(i.containsKey("artist")) {
                        i["artist"]
                    } else {
                        ""
                    }
                )
                .setAlbum(
                    if(i.containsKey("album")) {
                        i["album"]
                    } else {
                        ""
                    }
                )
                .setUri(
                    if(i.containsKey("uri")) {
                        i["uri"]
                    } else {
                        ""
                    }
                )
                .setAlbumArtUri(
                    if(i.containsKey("albumArtUri")) {
                        i["albumArtUri"]
                    } else {
                        ""
                    }
                )
                .build()

            newList.add(song)
        }
        return@withContext newList.toList()
    }

    suspend fun setSongs(songsList : List<LayoutSong>) {
        layoutSongsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearSongs()
                .addAllSongs(songsList)
                .build()
        }
    }

    suspend fun clearSongs() {
        layoutSongsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearSongs()
                .build()
        }
    }
}