package com.github.korblu.astrud.data.media

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.github.korblu.astrud.data.room.Song
import com.github.korblu.astrud.ui.viewmodels.SongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// First code of bluu-chan guys, thank you AI for teaching
// me how to use this API because otherwise...(gulp) 05/25/25

class UserSongs(songViewModel: SongViewModel) {
    suspend fun getAllMetadata(context : Context, path: String) : List<Song> = withContext(
        Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val hasReadPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasReadPermission) {
            Log.d("UserSongs", "Permission to read audios not granted. Unable to retrieve songs")
            return@withContext emptyList()
        }

        val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DISC_NUMBER,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val normalizedPath = File(path).canonicalPath.trimEnd('/') + File.separator
        val selection = "${MediaStore.Audio.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("$normalizedPath%")
        val isMusicSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val notHiddenSelection = "${MediaStore.Audio.Media.IS_PENDING} = 0"
        val hasArtistSelection = "${MediaStore.Audio.Media.TITLE} IS NOT NULL"
        val hasAlbumArtSelection = "${MediaStore.Audio.Media.ALBUM_ID} IS NOT NULL"

        val finalSelection = "$selection AND $isMusicSelection AND $notHiddenSelection AND $hasAlbumArtSelection AND $hasArtistSelection"
        val finalSelectionArgs = selectionArgs

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                collection,
                projection,
                finalSelection,
                finalSelectionArgs,
                sortOrder
            )

            cursor?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val composerColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val trackColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val discColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISC_NUMBER)
                val genreColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while(it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown Title"
                    val artist = it.getString(artistColumn) ?: "Unknown Artist"
                    val composer = it.getString(composerColumn) ?: "Unknown Composer"
                    val album = it.getString(albumColumn) ?: "Unknown Album"
                    val genre = it.getString(genreColumn) ?: "Unknown Genre"
                    val duration = it.getLong(durationColumn)
                    val albumId = it.getLong(albumIdColumn)
                    val track = if(it.isNull(trackColumn)) {null} else {it.getInt(trackColumn)}
                    val discNumber = if(it.isNull(discColumn)) {null} else {it.getInt(discColumn)}
                    val year = if(it.isNull(yearColumn)) {null} else {it.getInt(yearColumn)}
                    val songUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val albumUri = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    )

                    songs.add(
                        Song(
                            id = null,
                            title = title,
                            artist = artist,
                            composer = composer,
                            album = album,
                            track = track,
                            discNumber = discNumber,
                            genre = genre,
                            duration = duration.toInt(),
                            year = year,
                            uri = songUri.toString(),
                            coverUri = albumUri.toString(),
                        )
                    )
                }
            }
        } catch(e: Exception) {
            Log.e("UserSongs", "Error querying MediaStore: ${e.message}")
        } finally {
            cursor?.close()
        }
        return@withContext songs
    }

    suspend fun getCollectionSize(
        context: Context,
        album : Boolean?,
        year : Boolean?,
        artist : Boolean?
    ) : Int? = withContext(Dispatchers.IO) {
        val readPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else{
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if (!readPermission) {
            Log.d("UserSongs", "No permission for getRandomSong().")
            return@withContext null
        }

        val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR
        )

        val isMusicSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val notHiddenSelection = "${MediaStore.Audio.Media.IS_PENDING} = 0"

        var finalAlbumSelection = ""
        var finalArtistSelection = ""
        var finalYearSelection = ""

        if(album == true) {
            val hasAlbumSelection = "${MediaStore.Audio.Media.ALBUM} IS NOT NULL"
            finalAlbumSelection = " AND $hasAlbumSelection"
        }
        if(artist == true) {
            val hasArtistSelection = "${MediaStore.Audio.Media.ARTIST} IS NOT NULL"
            finalArtistSelection = " AND $hasArtistSelection"
        }
        if(year == true) {
            val hasYearSelection = "${MediaStore.Audio.Media.YEAR} IS NOT NULL"
            finalYearSelection = " AND $hasYearSelection"
        }


        val finalSelection = "$isMusicSelection AND ${notHiddenSelection}${finalAlbumSelection}${finalArtistSelection}${finalYearSelection}"
        val finalSelectionArgs : Array<String>? = null

        val sortOrder = ""

        var cursor : Cursor? = null
        var counter = 0
        try {
            cursor = context.contentResolver.query(
                collection,
                projection,
                finalSelection,
                finalSelectionArgs,
                sortOrder
            )
            cursor?.let {
                while(it.moveToNext()) {
                    counter++
                }
            }

            return@withContext counter
        } catch(e: Exception) {
            Log.d("UserSongs", "Error in cursor query in getCollectionSize(): $e")
        } finally {
            cursor?.close()
        }
    }

    suspend fun getRandomSong(context: Context, yearOnly: Boolean = false): Map<String?, String?>? = withContext(
        Dispatchers.IO
    ) {
        val readPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else{
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if (!readPermission) {
            Log.d("UserSongs", "No permission for getRandomSong().")
            return@withContext null
        }

        val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val isMusicSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val hasTitleSelection = "${MediaStore.Audio.Media.TITLE} IS NOT NULL"
        val notHiddenSelection = "${MediaStore.Audio.Media.IS_PENDING} = 0"
        val hasAlbumSelection = "${MediaStore.Audio.Media.ALBUM} IS NOT NULL"
        val hasYearSelection = "${MediaStore.Audio.Media.YEAR} IS NOT NULL"

        val finalSelection = if (yearOnly) {
            "$isMusicSelection AND $notHiddenSelection AND $hasAlbumSelection AND $hasTitleSelection AND $hasYearSelection"
        } else {
            "$isMusicSelection AND $notHiddenSelection AND $hasTitleSelection AND $hasAlbumSelection"
        }
        val finalSelectionArgs : Array<String>? = null

        var cursor : Cursor? = null
        try {
            cursor = context.contentResolver.query(
                collection,
                projection,
                finalSelection,
                finalSelectionArgs,
                null
            )

            cursor.let {
                if (it != null && it.count > 0) {
                    val randomPosition = (0 until it.count).random()
                    it.moveToPosition(randomPosition)

                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

                    val title : String? = it.getString(titleColumn)
                    val artist : String? = it.getString(artistColumn)
                    val id : String = it.getString(idColumn)
                    val album : String = it.getString(albumColumn)
                    val albumId = it.getLong(albumIdColumn)
                    val albumArtUri : String? = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    ).toString()
                    val songUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id.toLong()
                    ).toString()

                    return@withContext mapOf(
                        "title" to title,
                        "uri" to songUri,
                        "artist" to artist,
                        "album" to album,
                        "albumArtUri" to albumArtUri
                    )
                }
            }
        } catch(e: Exception) {
            Log.d("UserSongs", "Error in cursor query in getRandomSong(): $e")
        } finally {
            cursor?.close()
        }

        return@withContext null
    }

    class SongIterator(val context: Context) {
        /** IMPORTANT: DON'T FORGET TO CLOSE THE CURSOR WHEN ACTIVITY IS DESTROYED */
        var cursor : Cursor? = null

        val readPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else{
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        suspend fun setCursor(
            album : Boolean?,
            year : Boolean?,
            artist : Boolean?
        ) = withContext(Dispatchers.IO){

            if(!readPermission) {
                Log.d("SongIterator", "No permission for setCursor()")
                return@withContext null
            }

            val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR
            )

            val isMusicSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val notHiddenSelection = "${MediaStore.Audio.Media.IS_PENDING} = 0"

            var finalAlbumSelection = ""
            var finalArtistSelection = ""
            var finalYearSelection = ""

            if(album == true) {
                val hasAlbumSelection = "${MediaStore.Audio.Media.ALBUM} IS NOT NULL"
                finalAlbumSelection = " AND $hasAlbumSelection"
            }
            if(artist == true) {
                val hasArtistSelection = "${MediaStore.Audio.Media.ARTIST} IS NOT NULL"
                finalArtistSelection = " AND $hasArtistSelection"
            }
            if(year == true) {
                val hasYearSelection = "${MediaStore.Audio.Media.YEAR} IS NOT NULL"
                finalYearSelection = " AND $hasYearSelection"
            }


            val finalSelection = "$isMusicSelection AND $notHiddenSelection$finalAlbumSelection$finalArtistSelection$finalYearSelection"
            val finalSelectionArgs : Array<String>? = null

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            try {
                cursor = context.contentResolver.query(
                    collection,
                    projection,
                    finalSelection,
                    finalSelectionArgs,
                    sortOrder
                )
            } catch(e: Exception) {
                Log.d("SongIterator", "Failed to set up cursor query in setCursor(): $e")
            }
        }

        suspend fun getNextSong() : Map<String, String?>? = withContext(Dispatchers.IO){
            if(!readPermission) {
                Log.d("SongIterator", "No permission for getNextSong()")
                return@withContext null
            }

            try {
                cursor?.let {
                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                    if(it.moveToNext()) {
                        val title = it.getString(titleColumn) ?: "Unknown Title"
                        val album = it.getString(albumColumn) ?: "Unknown Album"
                        val artist = it.getString(artistColumn) ?: "Unknown Artist"
                        val year = if(it.isNull(yearColumn)) {null} else {it.getInt(yearColumn)}

                        return@withContext mapOf(
                            "title" to title,
                            "album" to album,
                            "artist" to artist,
                            "year" to year.toString()
                        )
                    }
                }
            } catch(e: Exception) {
                Log.d("SongIterator", "Failure in getNextSong(): $e")
            }
            return@withContext null
        }

        fun closeCursor() {
            cursor?.close()
            cursor = null
            Log.d("SongIterator", "Cursor closed.")
        }
    }

    
}