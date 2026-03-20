package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthHistoryImportRequest
import com.incedo.personalhealth.core.health.HealthHistoryImporter
import com.incedo.personalhealth.core.health.HealthMetricType

internal suspend fun importHealthHistory(
    gateway: HealthDataGateway,
    source: HealthDataSource,
    metrics: Set<HealthMetricType>,
    nowEpochMillis: Long,
    eventBus: AppEventBus,
    oneYearMillis: Long
): Int {
    if (metrics.isEmpty()) return 0
    val importer = HealthHistoryImporter(
        gateway = gateway,
        source = source,
        eventBus = eventBus
    )
    return importer.import(
        HealthHistoryImportRequest(
            metrics = metrics,
            startEpochMillis = nowEpochMillis - oneYearMillis,
            endEpochMillis = nowEpochMillis
        )
    ).size
}
