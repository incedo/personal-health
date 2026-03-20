package com.incedo.personalhealth.feature.home

internal const val SLEEP_HEALTH_METRIC_ID = "sleep"

internal fun supportsDedicatedHealthMetricDetail(metricId: String): Boolean =
    metricId == SLEEP_HEALTH_METRIC_ID || metricId == BODY_WEIGHT_HEALTH_METRIC_ID

internal fun resolveSleepMetric(metrics: List<HomeHealthMetricCard>): HomeHealthMetricCard =
    metrics.firstOrNull { it.id == SLEEP_HEALTH_METRIC_ID }
        ?: HomeHealthMetricCard(
            id = SLEEP_HEALTH_METRIC_ID,
            metricKey = "SLEEP_DURATION_MINUTES",
            domainId = "SLEEP",
            title = "Slaap",
            value = "Geen data",
            detail = "Laatste slaapsessie ontbreekt",
            progress = 0f,
            sourceSummary = "Bron onbekend",
            accent = HomeInsightTone.ACCENT
        )

internal fun resolveWeightMetric(metrics: List<HomeHealthMetricCard>): HomeHealthMetricCard =
    metrics.firstOrNull { it.id == BODY_WEIGHT_HEALTH_METRIC_ID }
        ?: HomeHealthMetricCard(
            id = BODY_WEIGHT_HEALTH_METRIC_ID,
            metricKey = "BODY_WEIGHT_KG",
            domainId = "BODY_MEASUREMENTS",
            title = "Gewicht",
            value = "Geen data",
            detail = "Laatste meting ontbreekt",
            progress = 0f,
            sourceSummary = "Bron onbekend",
            accent = HomeInsightTone.ACCENT
        )
