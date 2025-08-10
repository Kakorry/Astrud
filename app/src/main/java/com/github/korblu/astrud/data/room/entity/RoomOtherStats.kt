package com.github.korblu.astrud.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "other_stats")
data class RoomOtherStats(
    @PrimaryKey val id: Int = 1,
    val playedTotal: Int,
    val hoursSpent: Long
)
