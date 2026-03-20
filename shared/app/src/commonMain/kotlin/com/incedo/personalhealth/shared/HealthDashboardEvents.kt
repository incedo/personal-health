package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.core.health.HealthRecord
import com.incedo.personalhealth.core.health.buildTodayHeartRateSnapshot
import com.incedo.personalhealth.core.health.buildTodayStepsSnapshot

suspend fun publishDashboardHealthEvents(
    records: List<HealthRecord>,
    dayStartEpochMillis: Long,
    dayEndEpochMillis: Long,
    emittedAtEpochMillis: Long,
    eventBus: AppEventBus
) {
    eventBus.publish(
        HealthEvent.DashboardRecordsUpdated(
            records = records,
            dayStartEpochMillis = dayStartEpochMillis,
            dayEndEpochMillis = dayEndEpochMillis,
            emittedAtEpochMillis = emittedAtEpochMillis
        )
    )

    val stepSnapshot = buildTodayStepsSnapshot(
        records = records,
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    eventBus.publish(
        FrontendEvent.TodayStepsUpdated(
            totalSteps = stepSnapshot.totalSteps,
            buckets = stepSnapshot.buckets.map { bucket ->
                FrontendEvent.StepBucket(
                    label = bucket.label,
                    steps = bucket.steps
                )
            },
            emittedAtEpochMillis = emittedAtEpochMillis
        )
    )

    val heartRateSnapshot = buildTodayHeartRateSnapshot(
        records = records,
        dayStartEpochMillis = dayStartEpochMillis,
        dayEndEpochMillis = dayEndEpochMillis,
        bucketSizeHours = 1
    )
    eventBus.publish(
        FrontendEvent.TodayHeartRateUpdated(
            latestHeartRateBpm = heartRateSnapshot.latestHeartRateBpm,
            averageHeartRateBpm = heartRateSnapshot.averageHeartRateBpm,
            buckets = heartRateSnapshot.buckets.map { bucket ->
                FrontendEvent.HeartRateBucket(
                    label = bucket.label,
                    averageHeartRateBpm = bucket.averageHeartRateBpm
                )
            },
            emittedAtEpochMillis = emittedAtEpochMillis
        )
    )
    eventBus.publish(
        FrontendEvent.TodayHealthSummariesUpdated(
            items = buildHealthSummaryItems(
                records = records,
                dayStartEpochMillis = dayStartEpochMillis,
                dayEndEpochMillis = dayEndEpochMillis
            ),
            emittedAtEpochMillis = emittedAtEpochMillis
        )
    )
}
