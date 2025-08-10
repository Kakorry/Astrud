package com.github.korblu.astrud.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.korblu.astrud.data.room.dao.AlbumsPlayedDao
import com.github.korblu.astrud.data.room.dao.ArtistsPlayedDao
import com.github.korblu.astrud.data.room.dao.OtherStatsDao
import com.github.korblu.astrud.data.room.dao.RecentsDao
import com.github.korblu.astrud.data.room.dao.SongsPlayedDao
import com.github.korblu.astrud.data.room.entity.RoomAlbumsPlayed
import com.github.korblu.astrud.data.room.entity.RoomArtistsPlayed
import com.github.korblu.astrud.data.room.entity.RoomOtherStats
import com.github.korblu.astrud.data.room.entity.RoomRecents
import com.github.korblu.astrud.data.room.entity.RoomSongsPlayed

@Database(
    entities = [
        RoomRecents::class,
        RoomSongsPlayed::class,
        RoomAlbumsPlayed::class,
        RoomArtistsPlayed::class,
        RoomOtherStats::class
               ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun recentsDao() : RecentsDao
    abstract fun songsPlayedDao() : SongsPlayedDao
    abstract fun albumsPlayedDao() : AlbumsPlayedDao
    abstract fun artistsPlayedDao() : ArtistsPlayedDao
    abstract fun otherStatsDao() : OtherStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Disclaimer: I got this whole block from AI because it's Standard Boilerplate. Though
        // i do intend on learning exactly what this does in the future whenever i find myself
        // messing with database shenanigans again. bluu-chan's 07/05/25.

        // I am messing with database shenanigans again, and I still have absolutely no idea what
        // this does. Actually not absolutely, I understand good part of it, but I would never be
        // able to write this on my own. bluu-chan's 07/25/25

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "astrud_app_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}