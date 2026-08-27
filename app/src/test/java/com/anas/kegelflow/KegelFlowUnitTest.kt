package com.anas.kegelflow

import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.ui.model.PlanLevel
import com.anas.kegelflow.ui.model.WorkoutPlan
import com.anas.kegelflow.ui.model.WorkoutStage
import com.anas.kegelflow.util.LocalizationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KegelFlowUnitTest {

    @Test
    fun testWorkoutPlanPresets() {
        val presets = WorkoutPlan.getAllPresets()
        assertEquals(3, presets.size)

        val beginner = WorkoutPlan.PRESET_BEGINNER
        assertEquals(PlanLevel.BEGINNER, beginner.level)
        assertEquals(5, beginner.contractSeconds)
        assertEquals(5, beginner.relaxSeconds)
        assertEquals(5, beginner.reps)
        assertEquals(50, beginner.totalDurationSeconds)
        assertEquals("50s", beginner.getFormattedDuration())

        val intermediate = WorkoutPlan.PRESET_INTERMEDIATE
        assertEquals(PlanLevel.INTERMEDIATE, intermediate.level)
        assertEquals(7, intermediate.contractSeconds)
        assertEquals(5, intermediate.relaxSeconds)
        assertEquals(8, intermediate.reps)
        assertEquals(96, intermediate.totalDurationSeconds)
        assertEquals("1m 36s", intermediate.getFormattedDuration())

        val advanced = WorkoutPlan.PRESET_ADVANCED
        assertEquals(PlanLevel.ADVANCED, advanced.level)
        assertEquals(10, advanced.contractSeconds)
        assertEquals(5, advanced.relaxSeconds)
        assertEquals(10, advanced.reps)
        assertEquals(150, advanced.totalDurationSeconds)
        assertEquals("2m 30s", advanced.getFormattedDuration())
    }

    @Test
    fun testLocalizationHelper() {
        val enTitle = LocalizationHelper.getString("app_title", AppLanguage.ENGLISH)
        assertEquals("Kegel Flow", enTitle)

        val enStart = LocalizationHelper.getString("start_workout", AppLanguage.ENGLISH)
        assertEquals("Start Workout Now", enStart)

        val arTitle = LocalizationHelper.getString("app_title", AppLanguage.ARABIC)
        assertTrue(arTitle.isNotEmpty())

        val arContract = LocalizationHelper.getString("stage_contract", AppLanguage.ARABIC)
        assertTrue(arContract.isNotEmpty())
    }

    @Test
    fun testWorkoutStages() {
        val stages = WorkoutStage.values()
        assertEquals(5, stages.size)
        assertTrue(stages.contains(WorkoutStage.PREPARING))
        assertTrue(stages.contains(WorkoutStage.CONTRACT))
        assertTrue(stages.contains(WorkoutStage.RELAX))
        assertTrue(stages.contains(WorkoutStage.REST))
        assertTrue(stages.contains(WorkoutStage.FINISHED))
    }
}