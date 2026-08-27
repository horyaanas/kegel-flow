package com.anas.kegelflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean = true
)
