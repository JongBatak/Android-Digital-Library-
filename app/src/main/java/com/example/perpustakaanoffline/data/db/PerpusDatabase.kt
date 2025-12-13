package com.example.perpustakaanoffline.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(
    entities = [AccountEntity::class, BookEntity::class, ReadingStatEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class PerpusDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun bookDao(): BookDao
    abstract fun readingStatDao(): ReadingStatDao

    companion object {
        private const val DB_NAME = "perpustakaan.db"
        private const val PRELOAD_PATH = "databases/perpustakaan_preload.db"
        fun create(context: Context): PerpusDatabase {
            val appContext = context.applicationContext
            return Room.databaseBuilder(appContext, PerpusDatabase::class.java, DB_NAME)
                .createFromAsset(PRELOAD_PATH)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
