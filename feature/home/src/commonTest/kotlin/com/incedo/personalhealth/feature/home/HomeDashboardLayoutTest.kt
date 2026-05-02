package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeDashboardLayoutTest {
    @Test
    fun compactDashboardMetricsUseSingleColumnRows() {
        assertEquals(listOf(1, 1, 1, 1), dashboardMetricRowSizes(expanded = false, cardCount = 4))
    }

    @Test
    fun expandedDashboardMetricsUseTwoColumnRows() {
        assertEquals(listOf(2, 2), dashboardMetricRowSizes(expanded = true, cardCount = 4))
        assertEquals(listOf(2, 2, 1), dashboardMetricRowSizes(expanded = true, cardCount = 5))
    }

    @Test
    fun dashboardPanelHelpersStayDeterministic() {
        val points = listOf(
            StepTimelinePoint("08", 0),
            StepTimelinePoint("09", 450),
            StepTimelinePoint("10", 1800)
        )

        assertEquals(2, activeStepBuckets(points))
        assertEquals(84, consistencyHeatmap(points).size)
        assertEquals(7, weeklyVolumeBars(activityMinutesToday = 30, stepsTimeline = points).size)
        assertEquals(80, sleepRecoveryScore(heartRateBpm = 68))
    }
}
