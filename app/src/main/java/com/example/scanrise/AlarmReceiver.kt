package com.example.scanrise

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.scanrise.alarm.AlarmScheduler
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.scanrise.data.ScanRiseDatabase

class AlarmReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        val alarmId =
            intent?.getLongExtra(
                AlarmScheduler.EXTRA_ALARM_ID,
                -1L
            ) ?: -1L

        if (alarmId == -1L) {
            return
        }

        Log.d(
            "ScanRiseAlarm",
            "ALARM RECEIVER FIRED: alarmId=$alarmId"
        )

        createNotificationChannel(context)

        val serviceIntent =
            Intent(
                context,
                AlarmService::class.java
            ).apply {

                putExtra(
                    AlarmScheduler.EXTRA_ALARM_ID,
                    alarmId
                )
            }

        ContextCompat.startForegroundService(
            context,
            serviceIntent
        )

        handleAlarmLifecycle(
            context,
            alarmId
        )
    }

    private fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    ALARM_CHANNEL_ID,
                    "ScanRise Alarms",
                    NotificationManager
                        .IMPORTANCE_HIGH
                ).apply {

                    description =
                        "Alarm notifications"

                    lockscreenVisibility =
                        android.app.Notification
                            .VISIBILITY_PUBLIC
                }

            context
                .getSystemService(
                    NotificationManager::class.java
                )
                .createNotificationChannel(
                    channel
                )
        }
    }

    companion object {

        const val ALARM_CHANNEL_ID =
            "scanrise_alarm"

        fun notificationId(
            alarmId: Long
        ): Int {
            return 10_000 +
                    alarmId.toInt()
        }
    }

    private fun handleAlarmLifecycle(
        context: Context,
        alarmId: Long
    ) {

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val database =
                    ScanRiseDatabase.getDatabase(
                        context.applicationContext
                    )

                val alarmWithObjects =
                    database
                        .alarmDao()
                        .getByIdWithObjects(
                            alarmId
                        )

                if (alarmWithObjects != null) {

                    val alarm =
                        alarmWithObjects.alarm

                    if (alarm.repeatDays == 0) {

                        database
                            .alarmDao()
                            .setEnabled(
                                alarm.id,
                                false
                            )

                    } else {

                        AlarmScheduler.scheduleNext(
                            context,
                            alarm
                        )
                    }
                }

            } finally {
                pendingResult.finish()
            }
        }
    }
}