package com.incedo.personalhealth.integration.healthconnect

import com.incedo.personalhealth.core.health.HealthChangeSignal
import com.incedo.personalhealth.core.health.HealthChangeSignalSource
import com.incedo.personalhealth.core.health.HealthChangeTrigger
import com.incedo.personalhealth.core.health.HealthDataSource
import com.incedo.personalhealth.core.health.HealthSignalSubscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HealthConnectPollingSignalSource(
    private val pollIntervalMillis: Long = DEFAULT_POLL_INTERVAL_MILLIS
) : HealthChangeSignalSource {

    override fun start(
        scope: CoroutineScope,
        onSignal: (HealthChangeSignal) -> Unit
    ): HealthSignalSubscription {
        require(pollIntervalMillis > 0) { "pollIntervalMillis must be > 0" }

        val job: Job = scope.launch {
            while (isActive) {
                delay(pollIntervalMillis)
                onSignal(
                    HealthChangeSignal(
                        intentId = buildPollIntentId(),
                        source = HealthDataSource.HEALTH_CONNECT,
                        trigger = HealthChangeTrigger.POLL_TICK
                    )
                )
            }
        }

        return HealthSignalSubscription {
            job.cancel()
        }
    }

    companion object {
        const val DEFAULT_POLL_INTERVAL_MILLIS: Long = 60_000L
    }

    private fun buildPollIntentId(): String {
        val bucket = System.currentTimeMillis() / pollIntervalMillis
        return "health-connect-poll:$bucket"
    }
}
