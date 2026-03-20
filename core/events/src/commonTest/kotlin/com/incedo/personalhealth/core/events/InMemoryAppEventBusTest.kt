package com.incedo.personalhealth.core.events

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryAppEventBusTest {

    @Test
    fun publish_emitsEventToSubscribers() = runTest {
        val bus = InMemoryAppEventBus()
        val event = FrontendEvent.UiFeedbackRequested(
            message = "ok",
            emittedAtEpochMillis = 42L
        )

        val received = async { bus.events.first() }
        runCurrent()
        bus.publish(event)

        assertEquals(event, received.await())
    }

    @Test
    fun publish_preservesEventOrdering() = runTest {
        val bus = InMemoryAppEventBus()
        val first = FrontendEvent.NavigationChanged(
            fromRoute = "a",
            toRoute = "b",
            emittedAtEpochMillis = 1L
        )
        val second = FrontendEvent.SyncStateChanged(
            channel = "health-history-import:health_connect",
            state = SyncState.SYNCING,
            emittedAtEpochMillis = 2L
        )

        val receivedFirst = async { bus.events.first() }
        runCurrent()
        bus.publish(first)
        assertEquals(first, receivedFirst.await())

        val receivedSecond = async { bus.events.first() }
        runCurrent()
        bus.publish(second)
        assertEquals(second, receivedSecond.await())
    }

    @Test
    fun publish_supportsTodayStepsUpdatedEvent() = runTest {
        val bus = InMemoryAppEventBus()
        val event = FrontendEvent.TodayStepsUpdated(
            totalSteps = 1243,
            buckets = listOf(
                FrontendEvent.StepBucket(label = "08:00", steps = 320),
                FrontendEvent.StepBucket(label = "10:00", steps = 923)
            ),
            emittedAtEpochMillis = 10L
        )

        val received = async { bus.events.first() }
        runCurrent()
        bus.publish(event)

        assertEquals(event, received.await())
    }

    @Test
    fun publish_supportsTodayHeartRateUpdatedEvent() = runTest {
        val bus = InMemoryAppEventBus()
        val event = FrontendEvent.TodayHeartRateUpdated(
            latestHeartRateBpm = 63,
            averageHeartRateBpm = 66,
            buckets = listOf(
                FrontendEvent.HeartRateBucket(label = "08:00", averageHeartRateBpm = 64),
                FrontendEvent.HeartRateBucket(label = "10:00", averageHeartRateBpm = 68)
            ),
            emittedAtEpochMillis = 11L
        )

        val received = async { bus.events.first() }
        runCurrent()
        bus.publish(event)

        assertEquals(event, received.await())
    }

    @Test
    fun publish_supportsTodayHealthSummariesUpdatedEvent() = runTest {
        val bus = InMemoryAppEventBus()
        val event = FrontendEvent.TodayHealthSummariesUpdated(
            items = listOf(
                FrontendEvent.HealthSummaryItem(
                    metricKey = "STEPS",
                    metricId = "steps",
                    domainId = "ACTIVITY",
                    title = "Stappen",
                    value = "1200",
                    detail = "Vandaag totaal",
                    progress = 0.12f,
                    sourceSummary = "Health Connect"
                )
            ),
            emittedAtEpochMillis = 12L
        )

        val received = async { bus.events.first() }
        runCurrent()
        bus.publish(event)

        assertEquals(event, received.await())
    }
}
