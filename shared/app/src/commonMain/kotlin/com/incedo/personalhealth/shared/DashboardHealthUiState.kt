package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthEvent
import com.incedo.personalhealth.feature.home.HeartRateTimelinePoint
import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.StepTimelinePoint
import com.incedo.personalhealth.feature.home.HomeWeightChartCatalog

internal data class DashboardHealthUiState(
    val healthMetricCards: List<HomeHealthMetricCard>,
    val bodyWeightCatalog: HomeWeightChartCatalog
)

internal fun emptyDashboardHealthUiState(): DashboardHealthUiState = DashboardHealthUiState(
    healthMetricCards = emptyList(),
    bodyWeightCatalog = HomeWeightChartCatalog(emptyMap())
)

internal fun buildDashboardHealthUiState(
    event: HealthEvent.DashboardRecordsUpdated
): DashboardHealthUiState = DashboardHealthUiState(
    healthMetricCards = buildHealthSummaryItems(
        records = event.records,
        dayStartEpochMillis = event.dayStartEpochMillis,
        dayEndEpochMillis = event.dayEndEpochMillis
    ).map { it.toHomeCard() },
    bodyWeightCatalog = buildWeightChartCatalog(
        records = event.records,
        dayStartEpochMillis = event.dayStartEpochMillis,
        dayEndEpochMillis = event.dayEndEpochMillis
    )
)

internal fun FrontendEvent.TodayStepsUpdated.toStepTimelinePoints(): List<StepTimelinePoint> =
    buckets.map { bucket ->
        StepTimelinePoint(
            label = bucket.label,
            steps = bucket.steps
        )
    }

internal fun FrontendEvent.TodayHeartRateUpdated.toHeartRateTimelinePoints(): List<HeartRateTimelinePoint> =
    buckets
        .filter { it.averageHeartRateBpm > 0 }
        .map { bucket ->
            HeartRateTimelinePoint(
                label = bucket.label,
                bpm = bucket.averageHeartRateBpm
            )
        }
