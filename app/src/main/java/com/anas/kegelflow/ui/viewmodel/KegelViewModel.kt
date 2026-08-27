package com.anas.kegelflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anas.kegelflow.data.*
import com.anas.kegelflow.ui.model.PlanLevel
import com.anas.kegelflow.ui.model.WorkoutPlan
import com.anas.kegelflow.ui.model.WorkoutStage
import com.anas.kegelflow.util.AudioHapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WorkoutSessionUiState(
    val isActive: Boolean = false,
    val stage: WorkoutStage = WorkoutStage.PREPARING,
    val isPaused: Boolean = false,
    val plan: WorkoutPlan = WorkoutPlan.PRESET_BEGINNER,
    val currentRep: Int = 1,
    val totalReps: Int = 5,
    val secondsRemainingInStage: Int = 3,
    val totalSecondsInStage: Int = 3,
    val stageProgress: Float = 1f, // 1.0 down to 0.0 or 0.0 to 1.0
    val totalElapsedSeconds: Int = 0
)

data class StatsSummary(
    val todaySessionsCount: Int = 0,
    val totalSessionsCount: Int = 0,
    val totalDurationMinutes: Int = 0,
    val currentStreakDays: Int = 0,
    val completedDatesSet: Set<String> = emptySet() // "YYYY-MM-DD"
)

class KegelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KegelRepository
    private val audioHapticHelper = AudioHapticHelper(application)

    init {
        val db = KegelDatabase.getDatabase(application)
        repository = KegelRepository(db.kegelDao())
        com.anas.kegelflow.util.ReminderManager.createNotificationChannel(application)
        viewModelScope.launch {
            try {
                val enabled = repository.getEnabledRemindersList()
                for (rem in enabled) {
                    com.anas.kegelflow.util.ReminderManager.scheduleReminder(
                        context = application,
                        reminderId = rem.id,
                        hour = rem.hour,
                        minute = rem.minute,
                        label = rem.label
                    )
                }
            } catch (_: Exception) {}
        }
    }

    // App Settings State
    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    // Active Selected Tab (0 = Home, 1 = Plans, 2 = Stats, 3 = Settings)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Selected Active Plan
    private val _selectedPlan = MutableStateFlow(WorkoutPlan.PRESET_BEGINNER)
    val selectedPlan: StateFlow<WorkoutPlan> = _selectedPlan.asStateFlow()

    // Workout Session UI State
    private val _workoutState = MutableStateFlow(WorkoutSessionUiState())
    val workoutState: StateFlow<WorkoutSessionUiState> = _workoutState.asStateFlow()

    // Room Flows
    val allLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customPlans: StateFlow<List<CustomPlan>> = repository.customPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderSetting>> = repository.reminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated Stats Summary
    val statsSummary: StateFlow<StatsSummary> = allLogs.map { logs ->
        calculateStats(logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary())

    private var timerJob: Job? = null

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSelectedPlan(plan: WorkoutPlan) {
        _selectedPlan.value = plan
    }

    fun updateLanguage(language: AppLanguage) {
        _appSettings.update { it.copy(language = language) }
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        _appSettings.update { it.copy(themeMode = themeMode) }
    }

    fun toggleSound(enabled: Boolean) {
        _appSettings.update { it.copy(soundEnabled = enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        _appSettings.update { it.copy(vibrationEnabled = enabled) }
    }

    fun setPrepCountdown(seconds: Int) {
        _appSettings.update { it.copy(prepCountdownSeconds = seconds) }
    }

    // --- Workout Timer Logic ---

    fun startWorkout(plan: WorkoutPlan = _selectedPlan.value) {
        _selectedPlan.value = plan
        val prepTime = _appSettings.value.prepCountdownSeconds

        _workoutState.value = WorkoutSessionUiState(
            isActive = true,
            stage = WorkoutStage.PREPARING,
            isPaused = false,
            plan = plan,
            currentRep = 1,
            totalReps = plan.reps,
            secondsRemainingInStage = prepTime,
            totalSecondsInStage = prepTime,
            stageProgress = 1.0f,
            totalElapsedSeconds = 0
        )

        notifyStageTransition(WorkoutStage.PREPARING)
        runTimerLoop()
    }

    fun pauseWorkout() {
        _workoutState.update { it.copy(isPaused = true) }
    }

    fun resumeWorkout() {
        _workoutState.update { it.copy(isPaused = false) }
    }

    fun skipStage() {
        advanceToNextStage()
    }

    fun quitWorkout() {
        timerJob?.cancel()
        timerJob = null
        _workoutState.value = WorkoutSessionUiState(isActive = false)
    }

    private fun runTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_workoutState.value.isActive && _workoutState.value.stage != WorkoutStage.FINISHED) {
                delay(1000L)

                if (_workoutState.value.isPaused) continue

                val current = _workoutState.value
                val remaining = current.secondsRemainingInStage - 1
                val newElapsed = current.totalElapsedSeconds + 1
                if (remaining > 0) {
                    val totalStageSecs = maxOf(1, current.totalSecondsInStage)
                    val progress = remaining.toFloat() / totalStageSecs.toFloat()
                    _workoutState.update {
                        it.copy(
                            secondsRemainingInStage = remaining,
                            stageProgress = progress,
                            totalElapsedSeconds = newElapsed
                        )
                    }
                } else {
                    _workoutState.update { it.copy(totalElapsedSeconds = newElapsed) }
                    advanceToNextStage()
                }
            }
        }
    }

    private fun advanceToNextStage() {
        val current = _workoutState.value
        val plan = current.plan

        when (current.stage) {
            WorkoutStage.PREPARING -> {
                // From PREPARING to CONTRACT rep 1
                _workoutState.update {
                    it.copy(
                        stage = WorkoutStage.CONTRACT,
                        currentRep = 1,
                        secondsRemainingInStage = plan.contractSeconds,
                        totalSecondsInStage = plan.contractSeconds,
                        stageProgress = 1.0f
                    )
                }
                notifyStageTransition(WorkoutStage.CONTRACT)
            }
            WorkoutStage.CONTRACT -> {
                // From CONTRACT to RELAX
                _workoutState.update {
                    it.copy(
                        stage = WorkoutStage.RELAX,
                        secondsRemainingInStage = plan.relaxSeconds,
                        totalSecondsInStage = plan.relaxSeconds,
                        stageProgress = 1.0f
                    )
                }
                notifyStageTransition(WorkoutStage.RELAX)
            }
            WorkoutStage.RELAX -> {
                if (plan.restBetweenRepsSeconds > 0) {
                    _workoutState.update {
                        it.copy(
                            stage = WorkoutStage.REST,
                            secondsRemainingInStage = plan.restBetweenRepsSeconds,
                            totalSecondsInStage = plan.restBetweenRepsSeconds,
                            stageProgress = 1.0f
                        )
                    }
                    notifyStageTransition(WorkoutStage.REST)
                } else {
                    advanceRepOrFinish()
                }
            }
            WorkoutStage.REST -> {
                advanceRepOrFinish()
            }
            WorkoutStage.FINISHED -> {
                // Already finished
            }
        }
    }

    private fun advanceRepOrFinish() {
        val current = _workoutState.value
        val plan = current.plan

        if (current.currentRep < current.totalReps) {
            val nextRep = current.currentRep + 1
            _workoutState.update {
                it.copy(
                    stage = WorkoutStage.CONTRACT,
                    currentRep = nextRep,
                    secondsRemainingInStage = plan.contractSeconds,
                    totalSecondsInStage = plan.contractSeconds,
                    stageProgress = 1.0f
                )
            }
            notifyStageTransition(WorkoutStage.CONTRACT)
        } else {
            // Finish Workout!
            _workoutState.update {
                it.copy(
                    stage = WorkoutStage.FINISHED,
                    secondsRemainingInStage = 0,
                    stageProgress = 0.0f
                )
            }
            notifyStageTransition(WorkoutStage.FINISHED)
            saveCompletedWorkoutLog()
        }
    }

    private fun notifyStageTransition(stage: WorkoutStage) {
        val settings = _appSettings.value
        if (settings.soundEnabled) {
            audioHapticHelper.playStageTransitionSound(stage.name)
        }
        if (settings.vibrationEnabled) {
            val type = if (stage == WorkoutStage.FINISHED) "HEAVY" else "NORMAL"
            audioHapticHelper.triggerVibration(type)
        }
    }

    private fun saveCompletedWorkoutLog() {
        viewModelScope.launch {
            val current = _workoutState.value
            val plan = current.plan
            val log = WorkoutLog(
                planName = if (_appSettings.value.language == AppLanguage.ARABIC) plan.titleAr else plan.titleEn,
                contractSeconds = plan.contractSeconds,
                relaxSeconds = plan.relaxSeconds,
                repsCompleted = current.currentRep,
                targetReps = plan.reps,
                totalDurationSeconds = current.totalElapsedSeconds,
                isCompleted = true
            )
            repository.insertWorkoutLog(log)
        }
    }

    // Custom Plans Management
    fun createCustomPlan(name: String, contractSeconds: Int, relaxSeconds: Int, reps: Int) {
        viewModelScope.launch {
            val custom = CustomPlan(
                name = name,
                contractSeconds = contractSeconds,
                relaxSeconds = relaxSeconds,
                reps = reps
            )
            repository.insertCustomPlan(custom)
        }
    }

    fun deleteCustomPlan(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomPlan(id)
        }
    }

    // Reminders Management
    fun addReminder(hour: Int, minute: Int, label: String) {
        viewModelScope.launch {
            val newId = repository.insertReminder(
                ReminderSetting(hour = hour, minute = minute, label = label, isEnabled = true)
            )
            com.anas.kegelflow.util.ReminderManager.scheduleReminder(
                context = getApplication(),
                reminderId = newId,
                hour = hour,
                minute = minute,
                label = label
            )
        }
    }

    fun toggleReminder(reminder: ReminderSetting, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReminderEnabled(reminder.id, enabled)
            if (enabled) {
                com.anas.kegelflow.util.ReminderManager.scheduleReminder(
                    context = getApplication(),
                    reminderId = reminder.id,
                    hour = reminder.hour,
                    minute = reminder.minute,
                    label = reminder.label
                )
            } else {
                com.anas.kegelflow.util.ReminderManager.cancelReminder(
                    context = getApplication(),
                    reminderId = reminder.id
                )
            }
        }
    }

    fun toggleReminder(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReminderEnabled(id, enabled)
            if (!enabled) {
                com.anas.kegelflow.util.ReminderManager.cancelReminder(
                    context = getApplication(),
                    reminderId = id
                )
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            com.anas.kegelflow.util.ReminderManager.cancelReminder(
                context = getApplication(),
                reminderId = id
            )
            repository.deleteReminder(id)
        }
    }

    fun clearAllStats() {
        viewModelScope.launch {
            repository.clearWorkoutLogs()
        }
    }

    private fun calculateStats(logs: List<WorkoutLog>): StatsSummary {
        if (logs.isEmpty()) return StatsSummary()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = dateFormat.format(Date())

        var todayCount = 0
        var totalSecs = 0
        val datesSet = mutableSetOf<String>()

        logs.forEach { log ->
            totalSecs += log.totalDurationSeconds
            val logDateStr = dateFormat.format(Date(log.timestamp))
            datesSet.add(logDateStr)
            if (logDateStr == todayStr) {
                todayCount++
            }
        }

        // Calculate Streak (consecutive days ending today or yesterday)
        var streak = 0
        val calendar = Calendar.getInstance()
        var checkDateStr = dateFormat.format(calendar.time)

        if (datesSet.contains(checkDateStr)) {
            while (datesSet.contains(checkDateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDateStr = dateFormat.format(calendar.time)
            }
        } else {
            // Check if streak ended yesterday
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDateStr = dateFormat.format(calendar.time)
            while (datesSet.contains(checkDateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDateStr = dateFormat.format(calendar.time)
            }
        }

        return StatsSummary(
            todaySessionsCount = todayCount,
            totalSessionsCount = logs.size,
            totalDurationMinutes = totalSecs / 60,
            currentStreakDays = streak,
            completedDatesSet = datesSet
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioHapticHelper.release()
    }
}
