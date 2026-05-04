package com.incedo.personalhealth.core.designsystem

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PhAvatarTest {
    @Test
    fun avatarGeometryScalesSharedShapeFromShortestSide() {
        val geometry = phAvatarGeometry(
            width = 100f,
            height = 80f,
            variant = PhAvatarVariant.Neutral,
            selected = false
        )

        assertNear(5.6f, geometry.stroke)
        assertNear(Offset(50f, 27.2f), geometry.head.center)
        assertNear(12f, geometry.head.radius)
        assertNear(Offset(22f, 36.8f), geometry.shoulders.topLeft)
        assertNear(Size(56f, 28.8f), geometry.shoulders.size)
        assertNear(205f, geometry.shoulders.startAngle)
        assertNear(130f, geometry.shoulders.sweepAngle)
        assertNull(geometry.selection)
    }

    @Test
    fun masculineAvatarUsesBrowLineAccent() {
        val accent = phAvatarGeometry(100f, 80f, PhAvatarVariant.Masculine, selected = false).accent

        val line = assertIs<PhAvatarAccent.Line>(accent)
        assertNear(Offset(36f, 19.2f), line.start)
        assertNear(Offset(64f, 19.2f), line.end)
    }

    @Test
    fun feminineAvatarUsesHairArcAccent() {
        val accent = phAvatarGeometry(100f, 80f, PhAvatarVariant.Feminine, selected = false).accent

        val arc = assertIs<PhAvatarAccent.Arc>(accent)
        assertNear(Offset(28f, 13.6f), arc.topLeft)
        assertNear(Size(44f, 22.4f), arc.size)
        assertNear(205f, arc.startAngle)
        assertNear(130f, arc.sweepAngle)
    }

    @Test
    fun neutralAvatarUsesDotAccent() {
        val accent = phAvatarGeometry(100f, 80f, PhAvatarVariant.Neutral, selected = false).accent

        val dot = assertIs<PhAvatarAccent.Dot>(accent)
        assertNear(Offset(50f, 19.2f), dot.center)
        assertNear(3.2f, dot.radius)
    }

    @Test
    fun selectedAvatarAddsSelectionRing() {
        val selection = assertNotNull(
            phAvatarGeometry(100f, 80f, PhAvatarVariant.Neutral, selected = true).selection
        )

        assertNear(35.2f, selection.radius)
        assertNear(4.2f, selection.stroke)
    }

    private fun assertNear(expected: Float, actual: Float) {
        kotlin.test.assertTrue(abs(expected - actual) < 0.001f, "Expected $expected, got $actual")
    }

    private fun assertNear(expected: Offset, actual: Offset) {
        assertNear(expected.x, actual.x)
        assertNear(expected.y, actual.y)
    }

    private fun assertNear(expected: Size, actual: Size) {
        assertNear(expected.width, actual.width)
        assertNear(expected.height, actual.height)
    }
}
