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
    fun fallbackHeartRateTimeline_createsStableTimeline() {
        val timeline = fallbackHeartRateTimeline(
            averageBpm = 64,
            sampleCount = 2
        )

        assertEquals(6, timeline.size)
        assertEquals("06:00", timeline.first().label)
        assertEquals("Nu", timeline.last().label)
        assertEquals(67, timeline.first().bpm)
        assertEquals(62, timeline.last().bpm)
    }

    @Test
    fun heartRateDetailStats_summarizesTimeline() {
        val stats = heartRateDetailStats(
            listOf(
                HeartRateTimelinePoint("09:00", 58),
                HeartRateTimelinePoint("12:00", 64),
                HeartRateTimelinePoint("15:00", 71)
            )
        )

        assertEquals(64, stats.averageBpm)
        assertEquals(58, stats.minBpm)
        assertEquals(71, stats.maxBpm)
        assertEquals("Stabiel herstel", stats.recoveryLabel)
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
            quickActivitySummary(
                listOf(
                    QuickActivityEntry(
                        id = "fitness-1",
                        type = QuickActivityType.FITNESS,
                        title = "Fitness sessie",
                        createdAtEpochMillis = 100L,
                        durationMillis = 1_200_000L
                    )
                )
            )
        )
        assertEquals(
            "2 activiteiten gelogd",
            quickActivitySummary(
                listOf(
                    QuickActivityEntry(
                        id = "fitness-1",
                        type = QuickActivityType.FITNESS,
                        title = "Fitness sessie",
                        createdAtEpochMillis = 100L,
                        durationMillis = 1_200_000L
                    ),
                    QuickActivityEntry(
                        id = "cycling-2",
                        type = QuickActivityType.CYCLING,
                        title = "Fietsen sessie",
                        createdAtEpochMillis = 200L,
                        durationMillis = 1_800_000L
                    )
                )
            )
        )
    }

    @Test
    fun buildVitalityInsights_returnsThreeOrderedInsights() {
        val insights = buildVitalityInsights(
            fitScore = 72,
            heartRateBpm = 67,
            steps = 4_800,
            activityMinutes = 24
        )

        assertEquals(3, insights.size)
        assertEquals("Herstel in balans", insights[0].title)
        assertEquals(HomeInsightTone.ACCENT, insights[0].tone)
        assertEquals("Activiteit bouwt op", insights[1].title)
        assertEquals("Brandstof aanvullen", insights[2].title)
    }

    @Test
    fun buildVitalityInsights_flagsLowRecoveryAndLowActivity() {
        val insights = buildVitalityInsights(
            fitScore = 54,
            heartRateBpm = 79,
            steps = 1_800,
            activityMinutes = 8
        )

        assertEquals("Herstel bewaken", insights[0].title)
        assertEquals(HomeInsightTone.WARNING, insights[0].tone)
        assertEquals("Meer actieve tijd nodig", insights[1].title)
        assertEquals("Brandstof aanvullen", insights[2].title)
    }

    @Test
    fun supportsDedicatedHealthMetricDetail_onlyEnablesSleepForNow() {
        assertTrue(supportsDedicatedHealthMetricDetail("sleep"))
        assertTrue(!supportsDedicatedHealthMetricDetail("active_energy"))
        assertTrue(supportsDedicatedHealthMetricDetail("body_weight"))
    }

    @Test
    fun resolveHealthMetricValue_returnsWeightValueAndFallsBackWhenMissing() {
        val metrics = listOf(
            HomeHealthMetricCard(
                id = "body_weight",
                title = "Gewicht",
                value = "78.4 kg",
                detail = "Laatste meting",
                progress = 0.38f,
                sourceSummary = "Samsung Health",
                accent = HomeInsightTone.ACCENT
            )
        )

        assertEquals("78.4 kg", resolveHealthMetricValue(metrics, BODY_WEIGHT_HEALTH_METRIC_ID))
        assertEquals("Geen data", resolveHealthMetricValue(metrics, "unknown_metric"))
    }

    @Test
    fun weightDetailStats_summarizesTimeline() {
        val stats = weightDetailStats(
            listOf(
                WeightTimelinePoint("D-2", 79.4),
                WeightTimelinePoint("D-1", null),
                WeightTimelinePoint("Vandaag", 78.8)
            )
        )

        assertEquals(78.8, stats.latestWeightKg)
        assertEquals(78.8, stats.minWeightKg)
        assertEquals(79.4, stats.maxWeightKg)
        assertEquals(2, stats.measuredDays)
        assertEquals(-0.6, stats.changeKg)
    }
}
