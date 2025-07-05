package com.github.korblu.astrud.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongsEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String?,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val duration: Long,
    val uri: String,
    val embPicPath: String?
)