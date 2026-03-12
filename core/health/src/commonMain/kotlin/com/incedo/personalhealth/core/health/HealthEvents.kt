package com.incedo.personalhealth.core.health

import com.incedo.personalhealth.core.events.AppEvent

sealed interface HealthEvent : AppEvent {
    data class RecordsRead(
        val source: HealthDataSource,
        val request: HealthReadRequest,
        val count: Int,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class SyncRequested(
        val metrics: Set<HealthMetricType>,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class PermissionsRequested(
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class HealthConnectSettingsRequested(
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class LiveSyncIntentReceived(
        val source: HealthDataSource,
        val intentId: String,
        val trigger: HealthChangeTrigger,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class LiveSyncIntentSkippedDuplicate(
        val source: HealthDataSource,
        val intentId: String,
        val trigger: HealthChangeTrigger,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    data class LiveSyncIntentApplied(
        val source: HealthDataSource,
        val intentId: String,
        val trigger: HealthChangeTrigger,
        val metrics: Set<HealthMetricType>,
        val count: Int,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent

    sealed interface LiveSyncMetricIntentApplied : HealthEvent {
        val source: HealthDataSource
        val intentId: String
        val trigger: HealthChangeTrigger
        val count: Int
    }

    data class LiveSyncStepsIntentApplied(
        override val source: HealthDataSource,
        override val intentId: String,
        override val trigger: HealthChangeTrigger,
        override val count: Int,
        override val emittedAtEpochMillis: Long
    ) : LiveSyncMetricIntentApplied

    data class LiveSyncHeartRateIntentApplied(
        override val source: HealthDataSource,
        override val intentId: String,
        override val trigger: HealthChangeTrigger,
        override val count: Int,
        override val emittedAtEpochMillis: Long
    ) : LiveSyncMetricIntentApplied

    data class LiveSyncSleepIntentApplied(
        override val source: HealthDataSource,
        override val intentId: String,
        override val trigger: HealthChangeTrigger,
        override val count: Int,
        override val emittedAtEpochMillis: Long
    ) : LiveSyncMetricIntentApplied

    data class LiveSyncActiveEnergyIntentApplied(
        override val source: HealthDataSource,
        override val intentId: String,
        override val trigger: HealthChangeTrigger,
        override val count: Int,
        override val emittedAtEpochMillis: Long
    ) : LiveSyncMetricIntentApplied

    data class LiveSyncBodyWeightIntentApplied(
        override val source: HealthDataSource,
        override val intentId: String,
        override val trigger: HealthChangeTrigger,
        override val count: Int,
        override val emittedAtEpochMillis: Long
    ) : LiveSyncMetricIntentApplied

    data class LiveSyncIntentFailed(
        val source: HealthDataSource,
        val intentId: String,
        val trigger: HealthChangeTrigger,
        val reason: String,
        override val emittedAtEpochMillis: Long
    ) : HealthEvent
}
