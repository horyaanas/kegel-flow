package com.anas.kegelflow.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.anas.kegelflow.data.KegelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootReceiver", "Device rebooted / package replaced. Rescheduling alarms...")
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = KegelDatabase.getDatabase(context)
                    val enabledReminders = db.kegelDao().getEnabledRemindersList()
                    for (reminder in enabledReminders) {
                        ReminderManager.scheduleReminder(
                            context = context,
                            reminderId = reminder.id,
                            hour = reminder.hour,
                            minute = reminder.minute,
                            label = reminder.label
                        )
                    }
                    Log.d("BootReceiver", "Successfully rescheduled ${enabledReminders.size} reminders.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to reschedule reminders on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
