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
}
