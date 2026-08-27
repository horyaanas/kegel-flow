package com.anas.kegelflow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.ui.model.WorkoutStage
import com.anas.kegelflow.ui.theme.AccentPeach
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.theme.RelaxCyan
import com.anas.kegelflow.ui.viewmodel.KegelViewModel
import androidx.activity.compose.BackHandler
import com.anas.kegelflow.util.LocalizationHelper

@Composable
fun WorkoutScreen(
    viewModel: KegelViewModel,
    onFinish: () -> Unit
) {
    val workoutState by viewModel.workoutState.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val language = appSettings.language

    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept system back button during active workout
    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = LocalizationHelper.getString("quit", language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.ARABIC) "هل تريد بالتأكيد إلغاء جلسة التمرين الحالية؟" else "Are you sure you want to quit the current workout session?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.quitWorkout()
                        onFinish()
                    },
                    modifier = Modifier.testTag("confirm_quit_button")
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "نعم، إلغاء" else "Yes, Quit",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false },
                    modifier = Modifier.testTag("cancel_quit_button")
                ) {
                    Text(text = LocalizationHelper.getString("cancel", language))
                }
            }
        )
    }

    if (workoutState.stage == WorkoutStage.FINISHED) {
        // Celebration Finished Screen
        WorkoutFinishedView(
            language = language,
            repsCompleted = workoutState.currentRep,
            totalSeconds = workoutState.totalElapsedSeconds,
            onDone = onFinish
        )
        return
    }

    val stageColor = when (workoutState.stage) {
        WorkoutStage.PREPARING -> MaterialTheme.colorScheme.primary
        WorkoutStage.CONTRACT -> PrimaryTeal
        WorkoutStage.RELAX -> RelaxCyan
        WorkoutStage.REST -> AccentPeach
        WorkoutStage.FINISHED -> PrimaryTeal
    }

    val stageTitle = when (workoutState.stage) {
        WorkoutStage.PREPARING -> LocalizationHelper.getString("stage_preparing", language)
        WorkoutStage.CONTRACT -> LocalizationHelper.getString("stage_contract", language)
        WorkoutStage.RELAX -> LocalizationHelper.getString("stage_relax", language)
        WorkoutStage.REST -> LocalizationHelper.getString("stage_rest", language)
        WorkoutStage.FINISHED -> LocalizationHelper.getString("stage_finished", language)
    }

    // Gentle Pulse Animation for CONTRACT stage
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (workoutState.stage == WorkoutStage.CONTRACT && !workoutState.isPaused) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("workout_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("quit_workout_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Quit",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = String.format(
                        LocalizationHelper.getString("rep_counter", language),
                        workoutState.currentRep,
                        workoutState.totalReps
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.skipStage() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("skip_stage_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Giant Circular Timer & Animated Stage Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Stage Name Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = stageColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = stageTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = stageColor,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Big Timer Ring
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Background Track Ring
                CircularProgressIndicator(
                    progress = { 1.0f },
                    modifier = Modifier.fillMaxSize(),
                    color = stageColor.copy(alpha = 0.15f),
                    strokeWidth = 18.dp,
                    strokeCap = StrokeCap.Round
                )

                // Animated Progress Ring
                val animatedProgress by animateFloatAsState(
                    targetValue = workoutState.stageProgress,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                    label = "timerProgress"
                )

                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = stageColor,
                    strokeWidth = 18.dp,
                    strokeCap = StrokeCap.Round
                )

                // Huge Center Number
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${workoutState.secondsRemainingInStage}",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) "ثانية" else "Seconds",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Dynamic Real-Time Instruction Card
            val instructionTitle = when (workoutState.stage) {
                WorkoutStage.PREPARING -> LocalizationHelper.getString("instruction_prep_title", language)
                WorkoutStage.CONTRACT -> LocalizationHelper.getString("instruction_contract_title", language)
                WorkoutStage.RELAX -> LocalizationHelper.getString("instruction_relax_title", language)
                WorkoutStage.REST -> LocalizationHelper.getString("instruction_rest_title", language)
                WorkoutStage.FINISHED -> LocalizationHelper.getString("stage_finished", language)
            }

            val instructionSub = when (workoutState.stage) {
                WorkoutStage.PREPARING -> LocalizationHelper.getString("instruction_prep_sub", language)
                WorkoutStage.CONTRACT -> LocalizationHelper.getString("instruction_contract_sub", language)
                WorkoutStage.RELAX -> LocalizationHelper.getString("instruction_relax_sub", language)
                WorkoutStage.REST -> LocalizationHelper.getString("instruction_rest_sub", language)
                WorkoutStage.FINISHED -> ""
            }

            val instructionIcon = when (workoutState.stage) {
                WorkoutStage.PREPARING -> Icons.Default.SelfImprovement
                WorkoutStage.CONTRACT -> Icons.Default.FitnessCenter
                WorkoutStage.RELAX -> Icons.Default.Air
                WorkoutStage.REST -> Icons.Default.Coffee
                WorkoutStage.FINISHED -> Icons.Default.EmojiEvents
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = stageColor.copy(alpha = 0.10f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.2.dp,
                    color = stageColor.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(stageColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = instructionIcon,
                            contentDescription = null,
                            tint = stageColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = instructionTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = stageColor
                        )
                        if (instructionSub.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = instructionSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Controls (Pause / Resume)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (workoutState.isPaused) {
                        viewModel.resumeWorkout()
                    } else {
                        viewModel.pauseWorkout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(60.dp)
                    .testTag("pause_resume_workout_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (workoutState.isPaused) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (workoutState.isPaused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (workoutState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (workoutState.isPaused)
                            LocalizationHelper.getString("resume", language)
                        else
                            LocalizationHelper.getString("pause", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutFinishedView(
    language: AppLanguage,
    repsCompleted: Int,
    totalSeconds: Int,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
            .testTag("workout_finished_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PrimaryTeal.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (language == AppLanguage.ARABIC) "عاش! أحسنت إكمال التمرين 🎉" else "Awesome! Workout Completed 🎉",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (language == AppLanguage.ARABIC) "تم تسجيل الجلسة في إحصائياتك بنجاح" else "Session successfully logged to your statistics",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$repsCompleted",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) "التكرارات" else "Reps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val mins = totalSeconds / 60
                    val secs = totalSeconds % 60
                    Text(
                        text = if (mins > 0) "${mins}m ${secs}s" else "${secs}s",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) "المدة الإجمالية" else "Duration",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("done_workout_finished_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
        ) {
            Text(
                text = if (language == AppLanguage.ARABIC) "العودة للرئيسية" else "Return to Home",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
