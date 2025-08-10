package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.korblu.astrud.data.common.PlayableForDao
import com.github.korblu.astrud.data.room.entity.RoomSongsPlayed

@Dao
interface SongsPlayedDao : PlayableForDao<RoomSongsPlayed>{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertOrUpdate(item: RoomSongsPlayed)

    @Query("SELECT * FROM songs_played ORDER BY played DESC LIMIT :limit")
    override suspend fun getPlayedList(limit: Int) : List<RoomSongsPlayed>

    @Query("SELECT * FROM songs_played WHERE id = :id")
    override suspend fun getInfoFromId(id: Long) : RoomSongsPlayed?
}