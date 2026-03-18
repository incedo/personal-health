package com.incedo.personalhealth.core.health

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryLocalActivityStoreTest {

    @Test
    fun readRecords_filtersByMetricAndTimeRange() = runTest {
        val store = InMemoryLocalActivityStore()
        store.upsertRecords(
            listOf(
                record(
                    id = "steps-1",
                    metric = HealthMetricType.STEPS,
                    startEpochMillis = 100L,
                    endEpochMillis = 200L
                ),
                record(
                    id = "sleep-1",
                    metric = HealthMetricType.SLEEP_DURATION_MINUTES,
                    startEpochMillis = 150L,
                    endEpochMillis = 350L
                ),
                record(
                    id = "steps-2",
                    metric = HealthMetricType.STEPS,
                    startEpochMillis = 500L,
                    endEpochMillis = 700L
                )
            )
        )

        val records = store.readRecords(
            LocalActivityQuery(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = 120L,
                endEpochMillis = 600L
            )
        )

        assertEquals(listOf("steps-2", "steps-1"), records.map { it.id })
    }

    @Test
    fun upsertRecords_replacesExistingRecordWithSameId() = runTest {
        val store = InMemoryLocalActivityStore()
        store.upsertRecords(listOf(record(id = "steps-1", value = 100.0)))
        store.upsertRecords(listOf(record(id = "steps-1", value = 175.0)))

        val records = store.readRecords(
            LocalActivityQuery(
                metrics = emptySet(),
                startEpochMillis = 0L,
                endEpochMillis = Long.MAX_VALUE
            )
        )

        assertEquals(1, records.size)
        assertEquals(175.0, records.single().value)
    }

    @Test
    fun deleteRecordAndClear_removeStoredRecords() = runTest {
        val store = InMemoryLocalActivityStore()
        store.upsertRecords(
            listOf(
                record(id = "steps-1"),
                record(id = "steps-2")
            )
        )

        store.deleteRecord("steps-1")

        var records = store.readRecords(
            LocalActivityQuery(
                metrics = emptySet(),
                startEpochMillis = 0L,
                endEpochMillis = Long.MAX_VALUE
            )
        )
        assertEquals(listOf("steps-2"), records.map { it.id })

        store.clear()

        records = store.readRecords(
            LocalActivityQuery(
                metrics = emptySet(),
                startEpochMillis = 0L,
                endEpochMillis = Long.MAX_VALUE
            )
        )
        assertTrue(records.isEmpty())
    }

    private fun record(
        id: String,
        metric: HealthMetricType = HealthMetricType.STEPS,
        value: Double = 42.0,
        startEpochMillis: Long = 100L,
        endEpochMillis: Long = 200L
    ): HealthRecord = HealthRecord(
        id = id,
        metric = metric,
        value = value,
        unit = "count",
        startEpochMillis = startEpochMillis,
        endEpochMillis = endEpochMillis,
        source = HealthDataSource.UNKNOWN
    )
}
