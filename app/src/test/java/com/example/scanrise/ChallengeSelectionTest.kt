package com.example.scanrise

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class ChallengeSelectionTest {
    @Test fun selectionUsesRequestedWeights() {
        val counts = (0L until 20_000L).map { selectChallenge(it, true) }.groupingBy { it }.eachCount()
        assertTrue(abs(counts.getValue(ChallengeKind.WALK) / 20_000.0 - 0.50) < 0.02)
        assertTrue(abs(counts.getValue(ChallengeKind.MATH) / 20_000.0 - 0.25) < 0.02)
        assertTrue(abs(counts.getValue(ChallengeKind.MEMORY) / 20_000.0 - 0.25) < 0.02)
    }

    @Test fun devicesWithoutMotionSensorOnlyGetMentalChallenges() {
        val selections = (0L until 100L).map { selectChallenge(it, false) }.toSet()
        assertEquals(setOf(ChallengeKind.MATH, ChallengeKind.MEMORY), selections)
    }
}
