package com.example.scanrise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)

        val alarmIntent = Intent(context, AlarmActivity::class.java)

        val alarmPendingIntent = PendingIntent.getActivity(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            ALARM_CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("ScanRise")
            .setContentText("Your test alarm is ringing")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(alarmPendingIntent, true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(ALARM_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "ScanRise Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
                lockscreenVisibility =
                    android.app.Notification.VISIBILITY_PUBLIC
            }

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ALARM_CHANNEL_ID = "scanrise_alarm"
        const val ALARM_NOTIFICATION_ID = 1001
    }
}