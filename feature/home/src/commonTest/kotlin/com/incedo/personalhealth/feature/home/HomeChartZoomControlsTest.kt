package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeChartZoomControlsTest {

    @Test
    fun nextChartZoomLevel_clampsWithinBounds() {
        assertEquals(MaxChartZoomLevel, nextChartZoomLevel(MaxChartZoomLevel, zoomIn = true))
        assertEquals(MinChartZoomLevel, nextChartZoomLevel(MinChartZoomLevel, zoomIn = false))
        assertEquals(1.2f, nextChartZoomLevel(1f, zoomIn = true))
    }
}
