package com.github.korblu.astrud.data.repos

import com.github.korblu.astrud.data.room.SongsDao
import com.github.korblu.astrud.data.room.RoomSong

/**
 * For using the "Songs" Room Database.
 */
class SongsRepo(private val songsDao: SongsDao) {
    suspend fun addSong(roomSong: List<RoomSong>): List<Long> {
        return songsDao.insert(roomSong)
    }

    suspend fun updateSong(roomSong: RoomSong) : Int {
        return songsDao.update(roomSong)
    }

    suspend fun deleteSong(roomSong: RoomSong) : Int {
        return songsDao.delete(roomSong)
    }

    suspend fun getSongById(id : Long) : RoomSong? {
        return songsDao.getSongById(id)
    }

    suspend fun getAllSongs() : List<RoomSong> {
        return songsDao.getAllSongs()
    }

    suspend fun getSongsByAlbum(albumName: String?) : List<RoomSong> {
        return songsDao.getSongsByAlbum(albumName)
    }

    suspend fun getSongsByArtist(artist: String?) : List<RoomSong> {
        return songsDao.getSongsByArtist(artist)
    }

    suspend fun getSongsByGenre(genre: String?) : List<RoomSong> {
        return songsDao.getSongsByGenre(genre)
    }
}