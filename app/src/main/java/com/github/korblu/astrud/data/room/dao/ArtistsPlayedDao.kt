package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.korblu.astrud.data.common.PlayableForDao
import com.github.korblu.astrud.data.room.entity.RoomArtistsPlayed

@Dao
interface ArtistsPlayedDao : PlayableForDao<RoomArtistsPlayed>{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertOrUpdate(item: RoomArtistsPlayed)

    @Query("SELECT * FROM artists_played ORDER BY played DESC LIMIT :limit")
    override suspend fun getPlayedList(limit: Int) : List<RoomArtistsPlayed>

    // artits
    @Query("SELECT * FROM artists_played WHERE id = :id")
    override suspend fun getInfoFromId(id: Long) : RoomArtistsPlayed?
}