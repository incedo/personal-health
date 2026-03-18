package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeMetricsTest {

    @Test
    fun estimateFitScore_staysWithinBounds() {
        assertEquals(10, estimateFitScore(stepCount = 0, heartRateBpm = 180))
        assertEquals(100, estimateFitScore(stepCount = 20_000, heartRateBpm = 50))
    }

    @Test
    fun estimateFitScore_increasesWithStepCount() {
        val low = estimateFitScore(stepCount = 3_000, heartRateBpm = 65)
        val high = estimateFitScore(stepCount = 9_000, heartRateBpm = 65)
        assertTrue(high > low)
    }

    @Test
    fun formatSteps_appliesThousandsSeparator() {
        assertEquals("950", formatSteps(950))
        assertEquals("12.345", formatSteps(12_345))
        assertEquals("1.000.000", formatSteps(1_000_000))
    }

    @Test
    fun fallbackStepTimeline_returnsSingleNonNegativePoint() {
        val timeline = fallbackStepTimeline(-50)
        assertEquals(1, timeline.size)
        assertEquals("Nu", timeline[0].label)
        assertEquals(0, timeline[0].steps)
    }

    @Test
    fun summarizeStepTimeline_groupsPointsIntoLargerBuckets() {
        val summary = summarizeStepTimeline(
            points = listOf(
                StepTimelinePoint("00:00", 100),
                StepTimelinePoint("01:00", 120),
                StepTimelinePoint("02:00", 80),
                StepTimelinePoint("03:00", 60)
            ),
            pointsPerBucket = 2
        )

        assertEquals(2, summary.size)
        assertEquals("00:00-01:00", summary[0].label)
        assertEquals(220, summary[0].steps)
        assertEquals("02:00-03:00", summary[1].label)
        assertEquals(140, summary[1].steps)
    }

    @Test
    fun stepDetailStats_returnsDailySummary() {
        val stats = stepDetailStats(
            listOf(
                StepTimelinePoint("09:00", 400),
                StepTimelinePoint("10:00", 1_250),
                StepTimelinePoint("11:00", 0)
            )
        )

        assertEquals(1_650, stats.totalSteps)
        assertEquals("10:00", stats.peakHourLabel)
        assertEquals(1_250, stats.peakHourSteps)
        assertEquals(2, stats.activeHours)
    }

    @Test
    fun logQuickActivity_prependsNewEntryWithIncrementedId() {
        val first = logQuickActivity(emptyList(), QuickActivityType.RUNNING)
        val second = logQuickActivity(first, QuickActivityType.SWIMMING)

        assertEquals("running-01", first.first().id)
        assertEquals(QuickActivityType.SWIMMING, second.first().type)
        assertEquals("swimming-02", second.first().id)
        assertEquals("running-01", second.last().id)
    }

    @Test
    fun quickActivitySummary_formatsEmptySingleAndPluralStates() {
        assertEquals("Nog geen activiteiten gelogd", quickActivitySummary(emptyList()))
        assertEquals(
            "1 activiteit gelogd",
            quickActivitySummary(logQuickActivity(emptyList(), QuickActivityType.FITNESS))
        )
        assertEquals(
            "2 activiteiten gelogd",
            quickActivitySummary(
                logQuickActivity(
                    logQuickActivity(emptyList(), QuickActivityType.FITNESS),
                    QuickActivityType.CYCLING
                )
            )
        )
    }
}
