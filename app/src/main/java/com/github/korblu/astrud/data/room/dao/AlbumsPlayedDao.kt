package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.korblu.astrud.data.common.PlayableForDao
import com.github.korblu.astrud.data.room.entity.RoomAlbumsPlayed

@Dao
interface AlbumsPlayedDao : PlayableForDao<RoomAlbumsPlayed>{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertOrUpdate(item: RoomAlbumsPlayed)

    @Query("SELECT * FROM albums_played ORDER BY played DESC LIMIT :limit")
    override suspend fun getPlayedList(limit: Int) : List<RoomAlbumsPlayed>

    @Query("SELECT * FROM albums_played WHERE id = :id")
    override suspend fun getInfoFromId(id: Long) : RoomAlbumsPlayed?
}