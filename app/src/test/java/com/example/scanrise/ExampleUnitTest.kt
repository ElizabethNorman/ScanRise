package com.example.scanrise

import org.junit.Test

import org.junit.Assert.assertEquals

class GreetingForHourTest {
    @Test
    fun returnsGreetingForEachPartOfDay() {
        assertEquals("Good night", greetingForHour(4))
        assertEquals("Good morning", greetingForHour(5))
        assertEquals("Good morning", greetingForHour(11))
        assertEquals("Good afternoon", greetingForHour(12))
        assertEquals("Good afternoon", greetingForHour(16))
        assertEquals("Good evening", greetingForHour(17))
        assertEquals("Good evening", greetingForHour(20))
        assertEquals("Good night", greetingForHour(21))
    }
}
