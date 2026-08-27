package com.anas.kegelflow.data

import kotlinx.coroutines.flow.Flow

class KegelRepository(private val dao: KegelDao) {

    val allWorkoutLogs: Flow<List<WorkoutLog>> = dao.getAllWorkoutLogs()
    val customPlans: Flow<List<CustomPlan>> = dao.getAllCustomPlans()
    val reminders: Flow<List<ReminderSetting>> = dao.getAllReminders()

    suspend fun insertWorkoutLog(log: WorkoutLog) {
        dao.insertWorkoutLog(log)
    }

    suspend fun clearWorkoutLogs() {
        dao.clearWorkoutLogs()
    }

    suspend fun insertCustomPlan(plan: CustomPlan) {
        dao.insertCustomPlan(plan)
    }

    suspend fun deleteCustomPlan(id: Long) {
        dao.deleteCustomPlan(id)
    }

    suspend fun insertReminder(reminder: ReminderSetting): Long {
        return dao.insertReminder(reminder)
    }

    suspend fun getEnabledRemindersList(): List<ReminderSetting> {
        return dao.getEnabledRemindersList()
    }

    suspend fun updateReminderEnabled(id: Long, enabled: Boolean) {
        dao.updateReminderEnabled(id, enabled)
    }

    suspend fun deleteReminder(id: Long) {
        dao.deleteReminder(id)
    }
}
