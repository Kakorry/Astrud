package com.github.korblu.astrud.data.repos

import android.util.Log
import com.github.korblu.astrud.data.room.dao.RoomRecentsDao
import com.github.korblu.astrud.data.room.entity.RoomRecents
import com.github.korblu.astrud.data.room.pojo.LastPlayedAlbumsInfo
import com.github.korblu.astrud.data.room.pojo.LastPlayedArtistsInfo
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoomRecentsRepo (private val recentsDao: RoomRecentsDao){
    private val tag = "RoomRecentsRepo"

    suspend fun insertOrUpdate(song: RoomRecents) {
        recentsDao.insertOrUpdate(song)
    }

    fun getMostRecentsFlow(limit: Int) : Flow<List<RoomRecents>> {
        return recentsDao.getMostRecentsFlow(limit)
    }

    suspend fun getMostRecents(limit: Int) : List<RoomRecents> {
        return recentsDao.getMostRecents(limit)
    }

    suspend fun getSongById(id: String) : RoomRecents? {
        val value = recentsDao.getSongById(id)

        if(value != null) return value else {
            Log.w(tag, "getSongById returned null.")
            return null
        }
    }

    suspend fun getLastPlayedAlbums(limit: Int, dateFormat: Boolean = false) : List<LastPlayedAlbumsInfo> {
        val value = recentsDao.getLastPlayedAlbums(limit)

        if(dateFormat) {
            val newList = mutableListOf<LastPlayedAlbumsInfo>()

            for(i in value) {
                newList.add(i.copy(maxTimestamp = timestampToDate(i.maxTimestamp)))
            }
            return newList.toList()
        }
        return value
    }

    suspend fun getLastPlayedArtists(limit: Int, dateFormat: Boolean = false) : List<LastPlayedArtistsInfo> {
        val value = recentsDao.getLastPlayedArtists(limit)

        if(dateFormat) {
            val newList = mutableListOf<LastPlayedArtistsInfo>()

            for(i in value) {
                newList.add(i.copy(maxTimestamp = timestampToDate(i.maxTimestamp)))
            }
            return newList.toList()
        }
        return value
    }

    private fun timestampToDate(timestamp: String) : String {

        val date = Date(timestamp.toLong())
        val formatter = SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault())

        return formatter.format(date)
    }
}