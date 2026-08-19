package com.example.scanrise

import com.example.scanrise.alarm.AlarmScheduler
import com.example.scanrise.data.AlarmEntity
import com.example.scanrise.data.RepeatDay
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmSchedulerTest {

    private val utc = TimeZone.getTimeZone("UTC")

    @Test
    fun oneTimeAlarmInThePastSchedulesTomorrow() {
        val now = calendar(2026, Calendar.AUGUST, 17, 10, 30)
        val alarm = alarm(hour = 10, minute = 15)

        val result = resultCalendar(alarm, now)

        assertEquals(18, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(10, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(15, result.get(Calendar.MINUTE))
    }

    @Test
    fun repeatingAlarmSchedulesNextSelectedDay() {
        // Monday, with Wednesday selected.
        val now = calendar(2026, Calendar.AUGUST, 17, 10, 30)
        val alarm = alarm(
            hour = 8,
            minute = 0,
            repeatDays = RepeatDay.WEDNESDAY.bit
        )

        val result = resultCalendar(alarm, now)

        assertEquals(Calendar.WEDNESDAY, result.get(Calendar.DAY_OF_WEEK))
        assertEquals(19, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(8, result.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun repeatingAlarmWhoseTimePassedSchedulesFollowingWeek() {
        // Monday after the selected Monday alarm time.
        val now = calendar(2026, Calendar.AUGUST, 17, 10, 30)
        val alarm = alarm(
            hour = 10,
            minute = 0,
            repeatDays = RepeatDay.MONDAY.bit
        )

        val result = resultCalendar(alarm, now)

        assertEquals(Calendar.MONDAY, result.get(Calendar.DAY_OF_WEEK))
        assertEquals(24, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(10, result.get(Calendar.HOUR_OF_DAY))
    }

    private fun resultCalendar(
        alarm: AlarmEntity,
        now: Calendar
    ): Calendar = Calendar.getInstance(utc).apply {
        timeInMillis = AlarmScheduler.getNextTriggerTime(alarm, now)
    }

    private fun alarm(
        hour: Int,
        minute: Int,
        repeatDays: Int = 0
    ) = AlarmEntity(
        id = 1,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays
    )

    private fun calendar(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): Calendar = Calendar.getInstance(utc).apply {
        set(year, month, day, hour, minute, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
