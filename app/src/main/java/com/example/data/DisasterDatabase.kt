package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        SosEntity::class,
        ReportEntity::class,
        ReliefCampEntity::class,
        AlertEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DisasterDatabase : RoomDatabase() {
    abstract fun disasterDao(): DisasterDao

    companion object {
        @Volatile
        private var INSTANCE: DisasterDatabase? = null

        fun getDatabase(context: Context): DisasterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DisasterDatabase::class.java,
                    "disaster_rescue_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
