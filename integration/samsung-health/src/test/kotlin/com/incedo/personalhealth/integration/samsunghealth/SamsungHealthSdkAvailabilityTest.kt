package com.incedo.personalhealth.integration.samsunghealth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SamsungHealthSdkAvailabilityTest {

    @Test
    fun compareSamsungHealthVersions_ordersSemanticVersions() {
        assertTrue(compareSamsungHealthVersions("6.30.2", "6.30.1") > 0)
        assertTrue(compareSamsungHealthVersions("6.30.2", "6.30.2") == 0)
        assertTrue(compareSamsungHealthVersions("6.29.9", "6.30.2") < 0)
    }

    @Test
    fun readyAvailability_reportsUsableState() {
        val availability = SamsungHealthSdkAvailability(
            status = SamsungHealthSdkStatus.READY,
            appVersion = "6.30.2"
        )

        assertTrue(availability.isReady)
        assertEquals("6.30.2", availability.appVersion)
    }
}
