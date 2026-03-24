package com.incedo.personalhealth.core.coaches

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoachSearchStubServiceTest {
    @Test
    fun search_matchesNameCompanyAndType() {
        val byName = CoachSearchStubService.search("sanne")
        val byCompany = CoachSearchStubService.search("fuel")
        val byType = CoachSearchStubService.search("lifestyle")

        assertEquals("Sanne Vermeer", byName.first().name)
        assertEquals("Milan de Groot", byCompany.first().name)
        assertTrue(byType.any { it.type == CoachType.LIFESTYLE_COACH })
    }
}
