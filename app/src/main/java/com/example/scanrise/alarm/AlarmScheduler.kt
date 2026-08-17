package com.example.scanrise.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.scanrise.AlarmReceiver
import com.example.scanrise.MainActivity
import com.example.scanrise.data.AlarmEntity
import com.example.scanrise.data.RepeatDay
import com.example.scanrise.data.isDaySelected
import com.example.scanrise.data.ScanRiseDatabase
import java.util.Calendar
import java.util.Date

object AlarmScheduler {

    const val EXTRA_ALARM_ID = "ALARM_ID"

    fun scheduleNext(
        context: Context,
        alarm: AlarmEntity
    ): Boolean {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            Log.e(
                "ScanRise",
                "Cannot schedule exact alarm ${alarm.id}"
            )

            return false
        }

        val triggerTime =
            getNextTriggerTime(alarm)

        val readableTime =
            java.text.DateFormat
                .getDateTimeInstance()
                .format(Date(triggerTime))

        Log.d(
            "ScanRise",
            "Scheduling alarm ${alarm.id} for $readableTime"
        )

        val intent =
            Intent(
                context,
                AlarmReceiver::class.java
            ).apply {
                putExtra(
                    EXTRA_ALARM_ID,
                    alarm.id
                )
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val showIntent =
            PendingIntent.getActivity(
                context,
                alarm.id.toInt() + 100_000,
                Intent(
                    context,
                    MainActivity::class.java
                ),
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmClockInfo =
            AlarmManager.AlarmClockInfo(
                triggerTime,
                showIntent
            )

        alarmManager.setAlarmClock(
            alarmClockInfo,
            pendingIntent
        )

        Log.d(
            "ScanRiseAlarm",
            "setAlarmClock SUCCESS: alarmId=${alarm.id}, trigger=$readableTime"
        )

        return true
    }

    fun cancel(
        context: Context,
        alarmId: Long
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                AlarmReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_NO_CREATE or
                        PendingIntent.FLAG_IMMUTABLE
            )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    suspend fun restoreEnabledAlarms(
        context: Context
    ) {

        Log.d(
            "ScanRiseAlarm",
            "restoreEnabledAlarms START"
        )

        val database =
            ScanRiseDatabase.getDatabase(
                context.applicationContext
            )

        val enabledAlarms =
            database
                .alarmDao()
                .getAllEnabledWithObjects()

        Log.d(
            "ScanRiseAlarm",
            "Found ${enabledAlarms.size} enabled alarms"
        )

        enabledAlarms.forEach { alarmWithObjects ->

            val alarm =
                alarmWithObjects.alarm

            Log.d(
                "ScanRiseAlarm",
                "Restoring alarm ${alarm.id}: " +
                        "${alarm.hour}:${alarm.minute}, " +
                        "days=${alarm.repeatDays}"
            )

            val success =
                scheduleNext(
                    context,
                    alarm
                )

            Log.d(
                "ScanRiseAlarm",
                "Restore result for ${alarm.id}: $success"
            )
        }

        Log.d(
            "ScanRiseAlarm",
            "restoreEnabledAlarms END"
        )
    }

    private fun getNextTriggerTime(
        alarm: AlarmEntity
    ): Long {

        val now = Calendar.getInstance()

        // No repeat days:
        // next occurrence of this clock time.
        if (alarm.repeatDays == 0) {

            val next =
                Calendar.getInstance().apply {

                    set(
                        Calendar.HOUR_OF_DAY,
                        alarm.hour
                    )

                    set(
                        Calendar.MINUTE,
                        alarm.minute
                    )

                    set(
                        Calendar.SECOND,
                        0
                    )

                    set(
                        Calendar.MILLISECOND,
                        0
                    )
                }

            if (!next.after(now)) {
                next.add(
                    Calendar.DAY_OF_YEAR,
                    1
                )
            }

            return next.timeInMillis
        }

        // Repeating alarm:
        // Find the next selected weekday.
        for (daysAhead in 0..7) {

            val candidate =
                Calendar.getInstance().apply {

                    add(
                        Calendar.DAY_OF_YEAR,
                        daysAhead
                    )

                    set(
                        Calendar.HOUR_OF_DAY,
                        alarm.hour
                    )

                    set(
                        Calendar.MINUTE,
                        alarm.minute
                    )

                    set(
                        Calendar.SECOND,
                        0
                    )

                    set(
                        Calendar.MILLISECOND,
                        0
                    )
                }

            if (!candidate.after(now)) {
                continue
            }

            val repeatDay =
                calendarDayToRepeatDay(
                    candidate.get(
                        Calendar.DAY_OF_WEEK
                    )
                )

            if (
                isDaySelected(
                    alarm.repeatDays,
                    repeatDay
                )
            ) {
                return candidate.timeInMillis
            }
        }

        error(
            "Could not determine next alarm occurrence"
        )
    }

    private fun calendarDayToRepeatDay(
        calendarDay: Int
    ): RepeatDay {

        return when (calendarDay) {

            Calendar.MONDAY ->
                RepeatDay.MONDAY

            Calendar.TUESDAY ->
                RepeatDay.TUESDAY

            Calendar.WEDNESDAY ->
                RepeatDay.WEDNESDAY

            Calendar.THURSDAY ->
                RepeatDay.THURSDAY

            Calendar.FRIDAY ->
                RepeatDay.FRIDAY

            Calendar.SATURDAY ->
                RepeatDay.SATURDAY

            Calendar.SUNDAY ->
                RepeatDay.SUNDAY

            else ->
                error(
                    "Unknown calendar day: $calendarDay"
                )
        }
    }
}