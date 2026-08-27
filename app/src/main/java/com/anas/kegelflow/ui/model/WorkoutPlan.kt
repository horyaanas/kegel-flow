package com.anas.kegelflow.ui.model

enum class PlanLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    CUSTOM
}

data class WorkoutPlan(
    val level: PlanLevel,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val contractSeconds: Int,
    val relaxSeconds: Int,
    val reps: Int,
    val restBetweenRepsSeconds: Int = 0
) {
    val totalDurationSeconds: Int
        get() = (contractSeconds + relaxSeconds + restBetweenRepsSeconds) * reps

    fun getFormattedDuration(): String {
        val totalSecs = totalDurationSeconds
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return if (mins > 0) {
            "${mins}m ${secs}s"
        } else {
            "${secs}s"
        }
    }

    companion object {
        val PRESET_BEGINNER = WorkoutPlan(
            level = PlanLevel.BEGINNER,
            titleAr = "مبتدئ",
            titleEn = "Beginner",
            descriptionAr = "5 ثوانٍ ضغط + 5 ثوانٍ استرخاء × 5 تكرارات",
            descriptionEn = "5s Contract + 5s Relax × 5 reps",
            contractSeconds = 5,
            relaxSeconds = 5,
            reps = 5
        )

        val PRESET_INTERMEDIATE = WorkoutPlan(
            level = PlanLevel.INTERMEDIATE,
            titleAr = "متوسط",
            titleEn = "Intermediate",
            descriptionAr = "7 ثوانٍ ضغط + 5 ثوانٍ استرخاء × 8 تكرارات",
            descriptionEn = "7s Contract + 5s Relax × 8 reps",
            contractSeconds = 7,
            relaxSeconds = 5,
            reps = 8
        )

        val PRESET_ADVANCED = WorkoutPlan(
            level = PlanLevel.ADVANCED,
            titleAr = "متقدم",
            titleEn = "Advanced",
            descriptionAr = "10 ثوانٍ ضغط + 5 ثوانٍ استرخاء × 10 تكرارات",
            descriptionEn = "10s Contract + 5s Relax × 10 reps",
            contractSeconds = 10,
            relaxSeconds = 5,
            reps = 10
        )

        fun getAllPresets(): List<WorkoutPlan> = listOf(
            PRESET_BEGINNER,
            PRESET_INTERMEDIATE,
            PRESET_ADVANCED
        )
    }
}
