package com.github.korblu.astrud.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.korblu.astrud.data.common.Playable

@Entity(tableName = "artists_played")
data class RoomArtistsPlayed(
    @PrimaryKey override val id: Long,
    override val played: Int = 0
): Playable<RoomArtistsPlayed> {
    override fun withPlayedCount(count: Int): RoomArtistsPlayed {
        return copy(played = count)
    }
}
