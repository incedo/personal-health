package com.incedo.personalhealth.core.health

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class LocalActivityQuery(
    val metrics: Set<HealthMetricType>,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val limit: Int = 1_000
) {
    init {
        require(startEpochMillis <= endEpochMillis) {
            "startEpochMillis must be before or equal to endEpochMillis"
        }
        require(limit > 0) { "limit must be > 0" }
    }
}

enum class LocalActivityStorageEngine {
    SQLITE,
    SQLITE_WASM,
    INDEXED_DB,
    MEMORY
}

enum class LocalActivityStoragePersistence {
    DEVICE_LOCAL_PERSISTENT,
    BROWSER_LOCAL_PERSISTENT,
    VOLATILE
}

data class LocalActivityStorageRecommendation(
    val platform: String,
    val primaryEngine: LocalActivityStorageEngine,
    val fallbackEngine: LocalActivityStorageEngine? = null,
    val persistence: LocalActivityStoragePersistence,
    val rationale: String
)

interface LocalActivityStore {
    suspend fun upsertRecords(records: List<HealthRecord>)
    suspend fun readRecords(query: LocalActivityQuery): List<HealthRecord>
    suspend fun deleteRecord(recordId: String)
    suspend fun clear()
}

class InMemoryLocalActivityStore : LocalActivityStore {
    private val mutex = Mutex()
    private val recordsById = linkedMapOf<String, HealthRecord>()

    override suspend fun upsertRecords(records: List<HealthRecord>) {
        mutex.withLock {
            records.forEach { record ->
                recordsById[record.id] = record
            }
        }
    }

    override suspend fun readRecords(query: LocalActivityQuery): List<HealthRecord> = mutex.withLock {
        recordsById.values
            .asSequence()
            .filter { record -> query.metrics.isEmpty() || record.metric in query.metrics }
            .filter { record -> record.endEpochMillis >= query.startEpochMillis }
            .filter { record -> record.startEpochMillis <= query.endEpochMillis }
            .sortedByDescending { record -> record.startEpochMillis }
            .take(query.limit)
            .toList()
    }

    override suspend fun deleteRecord(recordId: String) {
        mutex.withLock {
            recordsById.remove(recordId)
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            recordsById.clear()
        }
    }
}

expect object PlatformLocalActivityStorage {
    fun recommendation(): LocalActivityStorageRecommendation
}
