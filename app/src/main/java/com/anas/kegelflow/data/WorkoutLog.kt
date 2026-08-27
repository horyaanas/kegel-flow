package com.anas.kegelflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val planName: String,
    val contractSeconds: Int,
    val relaxSeconds: Int,
    val repsCompleted: Int,
    val targetReps: Int,
    val totalDurationSeconds: Int,
    val isCompleted: Boolean = true
)
