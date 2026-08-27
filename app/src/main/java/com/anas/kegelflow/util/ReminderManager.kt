package com.anas.kegelflow.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anas.kegelflow.MainActivity
import com.anas.kegelflow.R
import java.util.Calendar

object ReminderManager {

    const val CHANNEL_ID = "kegel_reminders_channel"
    private const val CHANNEL_NAME = "Kegel Workout Reminders"
    private const val CHANNEL_DESC = "Daily notifications to remind you about Kegel exercises"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(
        context: Context,
        reminderId: Long,
        hour: Int,
        minute: Int,
        label: String
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            createNotificationChannel(context)

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("EXTRA_REMINDER_ID", reminderId)
                putExtra("EXTRA_HOUR", hour)
                putExtra("EXTRA_MINUTE", minute)
                putExtra("EXTRA_LABEL", label)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If time has already passed today, schedule for tomorrow
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val triggerTime = calendar.timeInMillis

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }

            Log.d("ReminderManager", "Scheduled reminder ID $reminderId for ${calendar.time}")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error scheduling reminder", e)
        }
    }

    fun cancelReminder(context: Context, reminderId: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("ReminderManager", "Cancelled reminder ID $reminderId")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error cancelling reminder", e)
        }
    }

    fun showReminderNotification(
        context: Context,
        reminderId: Long,
        hour: Int,
        minute: Int,
        label: String
    ) {
        try {
            createNotificationChannel(context)

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val contentPendingIntent = PendingIntent.getActivity(
                context,
                reminderId.toInt(),
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val displayLabel = if (label.isNotBlank()) label else "حان وقت تمرين كيجل اليومي 🌿"
            val subtitle = "جلسة قصيرة الآن تعزز قوتك واستقرارك وصحتك البدنية!"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(displayLabel)
                .setContentText(subtitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$displayLabel\n$subtitle"))
                .setColor(0xFF006B5E.toInt())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(reminderId.toInt(), builder.build())

            // Reschedule for next day so it repeats daily
            scheduleReminder(context, reminderId, hour, minute, label)
        } catch (e: SecurityException) {
            Log.e("ReminderManager", "Permission denied showing notification", e)
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error showing notification", e)
        }
    }
}
