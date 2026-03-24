package com.incedo.personalhealth.feature.home

import com.incedo.personalhealth.core.coaches.CoachSearchItem
import com.incedo.personalhealth.core.coaches.CoachType
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeCoachStateTest {
    @Test
    fun applySearchItem_populatesEditorState() {
        val item = CoachSearchItem(
            id = "coach-stub-1",
            name = "Sanne Vermeer",
            companyName = "Peak Motion Studio",
            type = CoachType.PERSONAL_TRAINER,
            location = "Utrecht"
        )

        val updated = CoachEditorState(searchQuery = "san").applySearchItem(item)

        assertEquals("coach-stub-1", updated.selectedSearchItemId)
        assertEquals(CoachType.PERSONAL_TRAINER, updated.selectedType)
        assertEquals("Sanne Vermeer", updated.name)
        assertEquals("Peak Motion Studio", updated.companyName)
        assertEquals("Utrecht", updated.location)
    }
}
