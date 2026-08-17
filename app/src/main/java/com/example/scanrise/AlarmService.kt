package com.example.scanrise

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.scanrise.alarm.AlarmScheduler

class AlarmService : Service() {

    private var ringtone: Ringtone? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val alarmId =
            intent?.getLongExtra(
                AlarmScheduler.EXTRA_ALARM_ID,
                -1L
            ) ?: -1L

        if (alarmId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(
            AlarmReceiver.notificationId(alarmId),
            AlarmNotificationFactory.create(
                context = this,
                alarmId = alarmId
            )
        )

        startAlarmSound()

        return START_NOT_STICKY
    }

    private fun startAlarmSound() {

        if (ringtone?.isPlaying == true) {
            return
        }

        val alarmUri =
            RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            )

        ringtone =
            RingtoneManager
                .getRingtone(
                    this,
                    alarmUri
                )
                .apply {

                    audioAttributes =
                        AudioAttributes.Builder()
                            .setUsage(
                                AudioAttributes.USAGE_ALARM
                            )
                            .build()

                    if (
                        android.os.Build.VERSION.SDK_INT >= 28
                    ) {
                        isLooping = true
                    }

                    play()
                }
    }

    override fun onDestroy() {

        ringtone?.stop()
        ringtone = null

        super.onDestroy()
    }
}