package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class HealthSyncCheckpointStoreTest {

    @Test
    fun markProcessed_tracksCheckpoint() = runTest {
        val store = InMemoryHealthSyncCheckpointStore(maxEntries = 4)

        assertFalse(store.hasProcessed("intent-1"))
        store.markProcessed("intent-1", processedAtEpochMillis = 1L)
        assertTrue(store.hasProcessed("intent-1"))
    }

    @Test
    fun markProcessed_trimsOldestWhenCapacityExceeded() = runTest {
        val store = InMemoryHealthSyncCheckpointStore(maxEntries = 2)

        store.markProcessed("intent-1", processedAtEpochMillis = 1L)
        store.markProcessed("intent-2", processedAtEpochMillis = 2L)
        store.markProcessed("intent-3", processedAtEpochMillis = 3L)

        assertFalse(store.hasProcessed("intent-1"))
        assertTrue(store.hasProcessed("intent-2"))
        assertTrue(store.hasProcessed("intent-3"))
    }
}
