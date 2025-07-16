package com.github.korblu.astrud.data.repos

import android.util.Log
import com.github.korblu.astrud.data.room.SongsDao
import com.github.korblu.astrud.data.room.Song

// Main thing to check if you want to use database that stores all the songs.
class SongsRepo(private val songsDao: SongsDao) {
    suspend fun addSong(song: List<Song>): List<Long> {
        return songsDao.insert(song)
    }

    suspend fun updateSong(song: Song) : Int {
        return songsDao.update(song)
    }

    suspend fun deleteSong(song: Song) : Int {
        return songsDao.delete(song)
    }

    suspend fun getSongById(id : Long) : Song? {
        return songsDao.getSongById(id)
    }

    suspend fun getAllSongs() : List<Song> {
        return songsDao.getAllSongs()
    }

    suspend fun getSongsByAlbum(albumName: String?) : List<Song> {
        return songsDao.getSongsByAlbum(albumName)
    }

    suspend fun getSongsByArtist(artist: String?) : List<Song> {
        return songsDao.getSongsByArtist(artist)
    }

    suspend fun getSongsByGenre(genre: String?) : List<Song> {
        return songsDao.getSongsByGenre(genre)
    }
}