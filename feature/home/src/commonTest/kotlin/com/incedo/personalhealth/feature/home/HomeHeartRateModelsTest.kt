package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeHeartRateModelsTest {

    @Test
    fun heartRateChartAxis_buildsPaddedOrderedLabels() {
        val axis = heartRateChartAxis(
            points = listOf(
                HeartRateTimelinePoint(label = "06:00", bpm = 58),
                HeartRateTimelinePoint(label = "12:00", bpm = 76),
                HeartRateTimelinePoint(label = "18:00", bpm = 64)
            )
        )

        assertEquals(4, axis.yAxisLabels.size)
        assertTrue(axis.maxValue > 76.0)
        assertTrue(axis.minValue < 58.0)
        assertTrue(axis.yAxisLabels.first().endsWith("bpm"))
        assertTrue(axis.yAxisLabels.last().endsWith("bpm"))
    }
}
