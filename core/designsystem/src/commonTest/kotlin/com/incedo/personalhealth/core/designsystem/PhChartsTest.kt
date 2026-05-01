package com.incedo.personalhealth.core.designsystem

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class PhChartsTest {
    @Test
    fun pointOnCircleReturnsCardinalPositions() {
        val center = Offset(10f, 10f)
        val radius = 5f

        assertNear(Offset(15f, 10f), pointOnCircle(center, radius, 0f))
        assertNear(Offset(10f, 15f), pointOnCircle(center, radius, 90f))
        assertNear(Offset(5f, 10f), pointOnCircle(center, radius, 180f))
    }

    private fun assertNear(expected: Offset, actual: Offset) {
        assertTrue(abs(expected.x - actual.x) < 0.001f, "Expected x=${expected.x}, got ${actual.x}")
        assertTrue(abs(expected.y - actual.y) < 0.001f, "Expected y=${expected.y}, got ${actual.y}")
    }
}
