package com.jnetai.communityalert.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jnetai.communityalert.R

class AlertReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "alert_reminders"
        private const val CHANNEL_NAME = "Alert Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getLongExtra("ALERT_ID", -1)
        val title = intent.getStringExtra("ALERT_TITLE") ?: "Community Alert"
        val description = intent.getStringExtra("ALERT_DESCRIPTION") ?: "You have a pending alert"

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(alertId.toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for community alert reminders"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}