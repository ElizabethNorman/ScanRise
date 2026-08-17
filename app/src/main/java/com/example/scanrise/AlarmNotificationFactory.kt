package com.example.scanrise

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.scanrise.alarm.AlarmScheduler

object AlarmNotificationFactory {

    fun create(
        context: Context,
        alarmId: Long
    ): android.app.Notification {

        val alarmIntent =
            Intent(
                context,
                AlarmActivity::class.java
            ).apply {

                putExtra(
                    AlarmScheduler.EXTRA_ALARM_ID,
                    alarmId
                )

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val alarmPendingIntent =
            PendingIntent.getActivity(
                context,
                alarmId.toInt(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        return NotificationCompat.Builder(
            context,
            AlarmReceiver.ALARM_CHANNEL_ID
        )
            .setSmallIcon(
                android.R.drawable.ic_lock_idle_alarm
            )
            .setContentTitle("ScanRise")
            .setContentText(
                "Scan an object to dismiss"
            )
            .setPriority(
                NotificationCompat.PRIORITY_MAX
            )
            .setCategory(
                NotificationCompat.CATEGORY_ALARM
            )
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(
                alarmPendingIntent
            )
            .setFullScreenIntent(
                alarmPendingIntent,
                true
            )
            .build()
    }
}