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
                companyName = "Personal Health AI",
                location = "In app",
                imageDataUrl = "data:image/png;base64,abc",
                isSelected = true
            ),
            CoachProfile(
                id = "coach-2",
                type = CoachType.PERSONAL_TRAINER,
                name = "Sanne",
                companyName = "Peak Motion Studio",
                location = "Utrecht",
                isSelected = false
            )
        )

        val decoded = decodeCoachProfiles(encodeCoachProfiles(original))
        val toggled = toggleCoachSelection(decoded, "coach-2")
        val removed = removeCoachProfile(decoded, "coach-1")

        assertEquals(original, decoded)
        assertEquals(2, toggled.size)
        assertTrue(toggled.last().isSelected)
        assertEquals(listOf("coach-2"), removed.map { it.id })
    }

    @Test
    fun decodeLegacyPayloadWithoutCompanyName_stillWorks() {
        val legacy = """
            coach-1
            AI_COACH
            Coach Nova
            In app
            data:image/png;base64,abc
            true
        """.trimIndent()

        val decoded = decodeCoachProfiles(legacy)

        assertEquals(1, decoded.size)
        assertEquals("", decoded.single().companyName)
        assertEquals("Coach Nova", decoded.single().name)
    }
}
