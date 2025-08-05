package com.github.korblu.astrud.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS songs")

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `recents` (
                `songId` TEXT NOT NULL,
                `lastPlayedTimestamp` INTEGER NOT NULL,
                `album` TEXT,
                `artist` TEXT,
                PRIMARY KEY(`songId`)
            )
        """)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE recents RENAME COLUMN songId TO songUri")
    }
}