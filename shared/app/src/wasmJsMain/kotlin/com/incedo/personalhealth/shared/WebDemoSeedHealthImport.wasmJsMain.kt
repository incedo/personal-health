package com.incedo.personalhealth.shared

internal fun webDemoCanonicalImportPayload(
    dayStartEpochMillis: Long,
    nowEpochMillis: Long
): String {
    val recordsJson = buildList {
        add(metricRecordJson("steps-07", "STEPS", 1240.0, "steps", dayStartEpochMillis + 7L * HOUR_MILLIS, dayStartEpochMillis + 8L * HOUR_MILLIS, "HEALTH_CONNECT"))
        add(metricRecordJson("steps-09", "STEPS", 1875.0, "steps", dayStartEpochMillis + 9L * HOUR_MILLIS, dayStartEpochMillis + 10L * HOUR_MILLIS, "HEALTH_CONNECT"))
        add(metricRecordJson("steps-12", "STEPS", 2140.0, "steps", dayStartEpochMillis + 12L * HOUR_MILLIS, dayStartEpochMillis + 13L * HOUR_MILLIS, "HEALTH_CONNECT"))
        add(metricRecordJson("steps-18", "STEPS", 1625.0, "steps", dayStartEpochMillis + 18L * HOUR_MILLIS, dayStartEpochMillis + 19L * HOUR_MILLIS, "HEALTH_CONNECT"))
        addAll(
            listOf(61, 58, 64, 72, 69, 63).mapIndexed { index, bpm ->
                val start = dayStartEpochMillis + (6L + index * 2L) * HOUR_MILLIS + 15L * MINUTE_MILLIS
                metricRecordJson("hr-$index", "HEART_RATE_BPM", bpm.toDouble(), "bpm", start, start, "HEALTH_CONNECT")
            }
        )
        add(metricRecordJson("sleep-main", "SLEEP_DURATION_MINUTES", 463.0, "min", dayStartEpochMillis - 7L * HOUR_MILLIS - 43L * MINUTE_MILLIS, dayStartEpochMillis + 5L * MINUTE_MILLIS, "SAMSUNG_HEALTH"))
        addAll(
            listOf(120.0, 185.0, 96.0).mapIndexed { index, kcal ->
                val start = dayStartEpochMillis + (7L + index * 4L) * HOUR_MILLIS
                metricRecordJson("energy-$index", "ACTIVE_ENERGY_KCAL", kcal, "kcal", start, start + 55L * MINUTE_MILLIS, "SAMSUNG_HEALTH")
            }
        )
        add(metricRecordJson("weight-latest", "BODY_WEIGHT_KG", 78.4, "kg", dayStartEpochMillis + 6L * HOUR_MILLIS + 5L * MINUTE_MILLIS, dayStartEpochMillis + 6L * HOUR_MILLIS + 5L * MINUTE_MILLIS, "SAMSUNG_HEALTH"))
    }.joinToString(",")

    return """
        {
          "version": "1.0",
          "exportedAtEpochMillis": $nowEpochMillis,
          "window": {
            "startEpochMillis": $dayStartEpochMillis,
            "endEpochMillis": $nowEpochMillis
          },
          "records": [
            $recordsJson
          ]
        }
    """.trimIndent()
}

private fun metricRecordJson(
    id: String,
    metric: String,
    value: Double,
    unit: String,
    startEpochMillis: Long,
    endEpochMillis: Long,
    source: String
): String = """
    {
      "id": "$id",
      "metric": "$metric",
      "value": $value,
      "unit": "$unit",
      "startEpochMillis": $startEpochMillis,
      "endEpochMillis": $endEpochMillis,
      "source": "$source",
      "metadata": {
        "provider": "${source.lowercase()}"
      }
    }
""".trimIndent()

private const val MINUTE_MILLIS = 60_000L
private const val HOUR_MILLIS = 60L * MINUTE_MILLIS
