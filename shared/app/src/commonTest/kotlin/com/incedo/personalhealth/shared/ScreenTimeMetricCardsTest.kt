package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.wellbeing.ScreenTimePermissionState
import com.incedo.personalhealth.core.wellbeing.ScreenTimeSummary
import com.incedo.personalhealth.core.wellbeing.SocialAppUsage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenTimeMetricCardsTest {
    @Test
    fun buildScreenTimeMetricCards_buildsSummaryAndPerAppCards() {
        val cards = buildScreenTimeMetricCards(
            ScreenTimeSummary(
                permissionState = ScreenTimePermissionState.GRANTED,
                totalScreenMinutes = 210,
                socialScreenMinutes = 95,
                selectedSocialApps = listOf(
                    SocialAppUsage("com.instagram.android", "Instagram", 55),
                    SocialAppUsage("com.reddit.frontpage", "Reddit", 40),
                    SocialAppUsage("com.whatsapp", "WhatsApp", 0)
                )
            )
        )

        assertEquals("210 min", cards.first { it.id == "screen_time_total" }.value)
        assertEquals("95 min", cards.first { it.id == "screen_time_social" }.value)
        assertTrue(cards.any { it.id == "screen_time_com.instagram.android" && it.value == "55 min" })
        assertTrue(cards.any { it.id == "screen_time_com.reddit.frontpage" && it.value == "40 min" })
        assertTrue(cards.none { it.id == "screen_time_com.whatsapp" })
    }

    @Test
    fun buildScreenTimeMetricCards_showsPermissionNeededState() {
        val cards = buildScreenTimeMetricCards(
            ScreenTimeSummary(
                permissionState = ScreenTimePermissionState.REQUIRES_ACCESS,
                totalScreenMinutes = 0,
                socialScreenMinutes = 0,
                selectedSocialApps = emptyList()
            )
        )

        assertEquals("Toegang nodig", cards.first { it.id == "screen_time_total" }.value)
        assertEquals("Toegang nodig", cards.first { it.id == "screen_time_social" }.value)
        assertEquals(2, cards.size)
    }
}
