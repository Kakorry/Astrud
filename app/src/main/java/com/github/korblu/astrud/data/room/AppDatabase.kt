package com.github.korblu.astrud.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.korblu.astrud.data.room.dao.RoomRecentsDao
import com.github.korblu.astrud.data.room.entity.RoomRecents

@Database(
    entities = [RoomRecents::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun roomRecentsDao() : RoomRecentsDao

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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}