package com.jnetai.communityalert.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jnetai.communityalert.data.entity.Alert
import java.time.LocalDateTime
import java.time.ZoneId

object AlertScheduler {

    fun scheduleReminder(context: Context, alert: Alert) {
        val reminderTime = alert.reminderTime ?: return
        if (reminderTime.isBefore(LocalDateTime.now())) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReminderReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
            putExtra("ALERT_TITLE", alert.title)
            putExtra("ALERT_DESCRIPTION", alert.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = reminderTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, alertId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}