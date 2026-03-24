package com.incedo.personalhealth.core.recommendations

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class StubRecommendationOfDayApiTest {
    private val api = StubRecommendationOfDayApi()

    @Test
    fun returnsHighReadinessRecommendationForStrongFitScore() = runTest {
        val recommendation = api.getRecommendationOfDay(
            DailyRecommendationRequest(
                fitScore = 82,
                heartRateBpm = 61,
                steps = 8420,
                activityMinutesToday = 44,
                profileName = "Kees"
            )
        )

        assertEquals("Focus van de dag", recommendation.title)
        assertTrue(recommendation.summary.contains("82"))
        assertTrue(recommendation.summary.contains("8420"))
        assertTrue(recommendation.guidance.contains("61"))
        assertEquals(3, recommendation.insights.size)
        assertTrue(recommendation.insights.first().description.contains("61"))
        assertEquals(RecommendationSource.STUB, recommendation.source)
    }

    @Test
    fun returnsRecoveryRecommendationWhenHeartRateRunsHigh() = runTest {
        val recommendation = api.getRecommendationOfDay(
            DailyRecommendationRequest(
                fitScore = 64,
                heartRateBpm = 78,
                steps = 3100,
                activityMinutesToday = 18,
                profileName = "Kees"
            )
        )

        assertTrue(recommendation.summary.contains("64"))
        assertTrue(recommendation.guidance.contains("78"))
        assertTrue(recommendation.insights.first().description.contains("78"))
    }

    @Test
    fun rotatesMessagesAcrossCallsForSameInput() = runTest {
        val request = DailyRecommendationRequest(
            fitScore = 72,
            heartRateBpm = 70,
            steps = 6200,
            activityMinutesToday = 32,
            profileName = "Kees"
        )

        val first = api.getRecommendationOfDay(request)
        val second = api.getRecommendationOfDay(request)
        val third = api.getRecommendationOfDay(request)

        assertTrue(first.summary != second.summary || first.guidance != second.guidance)
        assertTrue(second.summary != third.summary || second.guidance != third.guidance)
        assertTrue(first.insights != second.insights)
        assertTrue(second.insights != third.insights)
    }
}
