package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals

class TodayStepsSnapshotTest {

    @Test
    fun buildTodayStepsSnapshot_aggregatesRecordsIntoHourlyBuckets() {
        val snapshot = buildTodayStepsSnapshot(
            records = listOf(
                record(id = "a", value = 125.0, endEpochMillis = hour(1)),
                record(id = "b", value = 300.0, endEpochMillis = hour(1) + 10L),
                record(id = "c", value = 725.0, endEpochMillis = hour(14))
            ),
            dayStartEpochMillis = 0L,
            dayEndEpochMillis = hour(23) + 59L,
            bucketSizeHours = 1
        )

        assertEquals(1_150, snapshot.totalSteps)
        assertEquals(24, snapshot.buckets.size)
        assertEquals("00:00", snapshot.buckets[0].label)
        assertEquals(0, snapshot.buckets[0].steps)
        assertEquals(425, snapshot.buckets[1].steps)
        assertEquals(725, snapshot.buckets[14].steps)
    }

    @Test
    fun buildTodayStepsSnapshot_supportsLargerBucketSizes() {
        val snapshot = buildTodayStepsSnapshot(
            records = listOf(
                record(id = "a", value = 200.0, endEpochMillis = hour(0) + 1L),
                record(id = "b", value = 250.0, endEpochMillis = hour(2) + 1L)
            ),
            dayStartEpochMillis = 0L,
            dayEndEpochMillis = hour(23) + 59L,
            bucketSizeHours = 3
        )

        assertEquals(8, snapshot.buckets.size)
        assertEquals("00:00", snapshot.buckets.first().label)
        assertEquals(450, snapshot.buckets.first().steps)
    }

    private fun record(
        id: String,
        value: Double,
        endEpochMillis: Long
    ): HealthRecord = HealthRecord(
        id = id,
        metric = HealthMetricType.STEPS,
        value = value,
        unit = "count",
        startEpochMillis = endEpochMillis,
        endEpochMillis = endEpochMillis,
        source = HealthDataSource.UNKNOWN
    )

    private fun hour(hour: Int): Long = hour * 60L * 60L * 1000L
}
