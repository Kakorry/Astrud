package com.github.korblu.astrud.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recents")
data class RoomRecents(
    @PrimaryKey val songUri: String,
    val lastPlayedTimestamp: Long,
    val album: String?,
    val artist: String?
)