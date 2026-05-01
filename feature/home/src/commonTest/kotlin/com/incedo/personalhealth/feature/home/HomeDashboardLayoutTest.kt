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
}
