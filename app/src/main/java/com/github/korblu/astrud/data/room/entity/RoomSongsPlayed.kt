package com.github.korblu.astrud.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.korblu.astrud.data.common.Playable

@Entity(tableName = "songs_played")
data class RoomSongsPlayed(
    @PrimaryKey override val id: Long,
    override val played: Int = 0
): Playable<RoomSongsPlayed> {
    override fun withPlayedCount(count: Int) : RoomSongsPlayed{
        return copy(played = count)
    }
}
