package com.incedo.personalhealth.core.health

import com.incedo.personalhealth.core.events.AppEvent
import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest

class HealthLiveSyncProcessorTest {

    @Test
    fun processSignal_rejectsInvalidInput() = runTest {
        val processor = HealthLiveSyncProcessor(
            gateway = FakeGateway(records = emptyList()),
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = RecordingAppEventBus()
        )

        assertFailsWith<IllegalArgumentException> {
            processor.processSignal(
                signal = HealthChangeSignal(
                    intentId = "i1",
                    source = HealthDataSource.HEALTH_CONNECT,
                    trigger = HealthChangeTrigger.MANUAL
                ),
                metrics = emptySet(),
                lookbackMillis = 1000L
            )
        }
        assertFailsWith<IllegalArgumentException> {
            processor.processSignal(
                signal = HealthChangeSignal(
                    intentId = "",
                    source = HealthDataSource.HEALTH_CONNECT,
                    trigger = HealthChangeTrigger.MANUAL
                ),
                metrics = setOf(HealthMetricType.STEPS),
                lookbackMillis = 1000L
            )
        }
        assertFailsWith<IllegalArgumentException> {
            processor.processSignal(
                signal = HealthChangeSignal(
                    intentId = "i2",
                    source = HealthDataSource.HEALTHKIT,
                    trigger = HealthChangeTrigger.MANUAL
                ),
                metrics = setOf(HealthMetricType.STEPS),
                lookbackMillis = 1000L
            )
        }
    }

    @Test
    fun processSignal_publishesTypeSpecificMetricEvent() = runTest {
        val fakeBus = RecordingAppEventBus()
        val fakeGateway = FakeGateway(
            records = listOf(
                HealthRecord(
                    id = "record-1",
                    metric = HealthMetricType.STEPS,
                    value = 1234.0,
                    unit = "count",
                    startEpochMillis = 1_000,
                    endEpochMillis = 2_000,
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        )
        val processor = HealthLiveSyncProcessor(
            gateway = fakeGateway,
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = fakeBus
        )

        val result = processor.processSignal(
            signal = HealthChangeSignal(
                intentId = "intent-1",
                source = HealthDataSource.HEALTH_CONNECT,
                trigger = HealthChangeTrigger.POLL_TICK
            ),
            metrics = setOf(HealthMetricType.STEPS),
            lookbackMillis = 60_000,
            limit = 10
        )

        assertEquals(1, result.size)
        assertEquals(5, fakeBus.recordedEvents.size)
        val received = fakeBus.recordedEvents[0] as HealthEvent.LiveSyncIntentReceived
        val syncStart = fakeBus.recordedEvents[1] as FrontendEvent.SyncStateChanged
        val changed = fakeBus.recordedEvents[2] as HealthEvent.LiveSyncIntentApplied
        val typedMetricEvent = fakeBus.recordedEvents[3] as HealthEvent.LiveSyncStepsIntentApplied
        val syncEnd = fakeBus.recordedEvents[4] as FrontendEvent.SyncStateChanged
        assertEquals("intent-1", received.intentId)
        assertEquals(SyncState.SYNCING, syncStart.state)
        assertEquals(HealthChangeTrigger.POLL_TICK, changed.trigger)
        assertEquals(1, changed.count)
        assertEquals(1, typedMetricEvent.count)
        assertEquals(SyncState.UP_TO_DATE, syncEnd.state)
    }

    @Test
    fun processSignal_publishesMultipleTypeEventsWhenMetricsDiffer() = runTest {
        val fakeBus = RecordingAppEventBus()
        val fakeGateway = FakeGateway(
            records = listOf(
                HealthRecord(
                    id = "steps-1",
                    metric = HealthMetricType.STEPS,
                    value = 10.0,
                    unit = "count",
                    startEpochMillis = 1_000,
                    endEpochMillis = 2_000,
                    source = HealthDataSource.HEALTH_CONNECT
                ),
                HealthRecord(
                    id = "hr-1",
                    metric = HealthMetricType.HEART_RATE_BPM,
                    value = 60.0,
                    unit = "bpm",
                    startEpochMillis = 1_000,
                    endEpochMillis = 2_000,
                    source = HealthDataSource.HEALTH_CONNECT
                )
            )
        )
        val processor = HealthLiveSyncProcessor(
            gateway = fakeGateway,
            source = HealthDataSource.HEALTH_CONNECT,
            eventBus = fakeBus
        )

        processor.processSignal(
            signal = HealthChangeSignal(
                intentId = "intent-multi",
                source = HealthDataSource.HEALTH_CONNECT,
                trigger = HealthChangeTrigger.POLL_TICK
            ),
            metrics = setOf(HealthMetricType.STEPS, HealthMetricType.HEART_RATE_BPM),
            lookbackMillis = 60_000,
            limit = 10
        )

        val typedEvents = fakeBus.recordedEvents.filterIsInstance<HealthEvent.LiveSyncMetricIntentApplied>()
        assertEquals(2, typedEvents.size)
        assertTrue(typedEvents.any { it is HealthEvent.LiveSyncStepsIntentApplied })
        assertTrue(typedEvents.any { it is HealthEvent.LiveSyncHeartRateIntentApplied })
    }

    @Test
    fun processSignal_skipsAlreadyProcessedIntent() = runTest {
        val fakeBus = RecordingAppEventBus()
        val checkpointStore = InMemoryHealthSyncCheckpointStore()
        val processor = HealthLiveSyncProcessor(
            gateway = FakeGateway(records = emptyList()),
            source = HealthDataSource.HEALTH_CONNECT,
            checkpointStore = checkpointStore,
            eventBus = fakeBus
        )
        checkpointStore.markProcessed("HEALTH_CONNECT:intent-duplicate", 1L)

        val result = processor.processSignal(
            signal = HealthChangeSignal(
                intentId = "intent-duplicate",
                source = HealthDataSource.HEALTH_CONNECT,
                trigger = HealthChangeTrigger.POLL_TICK
            ),
            metrics = setOf(HealthMetricType.STEPS),
            lookbackMillis = 60_000
        )

        assertEquals(0, result.size)
        assertEquals(2, fakeBus.recordedEvents.size)
        val received = fakeBus.recordedEvents[0] as HealthEvent.LiveSyncIntentReceived
        val skipped = fakeBus.recordedEvents[1] as HealthEvent.LiveSyncIntentSkippedDuplicate
        assertEquals("intent-duplicate", received.intentId)
        assertEquals("intent-duplicate", skipped.intentId)
    }

    private class FakeGateway(
        private val records: List<HealthRecord>
    ) : HealthDataGateway {
        override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> = records
    }

    private class RecordingAppEventBus : AppEventBus {
        private val eventsFlow = MutableSharedFlow<AppEvent>(extraBufferCapacity = 32)
        override val events: Flow<AppEvent> = eventsFlow.asSharedFlow()
        val recordedEvents = mutableListOf<AppEvent>()

        override suspend fun publish(event: AppEvent) {
            recordedEvents += event
            eventsFlow.emit(event)
        }
    }
}
