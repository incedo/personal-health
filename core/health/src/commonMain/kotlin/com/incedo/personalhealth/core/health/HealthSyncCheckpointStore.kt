package com.incedo.personalhealth.core.health

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface HealthSyncCheckpointStore {
    suspend fun hasProcessed(checkpointKey: String): Boolean
    suspend fun markProcessed(checkpointKey: String, processedAtEpochMillis: Long)
}

class InMemoryHealthSyncCheckpointStore(
    private val maxEntries: Int = 4_096
) : HealthSyncCheckpointStore {
    private val mutex = Mutex()
    private val checkpoints = LinkedHashMap<String, Long>()

    override suspend fun hasProcessed(checkpointKey: String): Boolean = mutex.withLock {
        checkpoints.containsKey(checkpointKey)
    }

    override suspend fun markProcessed(checkpointKey: String, processedAtEpochMillis: Long) {
        mutex.withLock {
            checkpoints[checkpointKey] = processedAtEpochMillis
            trimIfNeeded()
        }
    }

    private fun trimIfNeeded() {
        while (checkpoints.size > maxEntries) {
            val firstKey = checkpoints.entries.firstOrNull()?.key ?: return
            checkpoints.remove(firstKey)
        }
    }
}
