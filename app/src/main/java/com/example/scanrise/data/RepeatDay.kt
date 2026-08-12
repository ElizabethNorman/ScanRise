package com.example.scanrise.data

enum class RepeatDay(
    val bit: Int,
    val shortName: String
) {

    MONDAY(
        bit = 1 shl 0,
        shortName = "Mon"
    ),

    TUESDAY(
        bit = 1 shl 1,
        shortName = "Tue"
    ),

    WEDNESDAY(
        bit = 1 shl 2,
        shortName = "Wed"
    ),

    THURSDAY(
        bit = 1 shl 3,
        shortName = "Thu"
    ),

    FRIDAY(
        bit = 1 shl 4,
        shortName = "Fri"
    ),

    SATURDAY(
        bit = 1 shl 5,
        shortName = "Sat"
    ),

    SUNDAY(
        bit = 1 shl 6,
        shortName = "Sun"
    )
}

fun isDaySelected(
    repeatDays: Int,
    day: RepeatDay
): Boolean {
    return repeatDays and day.bit != 0
}

fun toggleRepeatDay(
    repeatDays: Int,
    day: RepeatDay
): Int {

    return if (isDaySelected(repeatDays, day)) {
        repeatDays and day.bit.inv()
    } else {
        repeatDays or day.bit
    }
}