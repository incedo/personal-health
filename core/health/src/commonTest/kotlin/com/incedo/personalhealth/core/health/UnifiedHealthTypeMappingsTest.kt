package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnifiedHealthTypeMappingsTest {

    @Test
    fun mappings_coverRequestedDomains() {
        val coveredDomains = UnifiedHealthTypeMappings.all
            .map { it.canonicalType.domain }
            .toSet()

        assertTrue(HealthDomain.ACTIVITY in coveredDomains)
        assertTrue(HealthDomain.BODY_MEASUREMENTS in coveredDomains)
        assertTrue(HealthDomain.CYCLE_TRACKING in coveredDomains)
        assertTrue(HealthDomain.NUTRITION in coveredDomains)
        assertTrue(HealthDomain.SLEEP in coveredDomains)
        assertTrue(HealthDomain.VITALS in coveredDomains)
    }

    @Test
    fun canonicalTypes_areUnique() {
        val canonicalTypes = UnifiedHealthTypeMappings.all.map { it.canonicalType }
        assertEquals(canonicalTypes.size, canonicalTypes.toSet().size)
    }
}
