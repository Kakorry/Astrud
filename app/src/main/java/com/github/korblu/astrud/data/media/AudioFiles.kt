package com.github.korblu.astrud.data.media

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import kotlin.random.Random

// First code of bluu-chan guys, thank you AI for teaching
// me how to use this API because otherwise...(gulp) 05/25/25


class AudioFiles(private val activity : Activity) {
    val onResult = { uri: Uri? ->
        this.getDirectory(
            requestCode = 1,
            if (uri != null) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            Intent().apply { data = uri })
    }

    val contractDocumentTree = ActivityResultContracts.OpenDocumentTree()
    // val contractOpenDocument = ActivityResultContracts.OpenDocument()

    lateinit var songFolder: Uri

    fun getDirectory(requestCode : Int, resultCode : Int, data : Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { treeURI ->
                activity.contentResolver.takePersistableUriPermission(
                    treeURI,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                songFolder = treeURI
            }
        }
    }

    fun getAllDefaultDirSongsMD(context: Context) : MutableList<Map<String, String?>> {
        val directoryFile = DocumentFile.fromTreeUri(context, songFolder)
        var songs : Array<DocumentFile>?
        val listOfSongMaps : MutableList<Map<String, String?>> = mutableListOf()

        try {
            songs = directoryFile?.listFiles()
        } catch(e : Exception) {
            songs = null
            Log.e("AFError", "Failed to get songs from default directory: $e")
        }

        if (songs != null) {
            for(song in songs) {
                listOfSongMaps.add(this.getMetadata(context, song.uri))
            }
        }
        return listOfSongMaps
    }

    // bluu-chan's back after 2 months or so and i'll start commenting my codes more.
    // this function just gets a song's metadata like duration, artist, year, etc. 03/07/2025
    fun getMetadata(context : Context, uri : Uri) : Map<String, String?> {
        val metadataRetriever = MediaMetadataRetriever()
        val metadata = mutableMapOf<String, String?>()

        try {
            metadataRetriever.setDataSource(context, uri)

            metadata["title"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            metadata["artist"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            metadata["album"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            metadata["genre"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
            metadata["duration"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            metadata["year"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)

            val embeddedPic = metadataRetriever.embeddedPicture
            if (embeddedPic != null) {
                metadata["hasEmbeddedPic"] = "true"
            } else {
                metadata["hasEmbeddedPic"] = "false"
            }
        } catch (e: Exception) {
            Log.e("Mp3MetadataExtractor", "Error extracting metadata: ${e.message}")
        } finally {
            try {
                metadataRetriever.release()
            } catch (e: Exception) {
                Log.e(
                    "Mp3MetadataExtractor",
                    "Error releasing MediaMetadataRetriever: ${e.message}"
                )
            }
        }
        return metadata
    }
    fun getEmbeddedPic(context : Context) : Bitmap? {
        val directoryFile = DocumentFile.fromTreeUri(context, songFolder)
        val songs = directoryFile?.listFiles()
        val songList = songs?.size
        val randomSong = Random.nextInt(0, songList ?: 0)
        val metadataRetriever = MediaMetadataRetriever()
        var bitmap : Bitmap? = null

        val songUri = songs?.get(randomSong)?.uri
        try {
            metadataRetriever.setDataSource(context, songUri)
            val embeddedPic = metadataRetriever.embeddedPicture ?: return null
            bitmap = BitmapFactory.decodeByteArray(embeddedPic, 0, embeddedPic.size)
        } catch (
            e: Exception
        ) {
            Log.e("Mp3MetadataExtractor", "${e.message}")
        }
        return bitmap
    }
}