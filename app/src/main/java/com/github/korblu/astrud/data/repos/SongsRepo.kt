package com.github.korblu.astrud.data.repos

import com.github.korblu.astrud.data.room.SongsDao
import com.github.korblu.astrud.data.room.SongsEntity

// Main thing to check if you want to use database that stores all the songs.
// todo Check if this actually works because the silly other guy (kakorry) didn't make a viewmodel yet so i can't freaking debug.
class SongsRepo(private val songsDao: SongsDao) {
    suspend fun addSong(song: SongsEntity): Long {
        return songsDao.insert(song)
    }

    suspend fun updateSong(song: SongsEntity) : Int {
        return songsDao.update(song)
    }

    suspend fun deleteSong(song: SongsEntity) : Int {
        return songsDao.delete(song)
    }

    suspend fun getSongById(id : Long) : SongsEntity {
        return songsDao.getSongById(id)
    }
}