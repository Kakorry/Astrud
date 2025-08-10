package com.github.korblu.astrud.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.korblu.astrud.data.common.Playable

@Entity(tableName = "albums_played")
data class RoomAlbumsPlayed(
    @PrimaryKey override val id: Long,
    override val played: Int = 0
): Playable<RoomAlbumsPlayed> {
    override fun withPlayedCount(count: Int): RoomAlbumsPlayed {
        return copy(played = count)
    }
}
