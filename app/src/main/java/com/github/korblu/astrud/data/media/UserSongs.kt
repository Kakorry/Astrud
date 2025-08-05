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
import com.github.korblu.astrud.data.media.media_models.SongMetadataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// First code of bluu-chan guys, thank you AI for teaching
// me how to use this API because otherwise...(gulp) 05/25/25

class UserSongs(val context: Context) {
    /** IMPORTANT: DON'T FORGET TO CLOSE THE CURSOR WHEN ACTIVITY IS DESTROYED */
    private var cursor : Cursor? = null
    private var getNextSongPosition = -1

    /**
     * ##### Sets the main cursor for the functions. Remember to close it on activity destruction to prevent memory leaks.
     *
     * @param projection Array<String> of metadata you want to use with the cursor
     * @param mustHave Array<String> of "must have"s when showing the song. e.g., when you want the
     * cursor only to select songs that have a defined album.
     * All possible inputs: "ALBUM", "ARTIST", "YEAR"
     * @param sort String of the sort order you want to use. Default is "ASC". Use "DSC" for
     * descending and RANDOM for random order.
     *
     *
     * ##### By the way, here is a default projection that gets the most important metadata:
     * ```
     * arrayOf(
     *     MediaStore.Audio.Media.TITLE,
     *     MediaStore.Audio.Media.ARTIST,
     *     MediaStore.Audio.Media.ALBUM,
     *     MediaStore.Audio.Media._ID,
     *     MediaStore.Audio.Media.ALBUM_ID,
     *     MediaStore.Audio.Media.DURATION,
     *     MediaStore.Audio.Media.YEAR
     * )
     * ```
     *
     * @author bluu
     * @see closeCursor */
    suspend fun setCursor(
        projection : Array<String>,
        specificUri : Uri? = null,
        mustHave : Array<String>? = null,
        sort : String = "ASC"
    ) = withContext(Dispatchers.IO){
        val readPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else{
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if(!readPermission) {
            Log.e("Permission Error", "No permission for setCursor()")
            return@withContext
        }

        cursor?.close()


        val collection = specificUri ?: MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val finalSelection : String?
        val finalSelectionArgs : Array<String>? = null
        val sortOrder : String?

        if(specificUri == null) {
            val selectionConditions = mutableListOf<String>()
            selectionConditions.add("${MediaStore.Audio.Media.IS_MUSIC} != 0")
            selectionConditions.add("${MediaStore.Audio.Media.IS_PENDING} = 0")

            if (!mustHave.isNullOrEmpty()) {
                if (mustHave.contains("ALBUM")) {
                    selectionConditions.add("${MediaStore.Audio.Media.ALBUM} IS NOT NULL")
                }
                if (mustHave.contains("ARTIST")) {
                    selectionConditions.add("${MediaStore.Audio.Media.ARTIST} IS NOT NULL")
                }
                if (mustHave.contains("YEAR")) {
                    selectionConditions.add("${MediaStore.Audio.Media.YEAR} IS NOT NULL")
                }
            }

            finalSelection = selectionConditions.joinToString(" AND ")

            sortOrder = if (sort == "RANDOM") {
                "RANDOM()"
            } else {
                "${MediaStore.Audio.Media.TITLE} $sort"
            }
        } else {
            finalSelection = null
            sortOrder = null
        }

        try {
            cursor = context.contentResolver.query(
                collection,
                projection,
                finalSelection,
                finalSelectionArgs,
                sortOrder
            )
        } catch(e: Exception) {
            Log.e("Error", "Failed to set up cursor query in setCursor(): $e")
            cursor = null
        }
    }

    fun closeCursor() {
        cursor?.close()
        cursor = null
        Log.d("UserSongs", "Cursor closed.")
    }

    suspend fun getRandomSong(): SongMetadataModel? = withContext(Dispatchers.IO) {
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
            Log.e("Permission Error", "No permission for getRandomSong().")
            return@withContext null
        }

        if(cursor == null) {
            Log.w("UserSongs", "Cursor is null")
            return@withContext null
        }

        try{
            cursor?.let {
                if (it.count > 0) {
                    val randomPosition = (0 until it.count).random()
                    it.moveToPosition(randomPosition)

                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

                    val title : String? = it.getString(titleColumn)
                    val artist: String? = it.getString(artistColumn)
                    val id : Long = it.getLong(idColumn)
                    val album : String = it.getString(albumColumn)
                    val albumId : Long = it.getLong(albumIdColumn)
                    val albumArtUri : String? = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    ).toString()
                    val songUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    return@withContext SongMetadataModel(
                        title,
                        artist,
                        album,
                        songUri,
                        albumArtUri,
                        null,
                        null
                    )
                }
            }
        } catch(e: Exception) {
            Log.e("Error", "Error in cursor query in getRandomSong(): $e")
        }

