package com.incedo.personalhealth.core.health

import com.incedo.personalhealth.core.events.AppEvent
import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest

class HealthHistoryImporterTest {

    @Test
    fun import_readsBatchesAndPublishesSyncStartAndEnd() = runTest {
        val gateway = RecordingGateway(
            recordsByWindow = mapOf(
                0L to listOf(record("a", 0L, 100L)),
                100L to listOf(record("b", 100L, 200L)),
                200L to listOf(record("c", 200L, 300L))
            )
        )
        val bus = RecordingEventBus()
        val importer = HealthHistoryImporter(
            gateway = gateway,
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = bus
        )

        val result = importer.import(
            HealthHistoryImportRequest(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = 0L,
                endEpochMillis = 300L,
                batchWindowMillis = 100L,
                batchLimit = 10
            )
        )

        assertEquals(3, gateway.requests.size)
        assertEquals(3, result.size)
        val states = bus.publishedEvents.filterIsInstance<FrontendEvent.SyncStateChanged>().map { it.state }
        assertEquals(listOf(SyncState.SYNCING, SyncState.UP_TO_DATE), states)
    }

    @Test
    fun import_publishesErrorStateWhenGatewayFails() = runTest {
        val gateway = RecordingGateway(
            recordsByWindow = emptyMap(),
            failAtWindowStart = 100L
        )
        val bus = RecordingEventBus()
        val importer = HealthHistoryImporter(
            gateway = gateway,
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = bus
        )

        assertFailsWith<IllegalStateException> {
            importer.import(
                HealthHistoryImportRequest(
                    metrics = setOf(HealthMetricType.STEPS),
                    startEpochMillis = 0L,
                    endEpochMillis = 300L,
                    batchWindowMillis = 100L,
                    batchLimit = 10
                )
            )
        }

        val states = bus.publishedEvents.filterIsInstance<FrontendEvent.SyncStateChanged>().map { it.state }
        assertEquals(listOf(SyncState.SYNCING, SyncState.ERROR), states)
    }

    @Test
    fun request_validation_rejectsInvalidArguments() {
        assertFailsWith<IllegalArgumentException> {
            HealthHistoryImportRequest(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = 10L,
                endEpochMillis = 0L
            )
        }
        assertFailsWith<IllegalArgumentException> {
            HealthHistoryImportRequest(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = 0L,
                endEpochMillis = 10L,
                batchWindowMillis = 0L
            )
        }
        assertFailsWith<IllegalArgumentException> {
            HealthHistoryImportRequest(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = 0L,
                endEpochMillis = 10L,
                batchLimit = 0
            )
        }
    }

    private class RecordingGateway(
        private val recordsByWindow: Map<Long, List<HealthRecord>>,
        private val failAtWindowStart: Long? = null
    ) : HealthDataGateway {
        val requests = mutableListOf<HealthReadRequest>()

        override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
            requests += request
            if (request.startEpochMillis == failAtWindowStart) {
                error("boom")
            }
            return recordsByWindow[request.startEpochMillis].orEmpty()
        }
    }

    private class RecordingEventBus : AppEventBus {
        private val flow = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)
        override val events: Flow<AppEvent> = flow.asSharedFlow()
        val publishedEvents = mutableListOf<AppEvent>()

        override suspend fun publish(event: AppEvent) {
            publishedEvents += event
            flow.emit(event)
        }
    }

    private fun record(id: String, start: Long, end: Long) = HealthRecord(
        id = id,
        metric = HealthMetricType.STEPS,
        value = 1.0,
        unit = "count",
        startEpochMillis = start,
        endEpochMillis = end,
        source = HealthDataSource.HEALTH_CONNECT
    )
}
