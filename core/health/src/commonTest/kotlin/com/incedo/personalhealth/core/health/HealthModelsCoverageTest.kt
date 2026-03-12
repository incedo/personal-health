package com.incedo.personalhealth.core.health

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthModelsCoverageTest {

    @Test
    fun canonicalModelDataClasses_holdValues() {
        val request = HealthReadRequest(
            metrics = setOf(HealthMetricType.STEPS, HealthMetricType.HEART_RATE_BPM),
            startEpochMillis = 10L,
            endEpochMillis = 20L,
            limit = 5
        )
        val record = HealthRecord(
            id = "id-1",
            metric = HealthMetricType.STEPS,
            value = 12.0,
            unit = "count",
            startEpochMillis = 10L,
            endEpochMillis = 20L,
            source = HealthDataSource.HEALTH_CONNECT,
            metadata = mapOf("origin" to "test")
        )

        assertEquals(5, request.limit)
        assertEquals("id-1", record.id)
        assertEquals("test", record.metadata["origin"])
    }

    @Test
    fun healthEvents_canBeConstructedForAllIntentTypes() {
        val now = 123L
        val received = HealthEvent.LiveSyncIntentReceived(
            source = HealthDataSource.HEALTH_CONNECT,
            intentId = "intent-1",
            trigger = HealthChangeTrigger.MANUAL,
            emittedAtEpochMillis = now
        )
        val skipped = HealthEvent.LiveSyncIntentSkippedDuplicate(
            source = HealthDataSource.HEALTH_CONNECT,
            intentId = "intent-1",
            trigger = HealthChangeTrigger.MANUAL,
            emittedAtEpochMillis = now
        )
        val applied = HealthEvent.LiveSyncIntentApplied(
            source = HealthDataSource.HEALTH_CONNECT,
            intentId = "intent-1",
            trigger = HealthChangeTrigger.MANUAL,
            metrics = setOf(HealthMetricType.STEPS),
            count = 2,
            emittedAtEpochMillis = now
        )
        val metricEvents = listOf(
            HealthEvent.LiveSyncStepsIntentApplied(HealthDataSource.HEALTH_CONNECT, "intent-1", HealthChangeTrigger.MANUAL, 1, now),
            HealthEvent.LiveSyncHeartRateIntentApplied(HealthDataSource.HEALTH_CONNECT, "intent-1", HealthChangeTrigger.MANUAL, 1, now),
            HealthEvent.LiveSyncSleepIntentApplied(HealthDataSource.HEALTH_CONNECT, "intent-1", HealthChangeTrigger.MANUAL, 1, now),
            HealthEvent.LiveSyncActiveEnergyIntentApplied(HealthDataSource.HEALTH_CONNECT, "intent-1", HealthChangeTrigger.MANUAL, 1, now),
            HealthEvent.LiveSyncBodyWeightIntentApplied(HealthDataSource.HEALTH_CONNECT, "intent-1", HealthChangeTrigger.MANUAL, 1, now)
        )
        val failed = HealthEvent.LiveSyncIntentFailed(
            source = HealthDataSource.HEALTH_CONNECT,
            intentId = "intent-1",
            trigger = HealthChangeTrigger.MANUAL,
            reason = "x",
            emittedAtEpochMillis = now
        )

        assertEquals("intent-1", received.intentId)
        assertEquals("intent-1", skipped.intentId)
        assertEquals(2, applied.count)
        assertEquals(5, metricEvents.size)
        assertEquals("x", failed.reason)
        assertTrue(metricEvents.all { it.count == 1 })
    }
}
