package com.incedo.personalhealth.core.health

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import kotlinx.coroutines.CoroutineScope

enum class HealthChangeTrigger {
    PLATFORM_OBSERVER,
    POLL_TICK,
    MANUAL
}

data class HealthChangeSignal(
    val intentId: String,
    val source: HealthDataSource,
    val trigger: HealthChangeTrigger,
    val emittedAtEpochMillis: Long = currentEpochMillis()
)

fun interface HealthSignalSubscription {
    fun stop()
}

interface HealthChangeSignalSource {
    fun start(
        scope: CoroutineScope,
        onSignal: (HealthChangeSignal) -> Unit
    ): HealthSignalSubscription
}

class HealthLiveSyncProcessor(
    private val gateway: HealthDataGateway,
    private val source: HealthDataSource,
    private val checkpointStore: HealthSyncCheckpointStore = InMemoryHealthSyncCheckpointStore(),
    private val eventBus: AppEventBus? = null
) {
    suspend fun processSignal(
        signal: HealthChangeSignal,
        metrics: Set<HealthMetricType>,
        lookbackMillis: Long,
        limit: Int = DEFAULT_HEALTH_IMPORT_BATCH_LIMIT
    ): List<HealthRecord> {
        require(metrics.isNotEmpty()) { "metrics must not be empty" }
        require(lookbackMillis > 0) { "lookbackMillis must be > 0" }
        require(limit > 0) { "limit must be > 0" }
        require(signal.source == source) { "signal source must match processor source" }
        require(signal.intentId.isNotBlank()) { "intentId must not be blank" }

        eventBus?.publish(
            HealthEvent.LiveSyncIntentReceived(
                source = source,
                intentId = signal.intentId,
                trigger = signal.trigger,
                emittedAtEpochMillis = nowMillis()
            )
        )

        val checkpointKey = checkpointKey(signal.intentId)
        if (checkpointStore.hasProcessed(checkpointKey)) {
            eventBus?.publish(
                HealthEvent.LiveSyncIntentSkippedDuplicate(
                    source = source,
                    intentId = signal.intentId,
                    trigger = signal.trigger,
                    emittedAtEpochMillis = nowMillis()
                )
            )
            return emptyList()
        }

        val channel = "health-live-sync:${source.name.lowercase()}"
        eventBus?.publish(
            FrontendEvent.SyncStateChanged(
                channel = channel,
                state = SyncState.SYNCING,
                emittedAtEpochMillis = nowMillis()
            )
        )

        val endEpochMillis = nowMillis()
        val request = HealthReadRequest(
            metrics = metrics,
            startEpochMillis = endEpochMillis - lookbackMillis,
            endEpochMillis = endEpochMillis,
            limit = limit
        )

        return runCatching {
            val records = gateway.readRecords(request)
            eventBus?.publish(
                HealthEvent.LiveSyncIntentApplied(
                    source = source,
                    intentId = signal.intentId,
                    trigger = signal.trigger,
                    metrics = metrics,
                    count = records.size,
                    emittedAtEpochMillis = nowMillis()
                )
            )
            publishMetricIntentEvents(
                signal = signal,
                records = records
            )
            eventBus?.publish(
                FrontendEvent.SyncStateChanged(
                    channel = channel,
                    state = SyncState.UP_TO_DATE,
                    emittedAtEpochMillis = nowMillis()
                )
            )
            checkpointStore.markProcessed(
                checkpointKey = checkpointKey,
                processedAtEpochMillis = nowMillis()
            )
            records
        }.getOrElse { error ->
            eventBus?.publish(
                HealthEvent.LiveSyncIntentFailed(
                    source = source,
                    intentId = signal.intentId,
                    trigger = signal.trigger,
                    reason = error.message ?: "unknown",
                    emittedAtEpochMillis = nowMillis()
                )
            )
            eventBus?.publish(
                FrontendEvent.SyncStateChanged(
                    channel = channel,
                    state = SyncState.ERROR,
                    emittedAtEpochMillis = nowMillis()
                )
            )
            throw error
        }
    }

    private fun nowMillis(): Long = currentEpochMillis()
    private fun checkpointKey(intentId: String): String = "${source.name}:$intentId"

    private suspend fun publishMetricIntentEvents(
        signal: HealthChangeSignal,
        records: List<HealthRecord>
    ) {
        val countsByMetric = records
            .groupingBy { it.metric }
            .eachCount()

        countsByMetric.forEach { (metric, count) ->
            if (count <= 0) return@forEach
            val eventFactory = metricIntentEventFactories[metric] ?: return@forEach
            eventBus?.publish(
                eventFactory(
                    MetricIntentEventContext(
                        source = source,
                        intentId = signal.intentId,
                        trigger = signal.trigger,
                        count = count,
                        emittedAtEpochMillis = nowMillis()
                    )
                )
            )
        }
    }

    private data class MetricIntentEventContext(
        val source: HealthDataSource,
        val intentId: String,
        val trigger: HealthChangeTrigger,
        val count: Int,
        val emittedAtEpochMillis: Long
    )

    private companion object {
        val metricIntentEventFactories:
            Map<HealthMetricType, (MetricIntentEventContext) -> HealthEvent.LiveSyncMetricIntentApplied> =
            mapOf(
                HealthMetricType.STEPS to { ctx ->
                    HealthEvent.LiveSyncStepsIntentApplied(
                        source = ctx.source,
                        intentId = ctx.intentId,
                        trigger = ctx.trigger,
                        count = ctx.count,
                        emittedAtEpochMillis = ctx.emittedAtEpochMillis
                    )
                },
                HealthMetricType.HEART_RATE_BPM to { ctx ->
                    HealthEvent.LiveSyncHeartRateIntentApplied(
                        source = ctx.source,
                        intentId = ctx.intentId,
                        trigger = ctx.trigger,
                        count = ctx.count,
                        emittedAtEpochMillis = ctx.emittedAtEpochMillis
                    )
                },
                HealthMetricType.SLEEP_DURATION_MINUTES to { ctx ->
                    HealthEvent.LiveSyncSleepIntentApplied(
                        source = ctx.source,
                        intentId = ctx.intentId,
                        trigger = ctx.trigger,
                        count = ctx.count,
                        emittedAtEpochMillis = ctx.emittedAtEpochMillis
                    )
                },
                HealthMetricType.ACTIVE_ENERGY_KCAL to { ctx ->
                    HealthEvent.LiveSyncActiveEnergyIntentApplied(
                        source = ctx.source,
                        intentId = ctx.intentId,
                        trigger = ctx.trigger,
                        count = ctx.count,
                        emittedAtEpochMillis = ctx.emittedAtEpochMillis
                    )
                },
                HealthMetricType.BODY_WEIGHT_KG to { ctx ->
                    HealthEvent.LiveSyncBodyWeightIntentApplied(
                        source = ctx.source,
                        intentId = ctx.intentId,
                        trigger = ctx.trigger,
                        count = ctx.count,
                        emittedAtEpochMillis = ctx.emittedAtEpochMillis
                    )
                }
            )
    }
}
