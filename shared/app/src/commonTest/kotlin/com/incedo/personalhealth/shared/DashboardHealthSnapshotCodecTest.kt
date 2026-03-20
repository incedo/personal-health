package com.incedo.personalhealth.shared

import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.HomeInsightTone
import com.incedo.personalhealth.feature.home.HomeWeightChartCatalog
import com.incedo.personalhealth.feature.home.HomeWeightRange
import com.incedo.personalhealth.feature.home.HomeWeightTimeline
import com.incedo.personalhealth.feature.home.WeightTimelinePoint
import kotlin.test.Test
import kotlin.test.assertEquals

class DashboardHealthSnapshotCodecTest {
    @Test
    fun persistedSnapshot_roundTripsToUiState() {
        val snapshot = DashboardHealthSnapshot(
            uiState = DashboardHealthUiState(
                healthMetricCards = listOf(
                    HomeHealthMetricCard(
                        id = "body_weight",
                        title = "Gewicht",
                        value = "78.4 kg",
                        detail = "Laatste meting",
                        progress = 0.38f,
                        sourceSummary = "Samsung Health",
                        accent = HomeInsightTone.ACCENT
                    )
                ),
                bodyWeightCatalog = HomeWeightChartCatalog(
                    timelines = mapOf(
                        HomeWeightRange.MONTH to HomeWeightTimeline(
                            range = HomeWeightRange.MONTH,
                            title = "Laatste 30 dagen",
                            points = listOf(
                                WeightTimelinePoint("D-1", 78.9),
                                WeightTimelinePoint("Vandaag", 78.4)
                            )
                        )
                    )
                )
            ),
            updatedAtEpochMillis = 1234L,
            metricMetadata = listOf(
                PersistedHealthMetricMetadata(
                    metricId = "BODY_WEIGHT_KG",
                    recordCount = 12,
                    latestRecordEpochMillis = 1200L
                )
            )
        )

        val payload = DashboardHealthSnapshotCodec.encode(snapshot.toPersistedSnapshot())
        val restored = DashboardHealthSnapshotCodec.decode(payload)?.toUiState()

        assertEquals(snapshot.uiState.healthMetricCards, restored?.healthMetricCards)
        assertEquals(
            snapshot.uiState.bodyWeightCatalog.timelineFor(HomeWeightRange.MONTH),
            restored?.bodyWeightCatalog?.timelineFor(HomeWeightRange.MONTH)
        )
    }
}
