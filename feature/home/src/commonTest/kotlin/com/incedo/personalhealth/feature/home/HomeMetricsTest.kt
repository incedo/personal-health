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
        val first = logQuickActivity(emptyList(), QuickActivityType.RUNNING, nowEpochMillis = 100L)
        val second = logQuickActivity(first, QuickActivityType.SWIMMING, nowEpochMillis = 200L)

        assertEquals("running-01", first.first().id)
        assertEquals(QuickActivityType.SWIMMING, second.first().type)
        assertEquals("swimming-02", second.first().id)
        assertEquals(200L, second.first().createdAtEpochMillis)
        assertEquals("running-01", second.last().id)
    }

    @Test
    fun logQuickActivity_usesNutritionSpecificEntryTitle() {
        val entries = logQuickActivity(emptyList(), QuickActivityType.NUTRITION, nowEpochMillis = 100L)

        assertEquals("nutrition-01", entries.first().id)
        assertEquals("Nutrition log", entries.first().title)
    }

    @Test
    fun createNutritionLogEntry_startsWithEmptyNutritionRecord() {
        val entry = createNutritionLogEntry(
            existingEntries = emptyList(),
            nowEpochMillis = 1234L
        )

        assertEquals("nutrition-01", entry.id)
        assertEquals(1234L, entry.createdAtEpochMillis)
        assertEquals("Jij", entry.details.posterName)
        assertTrue(entry.details.photos.isEmpty())
        assertTrue(entry.details.macroMetrics.isEmpty())
        assertTrue(entry.details.microMetrics.isEmpty())
        assertTrue(entry.details.recipeSections.isEmpty())
        assertEquals("", entry.details.note)
    }

    @Test
    fun quickActivitySummary_formatsEmptySingleAndPluralStates() {
        assertEquals("Nog geen activiteiten gelogd", quickActivitySummary(emptyList()))
        assertEquals(
            "1 activiteit gelogd",
            quickActivitySummary(logQuickActivity(emptyList(), QuickActivityType.FITNESS, nowEpochMillis = 100L))
        )
        assertEquals(
            "2 activiteiten gelogd",
            quickActivitySummary(
                logQuickActivity(
                    logQuickActivity(emptyList(), QuickActivityType.FITNESS, nowEpochMillis = 100L),
                    QuickActivityType.CYCLING,
                    nowEpochMillis = 200L
                )
            )
        )
    }

    @Test
    fun buildVitalityInsights_returnsThreeOrderedInsights() {
        val insights = buildVitalityInsights(
            fitScore = 72,
            heartRateBpm = 67,
            steps = 4_800
        )

        assertEquals(3, insights.size)
        assertEquals("Herstel in balans", insights[0].title)
        assertEquals(HomeInsightTone.ACCENT, insights[0].tone)
        assertEquals("Beweging loopt", insights[1].title)
        assertEquals("Brandstof aanvullen", insights[2].title)
    }

    @Test
    fun buildVitalityInsights_flagsLowRecoveryAndLowActivity() {
        val insights = buildVitalityInsights(
            fitScore = 54,
            heartRateBpm = 79,
            steps = 1_800
        )

        assertEquals("Herstel bewaken", insights[0].title)
        assertEquals(HomeInsightTone.WARNING, insights[0].tone)
        assertEquals("Meer beweging nodig", insights[1].title)
        assertEquals("Brandstof aanvullen", insights[2].title)
    }
}
