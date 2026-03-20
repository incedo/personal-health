package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthRecord

internal fun healthSourceSummary(
    records: List<HealthRecord>,
    metric: HealthMetricType
): String {
    val labels = preferredMetricRecords(records, metric)
        .asSequence()
        .map { it.source }
        .distinct()
        .map(::healthSourceLabel)
        .toList()

    return when (labels.size) {
        0 -> "Bron onbekend"
        1 -> labels.first()
        else -> labels.joinToString(" + ")
    }
}

internal fun healthSourceLabel(source: HealthDataSource): String = when (source) {
    HealthDataSource.HEALTH_CONNECT -> "Health Connect"
    HealthDataSource.SAMSUNG_HEALTH -> "Samsung Health"
    HealthDataSource.WITHINGS -> "Withings"
    HealthDataSource.HEALTHKIT -> "HealthKit"
    HealthDataSource.UNKNOWN -> "Onbekend"
}
