package com.incedo.personalhealth.core.wellbeing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenTimeModelsTest {
    @Test
    fun resolveSelectedSocialApps_defaultsToCatalogWhenNothingStored() {
        val apps = resolveSelectedSocialApps(selectedPackages = null)

        assertEquals(DefaultSocialAppCatalog.entries.size, apps.size)
        assertEquals("Instagram", apps.first().displayName)
    }

    @Test
    fun resolveSelectedSocialApps_filtersToStoredPackages() {
        val apps = resolveSelectedSocialApps(
            selectedPackages = setOf("com.whatsapp", "com.reddit.frontpage")
        )

        assertEquals(listOf("Reddit", "WhatsApp"), apps.map { it.displayName }.sorted())
        assertTrue(apps.none { it.displayName == "Instagram" })
    }
}
