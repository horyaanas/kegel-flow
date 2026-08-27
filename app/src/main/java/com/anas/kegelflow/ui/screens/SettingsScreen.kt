package com.anas.kegelflow.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.data.AppThemeMode
import com.anas.kegelflow.data.ReminderSetting
import com.anas.kegelflow.ui.theme.PrimaryTeal
import com.anas.kegelflow.ui.viewmodel.KegelViewModel
import com.anas.kegelflow.util.LocalizationHelper

@Composable
fun SettingsScreen(viewModel: KegelViewModel) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val language = settings.language
    val isAr = language == AppLanguage.ARABIC

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var showResetDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = LocalizationHelper.getString("reset_stats", language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = LocalizationHelper.getString("confirm_reset", language))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.clearAllStats()
                    },
                    modifier = Modifier.testTag("confirm_reset_stats_button")
                ) {
                    Text(
                        text = if (isAr) "إعادة ضبط" else "Reset",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    modifier = Modifier.testTag("cancel_reset_stats_button")
                ) {
                    Text(text = LocalizationHelper.getString("cancel", language))
                }
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            language = language,
            onDismiss = { showAddReminderDialog = false },
            onSave = { hour, minute, label ->
                viewModel.addReminder(hour, minute, label)
                showAddReminderDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = LocalizationHelper.getString("tab_settings", language),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
        )

        // Language Switcher Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PrimaryTeal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("language", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        FilterChip(
                            selected = settings.language == AppLanguage.ARABIC,
                            onClick = { viewModel.updateLanguage(AppLanguage.ARABIC) },
                            label = { Text("العربية") },
                            modifier = Modifier.testTag("lang_arabic_chip")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = settings.language == AppLanguage.ENGLISH,
                            onClick = { viewModel.updateLanguage(AppLanguage.ENGLISH) },
                            label = { Text("English") },
                            modifier = Modifier.testTag("lang_english_chip")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Theme Switcher Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = PrimaryTeal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("theme", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        FilterChip(
                            selected = settings.themeMode == AppThemeMode.LIGHT,
                            onClick = { viewModel.updateThemeMode(AppThemeMode.LIGHT) },
                            label = { Text(if (isAr) "فاتح" else "Light") },
                            modifier = Modifier.testTag("theme_light_chip")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = settings.themeMode == AppThemeMode.DARK,
                            onClick = { viewModel.updateThemeMode(AppThemeMode.DARK) },
                            label = { Text(if (isAr) "داكن" else "Dark") },
                            modifier = Modifier.testTag("theme_dark_chip")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio & Vibration Toggles
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = PrimaryTeal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("sound", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = settings.soundEnabled,
                        onCheckedChange = { viewModel.toggleSound(it) },
                        modifier = Modifier.testTag("sound_toggle_switch")
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = PrimaryTeal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("vibration", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { viewModel.toggleVibration(it) },
                        modifier = Modifier.testTag("vibration_toggle_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Countdown Preparation Time Selector
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = LocalizationHelper.getString("prep_time", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(3, 5, 10).forEach { seconds ->
                        FilterChip(
                            selected = settings.prepCountdownSeconds == seconds,
                            onClick = { viewModel.setPrepCountdown(seconds) },
                            label = { Text("$seconds ${if (isAr) "ثوانٍ" else "sec"}") },
                            modifier = Modifier.testTag("prep_time_${seconds}_chip")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reminders Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = PrimaryTeal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = LocalizationHelper.getString("reminders", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            checkAndRequestNotificationPermission()
                            showAddReminderDialog = true
                        },
                        modifier = Modifier.testTag("add_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = PrimaryTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (reminders.isEmpty()) {
                    Text(
                        text = if (isAr) "لم يتم ضبط تذكيرات يومية بعد." else "No daily reminders set.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    reminders.forEach { reminder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val timeStr = String.format("%02d:%02d", reminder.hour, reminder.minute)
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = reminder.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = reminder.isEnabled,
                                    onCheckedChange = {
                                        if (it) checkAndRequestNotificationPermission()
                                        viewModel.toggleReminder(reminder, it)
                                    },
                                    modifier = Modifier.testTag("reminder_toggle_${reminder.id}")
                                )
                                IconButton(
                                    onClick = { viewModel.deleteReminder(reminder.id) },
                                    modifier = Modifier.testTag("delete_reminder_${reminder.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Stats Danger Card
        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("reset_stats_button")
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = LocalizationHelper.getString("reset_stats", language),
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AddReminderDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (hour: Int, minute: Int, label: String) -> Unit
) {
    var hourStr by remember { mutableStateOf("08") }
    var minuteStr by remember { mutableStateOf("00") }
    var label by remember { mutableStateOf(if (language == AppLanguage.ARABIC) "تذكير التمرين" else "Workout Reminder") }

    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_reminder_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = LocalizationHelper.getString("add_reminder", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = { hourStr = it },
                        label = { Text(if (isAr) "الساعة (0-23)" else "Hour (0-23)") },
                        modifier = Modifier.weight(1f).testTag("reminder_hour_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = minuteStr,
                        onValueChange = { minuteStr = it },
                        label = { Text(if (isAr) "الدقيقة" else "Minute") },
                        modifier = Modifier.weight(1f).testTag("reminder_minute_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(if (isAr) "الوصف" else "Label") },
                    modifier = Modifier.fillMaxWidth().testTag("reminder_label_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.testTag("cancel_reminder_button")) {
                        Text(text = LocalizationHelper.getString("cancel", language))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val hour = hourStr.toIntOrNull()?.coerceIn(0, 23) ?: 8
                            val minute = minuteStr.toIntOrNull()?.coerceIn(0, 59) ?: 0
                            onSave(hour, minute, label)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        modifier = Modifier.testTag("save_reminder_button")
                    ) {
                        Text(
                            text = LocalizationHelper.getString("save", language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
