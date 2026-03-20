package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistedActivityTrackingStoreTest {

    @Test
    fun startAndStopActivity_persistsActiveAndCompletedState() {
        val driver = InMemoryDriver()
        val store = PersistedActivityTrackingStore(driver)

        val started = store.startActivity(QuickActivityType.RUNNING, nowEpochMillis = 1_000L)
        val stopped = store.stopActiveActivity(nowEpochMillis = 4_000L)

        assertEquals(QuickActivityType.RUNNING, started.activeSession?.type)
        assertNull(stopped.activeSession)
        assertEquals(1, stopped.completedSessions.size)
        assertEquals(3_000L, stopped.completedSessions.single().durationMillis)
    }

    @Test
    fun clear_removesPersistedTrackingCatalog() {
        val driver = InMemoryDriver()
        val store = PersistedActivityTrackingStore(driver)
        store.startActivity(QuickActivityType.WALKING, nowEpochMillis = 500L)

        store.clear()

        assertNull(driver.payload)
        assertNull(store.readSnapshot().activeSession)
        assertEquals(emptyList(), store.readSnapshot().completedSessions)
    }

    private class InMemoryDriver : ActivityTrackingPersistenceDriver {
        var payload: String? = null

        override fun read(): String? = payload

        override fun write(payload: String) {
            this.payload = payload
        }

        override fun clear() {
            payload = null
        }
    }
}
