package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.korblu.astrud.data.room.entity.RoomOtherStats

@Dao
interface OtherStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: RoomOtherStats)

    @Query("UPDATE other_stats SET playedTotal = :playedTotal WHERE id = 1")
    suspend fun updatePlayedTotal(playedTotal: Int)

    @Query("UPDATE other_stats SET hoursSpent = :hoursSpent WHERE id = 1")
    suspend fun updateHoursSpent(hoursSpent: Long)

    @Query("SELECT * FROM other_stats")
    suspend fun getOtherStats() : RoomOtherStats?
}
