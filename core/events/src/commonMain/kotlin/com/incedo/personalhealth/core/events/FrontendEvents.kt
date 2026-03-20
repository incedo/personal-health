package com.incedo.personalhealth.core.events

sealed interface FrontendEvent : AppEvent {
    data class UiFeedbackRequested(
        val message: String,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class NavigationChanged(
        val fromRoute: String,
        val toRoute: String,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class SyncStateChanged(
        val channel: String,
        val state: SyncState,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class TodayStepsUpdated(
        val totalSteps: Int,
        val buckets: List<StepBucket>,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class TodayHeartRateUpdated(
        val latestHeartRateBpm: Int?,
        val averageHeartRateBpm: Int?,
        val buckets: List<HeartRateBucket>,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class TodayHealthSummariesUpdated(
        val items: List<HealthSummaryItem>,
        override val emittedAtEpochMillis: Long
    ) : FrontendEvent

    data class StepBucket(
        val label: String,
        val steps: Int
    )

    data class HeartRateBucket(
        val label: String,
        val averageHeartRateBpm: Int
    )

    data class HealthSummaryItem(
        val metricId: String,
        val title: String,
        val value: String,
        val detail: String,
        val progress: Float,
        val sourceSummary: String
    )
}

enum class SyncState {
    IDLE,
    SYNCING,
    UP_TO_DATE,
    ERROR
}
