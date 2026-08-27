package com.anas.kegelflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.data.WorkoutLog
import com.anas.kegelflow.ui.theme.AccentPeach
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.theme.SecondaryMint
import com.anas.kegelflow.ui.viewmodel.KegelViewModel
import com.anas.kegelflow.util.LocalizationHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(viewModel: KegelViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    val language = appSettings.language
    val stats by viewModel.statsSummary.collectAsState()
    val logs by viewModel.allLogs.collectAsState()

    val isAr = language == AppLanguage.ARABIC

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("stats_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = LocalizationHelper.getString("tab_stats", language),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Top 3 Stat Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stats.totalSessionsCount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = LocalizationHelper.getString("total_sessions", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = SecondaryMint,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stats.totalDurationMinutes} ${LocalizationHelper.getString("mins", language)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAr) "مدة التدريب" else "Training Time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AccentPeach,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stats.currentStreakDays} ${LocalizationHelper.getString("days", language)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = LocalizationHelper.getString("streak_days", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Training Calendar Grid
        item {
            TrainingCalendarCard(
                language = language,
                completedDatesSet = stats.completedDatesSet
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Session History Header
        item {
            Text(
                text = if (isAr) "سجل الجلسات السابقة" else "Session History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (logs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "لم تقم بإجراء أي جلسات بعد. ابدأ اليوم!" else "No session logs recorded yet. Start today!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(logs) { log ->
                SessionHistoryItem(log = log, language = language)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TrainingCalendarCard(
    language: AppLanguage,
    completedDatesSet: Set<String>
) {
    val isAr = language == AppLanguage.ARABIC
    val calendar = Calendar.getInstance()
    val monthFormat = SimpleDateFormat("MMMM yyyy", if (isAr) Locale.forLanguageTag("ar") else Locale.US)
    val currentMonthName = monthFormat.format(calendar.time)

    // Calculate days of current month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تقويم الأيام المكتملة" else "Workout Calendar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = currentMonthName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid of 7 Columns
            val dayHeaders = if (isAr)
                listOf("ح", "ن", "ث", "ر", "خ", "ج", "س")
            else
                listOf("S", "M", "T", "W", "T", "F", "S")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Days Grid
            val daysList = (1..daysInMonth).toList()
            val chunkedDays = daysList.chunked(7)

            chunkedDays.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { dayNum ->
                        val dateKey = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayNum)
                        val isCompleted = completedDatesSet.contains(dateKey)

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNum",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // Pad empty items if last week row is less than 7
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionHistoryItem(
    log: WorkoutLog,
    language: AppLanguage
) {
    val isAr = language == AppLanguage.ARABIC
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", if (isAr) Locale.forLanguageTag("ar") else Locale.US)
    val formattedDate = dateFormat.format(Date(log.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = log.planName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val mins = log.totalDurationSeconds / 60
                val secs = log.totalDurationSeconds % 60
                Text(
                    text = if (mins > 0) "${mins}m ${secs}s" else "${secs}s",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                Text(
                    text = "${log.repsCompleted} ${LocalizationHelper.getString("sessions", language)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
