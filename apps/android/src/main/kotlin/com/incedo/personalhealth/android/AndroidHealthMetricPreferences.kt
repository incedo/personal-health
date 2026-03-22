package com.incedo.personalhealth.android

import com.incedo.personalhealth.core.health.HealthMetricType

internal val samsungPreferredMetrics = samsungCanonicalMetrics

internal fun samsungMetricsFor(metrics: Set<HealthMetricType>): Set<HealthMetricType> =
    metrics intersect samsungPreferredMetrics

internal fun healthConnectFallbackMetricsFor(
    metrics: Set<HealthMetricType>,
    samsungReady: Boolean
): Set<HealthMetricType> = if (samsungReady) {
    metrics - samsungPreferredMetrics
} else {
    metrics
}
