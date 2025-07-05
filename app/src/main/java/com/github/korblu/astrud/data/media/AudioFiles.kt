package com.github.korblu.astrud.data.media

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile

// First code of bluu-chan guys, thank you AI for teaching
// me how to use this API because otherwise...(gulp) 05/25/25

class AudioFiles(private val activity : Activity) {
    val contractDocumentTree = ActivityResultContracts.OpenDocumentTree()
    val contractOpenDocument = ActivityResultContracts.OpenDocument()

    val createDir = { uri: Uri? ->
        this.handleCreateDirActivityResult(
                requestCode = 1,
                if (uri != null) Activity.RESULT_OK else Activity.RESULT_CANCELED,
                Intent().apply { data = uri }
        )
    }

    fun handleCreateDirActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { treeURI ->
                activity.contentResolver.takePersistableUriPermission(
                    treeURI,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                val parentDir = DocumentFile.fromTreeUri(activity, treeURI)

                if (parentDir != null && parentDir.isDirectory) {
                    parentDir.createDirectory("Astrud Music")
                }
            }
        }
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
    fun getEmbeddedPic() {
        // todo Actually make this work lol lazy lazy (from bluu to bluu)
    }
}