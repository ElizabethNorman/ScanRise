package com.example.scanrise.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ScanObjectEntity::class,
        AlarmEntity::class,
        AlarmObjectCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ScanRiseDatabase : RoomDatabase() {

    abstract fun scanObjectDao(): ScanObjectDao

    abstract fun alarmDao(): AlarmDao

    companion object {

        @Volatile
        private var INSTANCE: ScanRiseDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `alarms` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `hour` INTEGER NOT NULL,
                            `minute` INTEGER NOT NULL,
                            `label` TEXT NOT NULL,
                            `enabled` INTEGER NOT NULL,
                            `repeatDays` INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `alarm_object_cross_ref` (
                            `alarmId` INTEGER NOT NULL,
                            `objectId` INTEGER NOT NULL,
                            PRIMARY KEY(`alarmId`, `objectId`),
                            FOREIGN KEY(`alarmId`)
                                REFERENCES `alarms`(`id`)
                                ON UPDATE NO ACTION
                                ON DELETE CASCADE,
                            FOREIGN KEY(`objectId`)
                                REFERENCES `scan_objects`(`id`)
                                ON UPDATE NO ACTION
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_alarm_object_cross_ref_alarmId`
                        ON `alarm_object_cross_ref` (`alarmId`)
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_alarm_object_cross_ref_objectId`
                        ON `alarm_object_cross_ref` (`objectId`)
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(
            context: Context
        ): ScanRiseDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        ScanRiseDatabase::class.java,
                        "scanrise_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}