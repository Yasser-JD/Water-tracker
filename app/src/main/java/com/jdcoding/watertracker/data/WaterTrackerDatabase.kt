package com.jdcoding.watertracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jdcoding.watertracker.model.Goal
import com.jdcoding.watertracker.model.User
import com.jdcoding.watertracker.model.WaterIntake

@Database(
    entities = [User::class, WaterIntake::class, Goal::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WaterTrackerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: WaterTrackerDatabase? = null

        fun getInstance(context: Context): WaterTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterTrackerDatabase::class.java,
                    "water_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
