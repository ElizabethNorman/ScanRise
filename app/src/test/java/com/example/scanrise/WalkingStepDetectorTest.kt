package com.example.scanrise

import org.junit.Assert.*
import org.junit.Test

class WalkingStepDetectorTest {
    @Test fun stationaryPhoneDoesNotCountSteps() {
        val detector = WalkingStepDetector()
        repeat(100) { assertFalse(detector.onSample(9.81, it * 100_000_000L)) }
    }

    @Test fun sustainedPeakAndRapidNoiseDoNotCountExtraSteps() {
        val detector = WalkingStepDetector()
        assertFalse(detector.onSample(14.0, 0L))
        assertFalse(detector.onSample(9.8, 100_000_000L))
        assertTrue(detector.onSample(13.0, 200_000_000L))
        assertFalse(detector.onSample(14.0, 600_000_000L))
        assertFalse(detector.onSample(9.8, 650_000_000L))
        assertTrue(detector.onSample(13.0, 700_000_000L))
        assertFalse(detector.onSample(9.8, 750_000_000L))
        assertFalse(detector.onSample(13.0, 800_000_000L))
    }

    @Test fun regularWalkingPeaksCountSteps() {
        val detector = WalkingStepDetector()
        repeat(50) { index ->
            val time = index * 600_000_000L
            assertFalse(detector.onSample(9.8, time))
            assertTrue(detector.onSample(13.0, time + 200_000_000L))
        }
    }
}
