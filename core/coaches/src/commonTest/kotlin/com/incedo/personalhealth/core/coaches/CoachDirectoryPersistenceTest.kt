package com.incedo.personalhealth.core.coaches

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoachDirectoryPersistenceTest {
    @Test
    fun encodeDecodeAndSelectionStayStable() {
        val original = listOf(
            CoachProfile(
                id = "coach-1",
                type = CoachType.AI_COACH,
                name = "Coach Nova",
                location = "In app",
                imageDataUrl = "data:image/png;base64,abc",
                isSelected = true
            ),
            CoachProfile(
                id = "coach-2",
                type = CoachType.PERSONAL_TRAINER,
                name = "Sanne",
                location = "Utrecht",
                isSelected = false
            )
        )

        val decoded = decodeCoachProfiles(encodeCoachProfiles(original))
        val toggled = toggleCoachSelection(decoded, "coach-2")

        assertEquals(original, decoded)
        assertEquals(2, toggled.size)
        assertTrue(toggled.last().isSelected)
    }
}
