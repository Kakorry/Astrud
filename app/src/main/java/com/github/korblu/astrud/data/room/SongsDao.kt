package com.github.korblu.astrud.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongsDao {
    @Insert
    suspend fun insert(song: List<Song>) : List<Long>

    @Update
    suspend fun update(song: Song) : Int

    @Delete
    suspend fun delete(song: Song) : Int

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long) : Song?

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs() : List<Song>

    @Query("SELECT * FROM songs WHERE album = :albumName")
    suspend fun getSongsByAlbum(albumName: String?) : List<Song>

    @Query("SELECT * FROM songs WHERE artist = :artist")
    suspend fun getSongsByArtist(artist: String?) : List<Song>

    @Query("SELECT * FROM songs WHERE genre = :genre")
    suspend fun getSongsByGenre(genre: String?) : List<Song>
}