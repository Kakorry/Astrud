package com.github.korblu.astrud.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.korblu.astrud.data.room.entity.RoomRecents
import com.github.korblu.astrud.data.room.pojo.LastPlayedAlbumsInfo
import com.github.korblu.astrud.data.room.pojo.LastPlayedArtistsInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomRecentsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(song: RoomRecents)

    @Query("SELECT * FROM recents ORDER BY lastPlayedTimestamp DESC LIMIT :limit")
    fun getMostRecentsFlow(limit: Int) : Flow<List<RoomRecents>>

    @Query("SELECT * FROM recents ORDER BY lastPlayedTimestamp DESC LIMIT :limit")
    suspend fun getMostRecents(limit: Int) : List<RoomRecents>

    @Query("SELECT * FROM recents WHERE songId = :songId")
    suspend fun getSongById(songId: String) : RoomRecents?

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