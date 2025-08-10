package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.korblu.astrud.data.room.entity.RoomRecents
import com.github.korblu.astrud.data.room.room_models.LastPlayedAlbumsInfo
import com.github.korblu.astrud.data.room.room_models.LastPlayedArtistsInfo

@Dao
interface RecentsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(song: RoomRecents)

    @Query("SELECT * FROM recents ORDER BY lastPlayedTimestamp DESC LIMIT :limit")
    suspend fun getMostRecents(limit: Int) : List<RoomRecents>

    @Query("SELECT * FROM recents WHERE songUri = :songUri")
    suspend fun getSongById(songUri: String) : RoomRecents?

    @Query("""
        SELECT album, MAX(lastPlayedTimestamp) AS maxTimestamp
        FROM recents 
        WHERE lastPlayedTimestamp IS NOT NULL
        GROUP BY album
        ORDER BY maxTimestamp DESC
        LIMIT :limit
        """)
    suspend fun getLastPlayedAlbums(limit: Int) : List<LastPlayedAlbumsInfo>

    @Query("""
        SELECT artist, MAX(lastPlayedTimestamp) AS maxTimestamp
        FROM recents
        WHERE lastPlayedTimestamp IS NOT NULL
        GROUP BY artist
        ORDER BY maxTimestamp DESC
        LIMIT :limit
    """)
    suspend fun getLastPlayedArtists(limit: Int) : List<LastPlayedArtistsInfo>
}