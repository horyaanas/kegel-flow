package com.anas.kegelflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_plans")
data class CustomPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val contractSeconds: Int,
    val relaxSeconds: Int,
    val reps: Int,
    val isDefault: Boolean = false
)
