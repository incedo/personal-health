package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals

class TodayHeartRateSnapshotTest {

    @Test
    fun buildTodayHeartRateSnapshot_aggregatesHourlyAveragesAndLatestValue() {
        val snapshot = buildTodayHeartRateSnapshot(
            records = listOf(
                HealthRecord("hr-1", HealthMetricType.HEART_RATE_BPM, 60.0, "bpm", 1_000L, 1_000L, HealthDataSource.HEALTH_CONNECT),
                HealthRecord("hr-2", HealthMetricType.HEART_RATE_BPM, 66.0, "bpm", 2_000L, 2_000L, HealthDataSource.HEALTH_CONNECT),
                HealthRecord("hr-3", HealthMetricType.HEART_RATE_BPM, 72.0, "bpm", 3_700_000L, 3_700_000L, HealthDataSource.HEALTH_CONNECT)
            ),
            dayStartEpochMillis = 0L,
            dayEndEpochMillis = 7_200_000L,
            bucketSizeHours = 1
        )

        assertEquals(72, snapshot.latestHeartRateBpm)
        assertEquals(66, snapshot.averageHeartRateBpm)
        assertEquals(63, snapshot.buckets[0].averageHeartRateBpm)
        assertEquals(72, snapshot.buckets[1].averageHeartRateBpm)
        assertEquals("00:00", snapshot.buckets[0].label)
        assertEquals("01:00", snapshot.buckets[1].label)
    }
}
