package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.HomeDetailDestination
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeNavigationEventsTest {

    @Test
    fun routeName_mapsDevTestDestination() {
        assertEquals("dev-test", HomeDetailDestination.DEV_TEST.routeName())
    }
}
