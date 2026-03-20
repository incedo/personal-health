package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CanonicalHealthImportTest {

    @Test
    fun parseCanonicalHealthImportDocument_mapsCanonicalRecords() {
        val document = parseCanonicalHealthImportDocument(
            """
            {
              "version": "1",
              "exportedAtEpochMillis": 1710000000000,
              "window": {
                "startEpochMillis": 1710028800000,
                "endEpochMillis": 1710115199000
              },
              "records": [
                {
                  "id": "steps-09",
                  "metric": "STEPS",
                  "value": 1240,
                  "unit": "count",
                  "startEpochMillis": 1710061200000,
                  "endEpochMillis": 1710064799000,
                  "source": "UNKNOWN",
                  "metadata": {
                    "origin": "csv-import",
                    "label": "09:00"
                  }
                }
              ]
            }
            """.trimIndent()
        )

        assertEquals("1", document.version)
        assertEquals(1710028800000, document.window.startEpochMillis)
        assertEquals(1, document.records.size)
        assertEquals("steps-09", document.records.single().id)
        assertEquals(HealthMetricType.STEPS, document.records.single().metric)
        assertEquals("csv-import", document.records.single().metadata["origin"])
    }

    @Test
    fun parseWithingsWeightCsvImport_mapsWeightRecords() {
        val document = parseWithingsWeightCsvImport(
            """
            Date,Time,Weight (kg),Fat mass (%),Systolic blood pressure (mmHg),Diastolic blood pressure (mmHg)
            2024-03-10,07:15:00,79.6,18.2,121,79
            2024-03-12,07:10:00,79.2,18.0,118,76
            """.trimIndent()
        )

        assertEquals("withings-health-csv-v1", document.version)
        assertEquals(8, document.records.size)
        assertTrue(document.records.any { it.metric == HealthMetricType.BODY_WEIGHT_KG })
        assertTrue(document.records.all { it.source == HealthDataSource.WITHINGS })
        assertEquals("withings-csv-import", document.records.first().metadata["origin"])
        assertTrue(document.records.any { it.metric == HealthMetricType.BODY_FAT_PERCENTAGE })
        assertTrue(document.records.any { it.metric == HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG })
        assertTrue(document.records.any { it.metric == HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG })
        assertTrue(document.window.startEpochMillis <= document.records.last().endEpochMillis)
        assertTrue(document.window.endEpochMillis >= document.records.last().endEpochMillis)
    }
}
