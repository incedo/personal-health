package com.incedo.personalhealth.feature.home

internal const val BODY_WEIGHT_HEALTH_METRIC_ID = "body_weight"

internal fun resolveHealthMetricValue(
    metrics: List<HomeHealthMetricCard>,
    metricId: String,
    emptyValue: String = "Geen data"
): String = metrics.firstOrNull { it.id == metricId }?.value ?: emptyValue
