package com.github.korblu.astrud.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongsDao {
    @Insert
    suspend fun insert(song: SongsEntity) : Long

    @Update
    suspend fun update(song: SongsEntity) : Int

    @Delete
    suspend fun delete(song: SongsEntity) : Int

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long) : SongsEntity
}