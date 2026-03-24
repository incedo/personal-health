package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthSummaryItemsTest {

    @Test
    fun buildHealthSummaryItems_includesExpandedSamsungBodyMetrics() {
        val items = buildHealthSummaryItems(
            records = listOf(
                healthRecord(HealthMetricType.BODY_FAT_MASS_KG, 18.4, 100L),
                healthRecord(HealthMetricType.MUSCLE_MASS_KG, 31.2, 200L),
                healthRecord(HealthMetricType.MUSCLE_PERCENTAGE, 42.0, 200L),
                healthRecord(HealthMetricType.SKELETAL_MUSCLE_PERCENTAGE, 38.0, 200L),
                healthRecord(HealthMetricType.FAT_FREE_PERCENTAGE, 78.0, 200L),
                healthRecord(HealthMetricType.FAT_FREE_MASS_KG, 60.1, 200L),
                healthRecord(HealthMetricType.MEAN_BLOOD_PRESSURE_MMHG, 91.0, 300L),
                healthRecord(HealthMetricType.PULSE_RATE_BPM, 63.0, 300L)
            ),
            dayStartEpochMillis = 0L,
            dayEndEpochMillis = 1_000L
        )

        assertTrue(items.any { it.metricId == "body_fat_mass" && it.value == "18.4 kg" })
        assertTrue(items.any { it.metricId == "muscle_mass" && it.value == "31.2 kg" })
        assertTrue(items.any { it.metricId == "muscle_percentage" && it.value == "42 %" })
        assertTrue(items.any { it.metricId == "skeletal_muscle_percentage" && it.value == "38 %" })
        assertTrue(items.any { it.metricId == "fat_free_percentage" && it.value == "78 %" })
        assertTrue(items.any { it.metricId == "fat_free_mass" && it.value == "60.1 kg" })
        assertTrue(items.any { it.metricId == "blood_pressure_mean" && it.value == "91 mmHg" })
        assertTrue(items.any { it.metricId == "pulse_rate" && it.value == "63 bpm" })
    }

    @Test
    fun buildHealthSummaryItems_aggregatesExpandedSamsungNutritionMetrics() {
        val items = buildHealthSummaryItems(
            records = listOf(
                healthRecord(HealthMetricType.PROTEIN_G, 24.2, 100L),
                healthRecord(HealthMetricType.PROTEIN_G, 11.3, 200L),
                healthRecord(HealthMetricType.CARBOHYDRATE_G, 40.0, 100L),
                healthRecord(HealthMetricType.CARBOHYDRATE_G, 25.5, 200L),
                healthRecord(HealthMetricType.SODIUM_MG, 300.0, 100L),
                healthRecord(HealthMetricType.SODIUM_MG, 420.0, 200L),
                healthRecord(HealthMetricType.VITAMIN_A_MCG, 550.0, 100L),
                healthRecord(HealthMetricType.VITAMIN_C_MG, 82.0, 200L)
            ),
            dayStartEpochMillis = 0L,
            dayEndEpochMillis = 1_000L
        )

        assertEquals("35.5 g", items.first { it.metricId == "protein" }.value)
        assertEquals("Binnen venster totaal", items.first { it.metricId == "protein" }.detail)
        assertEquals("65.5 g", items.first { it.metricId == "carbohydrate" }.value)
        assertEquals("720 mg", items.first { it.metricId == "sodium" }.value)
        assertEquals("550 mcg", items.first { it.metricId == "vitamin_a" }.value)
        assertEquals("82 mg", items.first { it.metricId == "vitamin_c" }.value)
    }

    private fun healthRecord(
        metric: HealthMetricType,
        value: Double,
        epochMillis: Long
    ) = HealthRecord(
        id = "${metric.metricId}-$epochMillis",
        metric = metric,
        value = value,
        startEpochMillis = epochMillis,
        endEpochMillis = epochMillis,
        source = HealthDataSource.SAMSUNG_HEALTH
    )
}
