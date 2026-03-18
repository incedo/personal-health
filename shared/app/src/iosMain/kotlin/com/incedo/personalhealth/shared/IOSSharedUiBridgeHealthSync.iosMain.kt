package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthHistoryImportRequest
import com.incedo.personalhealth.core.health.HealthHistoryImporter
import com.incedo.personalhealth.core.health.HealthLiveSyncProcessor
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthSignalSubscription
import com.incedo.personalhealth.core.health.buildTodayStepsSnapshot
import com.incedo.personalhealth.integration.healthkit.HealthKitGateway
import com.incedo.personalhealth.integration.healthkit.HealthKitObserverSignalSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.Foundation.NSDate
import platform.Foundation.NSCalendar
import platform.Foundation.timeIntervalSince1970

private val iosHealthImportScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
private var iosHealthLiveSyncSubscription: HealthSignalSubscription? = null

actual fun startIosHealthHistoryImportInternal() {
    val gateway = HealthKitGateway(eventBus = AppBus.events)
    if (!gateway.isAvailable()) return

    iosHealthImportScope.launch {
        val nowEpochMillis = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
        val lookbackMillis = 365L * 24L * 60L * 60L * 1000L
        val importer = HealthHistoryImporter(
            gateway = gateway,
            source = HealthDataSource.HEALTHKIT,
            eventBus = AppBus.events
        )

        runCatching {
            importer.import(
                HealthHistoryImportRequest(
                    metrics = setOf(
                        HealthMetricType.STEPS,
                        HealthMetricType.HEART_RATE_BPM,
                        HealthMetricType.SLEEP_DURATION_MINUTES,
                        HealthMetricType.ACTIVE_ENERGY_KCAL,
                        HealthMetricType.BODY_WEIGHT_KG
                    ),
                    startEpochMillis = nowEpochMillis - lookbackMillis,
                    endEpochMillis = nowEpochMillis
                )
            )
            publishTodayStepsFromHealthKit(gateway)
        }.onFailure {
            println("[iOS Health Sync] initial history import failed: ${it.message}")
        }
    }
}

actual fun startIosHealthLiveSyncInternal() {
    if (iosHealthLiveSyncSubscription != null) return

    val gateway = HealthKitGateway(eventBus = AppBus.events)
    if (!gateway.isAvailable()) return

    val processor = HealthLiveSyncProcessor(
        gateway = gateway,
        source = HealthDataSource.HEALTHKIT,
        eventBus = AppBus.events
    )
    val signalSource = HealthKitObserverSignalSource()
    iosHealthLiveSyncSubscription = signalSource.start(iosHealthImportScope) { signal ->
        iosHealthImportScope.launch {
            runCatching {
                processor.processSignal(
                    signal = signal,
                    metrics = setOf(
                        HealthMetricType.STEPS,
                        HealthMetricType.HEART_RATE_BPM,
                        HealthMetricType.SLEEP_DURATION_MINUTES,
                        HealthMetricType.ACTIVE_ENERGY_KCAL,
                        HealthMetricType.BODY_WEIGHT_KG
                    ),
                    lookbackMillis = 24L * 60L * 60L * 1000L
                )
                publishTodayStepsFromHealthKit(gateway)
            }.onFailure {
                println("[iOS Health Sync] live sync failed: ${it.message}")
            }
        }
    }
}

private suspend fun publishTodayStepsFromHealthKit(
    gateway: HealthKitGateway
) {
    val nowEpochMillis = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
    val startOfDay = NSCalendar.currentCalendar.startOfDayForDate(NSDate())
    val startOfDayEpochMillis = (startOfDay.timeIntervalSince1970 * 1000.0).toLong()
    val snapshot = buildTodayStepsSnapshot(
        records = gateway.readRecords(
            HealthReadRequest(
                metrics = setOf(HealthMetricType.STEPS),
                startEpochMillis = startOfDayEpochMillis,
                endEpochMillis = nowEpochMillis,
                limit = 10_000
            )
        ),
        dayStartEpochMillis = startOfDayEpochMillis,
        dayEndEpochMillis = nowEpochMillis,
        bucketSizeHours = 1
    )
    AppBus.events.publish(
        FrontendEvent.TodayStepsUpdated(
            totalSteps = snapshot.totalSteps,
            buckets = snapshot.buckets.map { bucket ->
                FrontendEvent.StepBucket(
                    label = bucket.label,
                    steps = bucket.steps
                )
            },
            emittedAtEpochMillis = nowEpochMillis
        )
    )
}
