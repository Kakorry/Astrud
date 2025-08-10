package com.github.korblu.astrud.data.repos

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.github.korblu.astrud.data.common.Playable
import com.github.korblu.astrud.data.common.PlayableForDao
import com.github.korblu.astrud.data.room.dao.AlbumsPlayedDao
import com.github.korblu.astrud.data.room.dao.ArtistsPlayedDao
import com.github.korblu.astrud.data.room.dao.OtherStatsDao
import com.github.korblu.astrud.data.room.dao.SongsPlayedDao
import com.github.korblu.astrud.data.room.entity.RoomAlbumsPlayed
import com.github.korblu.astrud.data.room.entity.RoomArtistsPlayed
import com.github.korblu.astrud.data.room.entity.RoomOtherStats
import com.github.korblu.astrud.data.room.entity.RoomSongsPlayed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserStatsRepo @Inject constructor(
    private val songsPlayedDao: SongsPlayedDao,
    private val albumsPlayedDao: AlbumsPlayedDao,
    private val artistsPlayedDao: ArtistsPlayedDao,
    private val otherStatsDao: OtherStatsDao,
    val context: Context
) {
    private val tag = "UserStatsRepo"

    private suspend fun isIdValid(id: Long, type: String): Boolean = withContext(
        Dispatchers.IO) {
        val uri: Uri = when (type) {
            "song" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            "album" -> MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            "artist" -> MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
            else -> {
                Log.w(tag, "isIdValid type is not valid.")
                return@withContext false
            }
        }
        val selection : String = when (type) {
            "song" -> "${MediaStore.Audio.Media._ID} = ?"
            "album" -> "${MediaStore.Audio.Albums._ID} = ?"
            "artist" -> "${MediaStore.Audio.Artists._ID} = ?"
            else -> {
                return@withContext false
            }
        }
        val selectionArgs = arrayOf(id.toString())

        context.contentResolver.query(
            uri,
            null,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return@withContext cursor.count > 0
        }
        return@withContext false
    }

    private suspend fun <T: Playable<T>> insertPlayableItem(item: T, dao: PlayableForDao<T>, type: String) {
        if(isIdValid(item.id, type)) {
            val timesPlayed = dao.getInfoFromId(item.id)?.played ?: 0
            dao.insertOrUpdate(item.withPlayedCount(timesPlayed + 1))
        } else {
            Log.w(tag, """"$type" id is not valid.""")
        }
    }

    private suspend fun <T: Playable<T>> getPlayedList(dao: PlayableForDao<T>, limit: Int = -1) : List<T> {
        return dao.getPlayedList(limit)
    }

    suspend fun incrementSongsPlayed(song: RoomSongsPlayed) {
        insertPlayableItem(song, songsPlayedDao, "song")
    }

    suspend fun incrementAlbumsPlayed(album: RoomAlbumsPlayed) {
        insertPlayableItem(album, albumsPlayedDao, "album")
    }

    suspend fun incrementArtistsPlayed(artist: RoomArtistsPlayed) {
        insertPlayableItem(artist, artistsPlayedDao, "artist")
    }

    suspend fun getPlayedSongs() : List<RoomSongsPlayed> {
        return getPlayedList(songsPlayedDao)
    }

    suspend fun getPlayedAlbums() : List<RoomAlbumsPlayed> {
        return getPlayedList(albumsPlayedDao)
    }

    suspend fun getPlayedArtists() : List<RoomArtistsPlayed> {
        return getPlayedList(artistsPlayedDao)
    }

    suspend fun updateOtherStats(stats: RoomOtherStats) {
        otherStatsDao.insertOrUpdate(stats)
    }

    suspend fun updatePlayedTotalStat(playedTotal: Int) {
        otherStatsDao.updatePlayedTotal(playedTotal)
    }

    suspend fun updateHoursSpent(hoursSpent: Long) {
        otherStatsDao.updateHoursSpent(hoursSpent)
    }

    suspend fun getOtherStats() : RoomOtherStats? {
        return otherStatsDao.getOtherStats()
    }
}