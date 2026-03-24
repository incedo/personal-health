package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.shared.publishDashboardHealthEvents
import java.time.Instant
import java.time.ZoneId

suspend fun publishTodayDashboardMetrics(
    primaryGateway: HealthDataGateway? = null,
    fallbackGateway: HealthDataGateway? = null,
    eventBus: AppEventBus,
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
) {
    val startOfDay = now.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant()
    val startEpochMillis = startOfDay.toEpochMilli()
    val endEpochMillis = now.toEpochMilli()
    val request = HealthReadRequest(
        metrics = dashboardWindowMetrics,
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
        limit = 10_000
    )
    val bodyMetricRequest = HealthReadRequest(
        metrics = dashboardHistoricalMetrics,
        startEpochMillis = 0L,
        endEpochMillis = endEpochMillis,
        limit = 5_000
    )
    val records = buildList {
        if (primaryGateway != null) {
            addAll(readDashboardRecords(primaryGateway, request))
            addAll(readDashboardRecords(primaryGateway, bodyMetricRequest))
        }
        if (fallbackGateway != null) {
            addAll(readDashboardRecords(fallbackGateway, request))
            addAll(readDashboardRecords(fallbackGateway, bodyMetricRequest))
        }
    }.distinctBy { it.id }

    publishDashboardHealthEvents(
        records = records,
        dayStartEpochMillis = startEpochMillis,
        dayEndEpochMillis = endEpochMillis,
        emittedAtEpochMillis = endEpochMillis,
        eventBus = eventBus
    )
}

private suspend fun readDashboardRecords(
    gateway: HealthDataGateway,
    request: HealthReadRequest
) = runCatching {
    gateway.readRecords(request)
}.getOrElse {
    emptyList()
}
