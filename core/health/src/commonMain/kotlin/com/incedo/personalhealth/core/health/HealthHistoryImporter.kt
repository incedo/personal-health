package com.incedo.personalhealth.core.health

import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState

const val DEFAULT_HEALTH_IMPORT_BATCH_LIMIT: Int = 1_000
const val DEFAULT_HEALTH_IMPORT_BATCH_WINDOW_MILLIS: Long = 7L * 24L * 60L * 60L * 1000L

data class HealthHistoryImportRequest(
    val metrics: Set<HealthMetricType>,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val batchWindowMillis: Long = DEFAULT_HEALTH_IMPORT_BATCH_WINDOW_MILLIS,
    val batchLimit: Int = DEFAULT_HEALTH_IMPORT_BATCH_LIMIT
) {
    init {
        require(startEpochMillis <= endEpochMillis) {
            "startEpochMillis must be before or equal to endEpochMillis"
        }
        require(batchWindowMillis > 0) { "batchWindowMillis must be > 0" }
        require(batchLimit > 0) { "batchLimit must be > 0" }
    }
}

class HealthHistoryImporter(
    private val gateway: HealthDataGateway,
    private val source: HealthDataSource,
    private val eventBus: AppEventBus? = null
) {
    suspend fun import(request: HealthHistoryImportRequest): List<HealthRecord> {
        val channel = "health-history-import:${source.name.lowercase()}"
        eventBus?.publish(
            FrontendEvent.SyncStateChanged(
                channel = channel,
                state = SyncState.SYNCING,
                emittedAtEpochMillis = nowMillis()
            )
        )

        val importedRecords = mutableListOf<HealthRecord>()
        var windowStart = request.startEpochMillis

        try {
            while (windowStart < request.endEpochMillis) {
                val windowEnd = minOf(windowStart + request.batchWindowMillis, request.endEpochMillis)
                importedRecords += gateway.readRecords(
                    HealthReadRequest(
                        metrics = request.metrics,
                        startEpochMillis = windowStart,
                        endEpochMillis = windowEnd,
                        limit = request.batchLimit
                    )
                )
                windowStart = windowEnd
            }

            eventBus?.publish(
                FrontendEvent.SyncStateChanged(
                    channel = channel,
                    state = SyncState.UP_TO_DATE,
                    emittedAtEpochMillis = nowMillis()
                )
            )
            return importedRecords
        } catch (error: Throwable) {
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

    companion object {
        const val DEFAULT_BATCH_LIMIT: Int = DEFAULT_HEALTH_IMPORT_BATCH_LIMIT
        const val DEFAULT_BATCH_WINDOW_MILLIS: Long = DEFAULT_HEALTH_IMPORT_BATCH_WINDOW_MILLIS
    }
}
