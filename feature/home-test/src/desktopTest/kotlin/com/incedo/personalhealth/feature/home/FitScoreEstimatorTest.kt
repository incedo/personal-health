package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FitScoreEstimatorTest {

    @Test
    fun estimateFitScore_clampsToBounds() {
        assertEquals(10, estimateFitScore(stepCount = 0, heartRateBpm = 180))
        assertEquals(100, estimateFitScore(stepCount = 20_000, heartRateBpm = 50))
    }

    @Test
    fun estimateFitScore_increasesWithMoreStepsAtSameHeartRate() {
        val low = estimateFitScore(stepCount = 3_000, heartRateBpm = 65)
        val high = estimateFitScore(stepCount = 9_000, heartRateBpm = 65)
        assertTrue(high > low)
    }
}
