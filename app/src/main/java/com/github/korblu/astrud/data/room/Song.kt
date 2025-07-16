package com.github.korblu.astrud.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song (
    @PrimaryKey(autoGenerate = true) val id: Long? = 0L,
    val title: String?,
    val artist: String?,
    val composer: String?,
    val album: String?,
    val track: Int?,
    val discNumber: Int?,
    val genre: String?,
    val duration: Int?,
    val year: Int?,
    val uri: String,
    val coverUri: String?
)