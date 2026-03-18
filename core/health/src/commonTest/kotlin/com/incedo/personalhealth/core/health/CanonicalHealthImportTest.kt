package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
