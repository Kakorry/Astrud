package com.github.korblu.astrud.data.media

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile

// First code of bluu-chan guys, thank you AI for teaching
// me how to use this API because otherwise...(gulp) 05/25/25

class AudioFiles(private val activity : Activity) {
    private val REQUEST_CODE_PICK_DIRECTORY = 1

    val contract = ActivityResultContracts.OpenDocumentTree()

    val onResult = { uri: Uri? ->
                this.handleDirPickActivityResult(
                requestCode = 1,
                if (uri != null) Activity.RESULT_OK else Activity.RESULT_CANCELED,
                Intent().apply { data = uri })
    }


    fun handleDirPickActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_OK) {
            data?.data?.also { treeURI ->
                activity.contentResolver.takePersistableUriPermission(
                    treeURI,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                val parentDir = DocumentFile.fromTreeUri(activity, treeURI)

                if (parentDir != null && parentDir.isDirectory) {
                    val folder = parentDir.createDirectory("Astrud Music")

                    if (folder != null) {
                        activity.runOnUiThread {
                            android.widget.Toast.makeText(activity, "Astrud Music is now born", android.widget.Toast.LENGTH_LONG).show()
                        }
                    } else {
                        activity.runOnUiThread {
                            android.widget.Toast.makeText(activity, "Astrud Music is dead (failed to create)", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_CANCELED) {
            activity.runOnUiThread {
                android.widget.Toast.makeText(activity,"YOU CANCELED WTH", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

}