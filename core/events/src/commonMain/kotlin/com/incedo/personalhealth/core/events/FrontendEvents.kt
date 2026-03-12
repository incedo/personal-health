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

    data class StepBucket(
        val label: String,
        val steps: Int
    )
}

enum class SyncState {
    IDLE,
    SYNCING,
    UP_TO_DATE,
    ERROR
}
