package com.anas.kegelflow.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)
        val hour = intent.getIntExtra("EXTRA_HOUR", 8)
        val minute = intent.getIntExtra("EXTRA_MINUTE", 0)
        val label = intent.getStringExtra("EXTRA_LABEL") ?: "تذكير تمرين كيجل اليومي"

        Log.d("ReminderReceiver", "Alarm received for reminder ID: $reminderId ($hour:$minute)")

        if (reminderId != -1L) {
            ReminderManager.showReminderNotification(
                context = context,
                reminderId = reminderId,
                hour = hour,
                minute = minute,
                label = label
            )
        }
    }
}
