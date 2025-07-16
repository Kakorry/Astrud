package com.github.korblu.astrud.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Song::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun songDao() : SongsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Disclaimer: I got this whole block from AI because it's Standard Boilerplate. Though
          i do intend on learning exactly what this does in the future whenever i find myself
          messing with database shenanigans again. bluu-chan's 07/05/25.
          */
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