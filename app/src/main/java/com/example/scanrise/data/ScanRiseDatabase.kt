package com.example.scanrise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScanObjectEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ScanRiseDatabase : RoomDatabase() {

    abstract fun scanObjectDao(): ScanObjectDao

    companion object {

        @Volatile
        private var INSTANCE: ScanRiseDatabase? = null

        fun getDatabase(context: Context): ScanRiseDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanRiseDatabase::class.java,
                    "scanrise_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}