package com.github.korblu.astrud.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongsDao {
    @Insert
    suspend fun insert(roomSong: List<RoomSong>) : List<Long>

    @Update
    suspend fun update(roomSong: RoomSong) : Int

    @Delete
    suspend fun delete(roomSong: RoomSong) : Int

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long) : RoomSong?

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs() : List<RoomSong>

    @Query("SELECT * FROM songs WHERE album = :albumName")
    suspend fun getSongsByAlbum(albumName: String?) : List<RoomSong>

    @Query("SELECT * FROM songs WHERE artist = :artist")
    suspend fun getSongsByArtist(artist: String?) : List<RoomSong>

    @Query("SELECT * FROM songs WHERE genre = :genre")
    suspend fun getSongsByGenre(genre: String?) : List<RoomSong>
}