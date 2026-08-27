package com.anas.kegelflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WorkoutLog::class, CustomPlan::class, ReminderSetting::class],
    version = 1,
    exportSchema = false
)
abstract class KegelDatabase : RoomDatabase() {
    abstract fun kegelDao(): KegelDao

    companion object {
        @Volatile
        private var INSTANCE: KegelDatabase? = null

        fun getDatabase(context: Context): KegelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KegelDatabase::class.java,
                    "kegel_flow_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
