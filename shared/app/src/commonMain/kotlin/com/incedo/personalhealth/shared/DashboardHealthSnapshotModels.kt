package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.HomeInsightTone
import com.incedo.personalhealth.feature.home.HomeWeightChartCatalog
import com.incedo.personalhealth.feature.home.HomeWeightRange
import com.incedo.personalhealth.feature.home.HomeWeightTimeline
import com.incedo.personalhealth.feature.home.WeightTimelinePoint
import kotlinx.serialization.Serializable

@Serializable
internal data class PersistedDashboardHealthSnapshot(
    val schemaVersion: Int = DASHBOARD_HEALTH_SNAPSHOT_SCHEMA_VERSION,
    val updatedAtEpochMillis: Long,
    val cards: List<PersistedHealthMetricCard> = emptyList(),
    val weightCatalog: List<PersistedWeightTimeline> = emptyList(),
    val metricMetadata: List<PersistedHealthMetricMetadata> = emptyList()
)

@Serializable
internal data class PersistedHealthMetricCard(
    val id: String,
    val title: String,
    val value: String,
    val detail: String,
    val progress: Float,
    val sourceSummary: String,
    val accent: String
)

@Serializable
internal data class PersistedWeightTimeline(
    val range: String,
    val title: String,
    val points: List<PersistedWeightTimelinePoint> = emptyList()
)

@Serializable
internal data class PersistedWeightTimelinePoint(
    val label: String,
    val weightKg: Double? = null
)

@Serializable
internal data class PersistedHealthMetricMetadata(
    val metricId: String,
    val recordCount: Int,
    val latestRecordEpochMillis: Long? = null
)

internal const val DASHBOARD_HEALTH_SNAPSHOT_SCHEMA_VERSION = 1

internal fun PersistedDashboardHealthSnapshot.toUiState(): DashboardHealthUiState = DashboardHealthUiState(
    healthMetricCards = cards.map { card ->
        com.incedo.personalhealth.feature.home.HomeHealthMetricCard(
            id = card.id,
            title = card.title,
            value = card.value,
            detail = card.detail,
            progress = card.progress,
            sourceSummary = card.sourceSummary,
            accent = HomeInsightTone.entries.firstOrNull { it.name == card.accent } ?: HomeInsightTone.ACCENT
        )
    },
    bodyWeightCatalog = HomeWeightChartCatalog(
        timelines = weightCatalog.associate { timeline ->
            val range = HomeWeightRange.entries.firstOrNull { it.name == timeline.range } ?: HomeWeightRange.MONTH
            range to HomeWeightTimeline(
                range = range,
                title = timeline.title,
                points = timeline.points.map { point ->
                    WeightTimelinePoint(
                        label = point.label,
                        weightKg = point.weightKg
                    )
                }
            )
        }
    )
)

internal fun DashboardHealthSnapshot.toPersistedSnapshot(): PersistedDashboardHealthSnapshot = PersistedDashboardHealthSnapshot(
    updatedAtEpochMillis = updatedAtEpochMillis,
    cards = uiState.healthMetricCards.map { card ->
        PersistedHealthMetricCard(
            id = card.id,
            title = card.title,
            value = card.value,
            detail = card.detail,
            progress = card.progress,
            sourceSummary = card.sourceSummary,
            accent = card.accent.name
        )
    },
    weightCatalog = uiState.bodyWeightCatalog.timelines.values.map { timeline ->
        PersistedWeightTimeline(
            range = timeline.range.name,
            title = timeline.title,
            points = timeline.points.map { point ->
                PersistedWeightTimelinePoint(
                    label = point.label,
                    weightKg = point.weightKg
                )
            }
        )
    },
    metricMetadata = metricMetadata
)
