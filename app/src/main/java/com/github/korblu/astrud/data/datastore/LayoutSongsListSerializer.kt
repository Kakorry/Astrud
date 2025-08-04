package com.github.korblu.astrud.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object LayoutSongsListSerializer : Serializer<LayoutSongsList> {
    override val defaultValue: LayoutSongsList = LayoutSongsList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LayoutSongsList {
        return try {
            LayoutSongsList.parseFrom(input)
        } catch (exception: Exception) {
            exception.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: LayoutSongsList, output: OutputStream) {
        withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}