        cursor?.moveToPosition(-1)
        return@withContext null
    }

    /**
     * Get the next song from the current open [cursor] (or the first song if not called before)
     * and returns a Map containing its title, album, artist, and year.
     *
     * @param check Tells if you want the function to check permission and cursor state on call.
     * Default is true.
     * @return Map<String, String?>?
     * @see setCursor*/
    suspend fun getNextSong(check: Boolean = true) : SongMetadataModel? = withContext(Dispatchers.IO){
        if(check) {
            val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

            if (!readPermission) {
                Log.e("Permission Error", "No permission for getNextSong()")
                return@withContext null
            }

            if (cursor == null) {
                Log.w("UserSongs", "Cursor is null")
                return@withContext null
            }
        }

        cursor?.moveToPosition(getNextSongPosition)
        try {
            cursor?.let {
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                if(it.moveToNext()) {
                    getNextSongPosition++

                    val title = it.getString(titleColumn) ?: "Unknown Title"
                    val album = it.getString(albumColumn) ?: "Unknown Album"
                    val artist = it.getString(artistColumn) ?: "Unknown Artist"
                    val id : String = it.getString(idColumn)
                    val albumId = it.getLong(albumIdColumn)
                    val songUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id.toLong()
                    ).toString()
                    val albumArtUri : String? = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    ).toString()

                    val year = if(it.isNull(yearColumn)) {null} else {it.getLong(yearColumn)}

                    return@withContext SongMetadataModel(
                        title,
                        artist,
                        album,
                        songUri,
                        albumArtUri,
                        null,
                        year
                    )
                }
            }
        } catch(e: Exception) {
            Log.d("Error", "Failure in getNextSong(): $e")
        }
        return@withContext null
    }

    suspend fun getCollectionSize() : Int? = withContext(Dispatchers.IO) {
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
            Log.e("Permission Error", "No permission for getRandomSong().")
            return@withContext null
        }

        if(cursor == null) {
            Log.w("UserSongs", "Cursor is null")
            return@withContext null
        }

        try {
            return@withContext cursor?.count
        } catch(e: Exception) {
            Log.e("Error", "Error in cursor query in getCollectionSize(): $e")
            return@withContext null
        }
    }

    suspend fun searchSong(input : String) : List<SongMetadataModel>? = withContext(
        Dispatchers.IO){
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
            Log.e("Permission Error", "No permission for getRandomSong().")
            return@withContext null
        }

        getNextSongPosition = -1

        var iteratorNumber = 0

        val inputFinal = input.lowercase().trim()
        val length = inputFinal.length

        val searchResult : MutableList<SongMetadataModel> = mutableListOf()

        while(iteratorNumber <= 10) {
            iteratorNumber++

            val songMap = getNextSong(false)
            if(songMap != null && songMap.title != null){
                val songLetters = songMap.title
                    .slice(0..length)
                    .lowercase()
                    .trim()

                if(inputFinal == songLetters) {
                    searchResult.add(songMap)
                }
            }
        }

        return@withContext searchResult.toList()
    }

    /** Lists all of the uniques "characteristics". e.g., list all the unique albums.
     *
     * DISCLAIMER: It creates its own local cursor, so no need to set one beforehand.
     *
     * @param toList String of what you want to list. The possibilities are:
     *
     * "ALBUM" "ARTIST"
     *
     * @return List<String> of all the uniques [toList]
     * */
    suspend fun listAllDesired(toList: String) : List<String>? = withContext(Dispatchers.IO) {
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        if (!readPermission) {
            Log.e("Permission Error", "No permission for listAllDesired()")
            return@withContext null
        }

        var localCursor: Cursor? = null
        val desiredList : MutableList<String> = mutableListOf()

        try {
            val toListFinal = when (toList) {
                "ALBUM" -> {
                    MediaStore.Audio.Media.ALBUM
                }

                "ARTIST" -> {
                    MediaStore.Audio.Media.ARTIST
                }

                else -> {
                    Log.w("UserSongs", "Parameter 'toList' is not valid.")
                    return@withContext null
                }
            }


            val collection: Uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

            val projection = arrayOf(
                MediaStore.Audio.Media.ALBUM
            )

            val selectionConditions = mutableListOf<String>()
            selectionConditions.add("${MediaStore.Audio.Media.IS_MUSIC} != 0")
            selectionConditions.add("${MediaStore.Audio.Media.IS_PENDING} = 0")
            selectionConditions.add("$toListFinal IS NOT NULL")

            val finalSelection = selectionConditions.joinToString(" AND ")
            val sortOrder = "$toListFinal ASC"

            localCursor = context.contentResolver.query(
                collection,
                projection,
                finalSelection,
                null,
                "$sortOrder) GROUP BY $toListFinal"
            )

            localCursor?.let {
                val desiredColumnIndex = it.getColumnIndex(toListFinal)
                if (desiredColumnIndex == -1) {
                    Log.e("UserSongs", "MediaStore.Audio.Media.ALBUM column not found.")
                    return@withContext emptyList()
                }
                while(it.moveToNext()) {
                    val desiredName = it.getString(desiredColumnIndex)
                    if(!desiredName.isNullOrBlank()) {
                        desiredList.add(desiredName)
                    }
                }
            }
        } catch(e: Exception) {
            Log.e("Error", "Error in UserSongs.listAllDesired(): $e")
        } finally {
            localCursor?.close()
        }
        return@withContext null
    }

    suspend fun getMetadataByUri(uri : Uri) : SongMetadataModel? = withContext(Dispatchers.IO){
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        if (!readPermission) {
            Log.e("Permission Error", "No permission for getMetadataByUri()")
            return@withContext null
        }

        var localCursor : Cursor? = null

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR
        )

        try{
            localCursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )

            localCursor?.let {
                if(it.moveToFirst()) {
                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                    val title : String? = it.getString(titleColumn)
                    val artist : String? = it.getString(artistColumn)
                    val album : String? = it.getString(albumColumn)
                    val albumId : Long = it.getLong(albumIdColumn)
                    val albumArtUri : String? = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    ).toString()
                    val duration: Long? = if (it.isNull(durationColumn)) null else it.getLong(durationColumn)
                    val year = if(it.isNull(yearColumn)) null else it.getLong(yearColumn)

                    return@withContext SongMetadataModel(
                        title,
                        artist,
                        album,
                        uri.toString(),
                        albumArtUri,
                        duration,
                        year
                    )
                } else{
                    Log.w("UserSongs", "No metadata found for the song at Uri: $uri")
                }
            }
        } catch(e: Exception) {
            Log.e("Error", "Error in UserSongs.getMetadataByUri(): ${e.message}")
        } finally {
            localCursor?.close()
        }
        return@withContext null
    }
}