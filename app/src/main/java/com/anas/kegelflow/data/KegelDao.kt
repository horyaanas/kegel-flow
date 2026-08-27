package com.anas.kegelflow.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KegelDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    @Query("DELETE FROM workout_logs")
    suspend fun clearWorkoutLogs()

    @Query("SELECT * FROM custom_plans ORDER BY id DESC")
    fun getAllCustomPlans(): Flow<List<CustomPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomPlan(plan: CustomPlan)

    @Query("DELETE FROM custom_plans WHERE id = :id")
    suspend fun deleteCustomPlan(id: Long)

    @Query("SELECT * FROM reminder_settings ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<ReminderSetting>>

    @Query("SELECT * FROM reminder_settings WHERE isEnabled = 1")
    suspend fun getEnabledRemindersList(): List<ReminderSetting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderSetting): Long

    @Query("UPDATE reminder_settings SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateReminderEnabled(id: Long, enabled: Boolean)

    @Query("DELETE FROM reminder_settings WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}
