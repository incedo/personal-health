package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.HomeInsightTone

internal fun FrontendEvent.HealthSummaryItem.toHomeCard(): HomeHealthMetricCard = HomeHealthMetricCard(
    id = metricId,
    metricKey = metricKey,
    domainId = domainId,
    title = title,
    value = value,
    detail = detail,
    progress = progress,
    sourceSummary = sourceSummary,
    accent = when (domainId) {
        "ACTIVITY", "SLEEP" -> HomeInsightTone.ACCENT
        "BODY_MEASUREMENTS", "NUTRITION" -> HomeInsightTone.WARM
        "VITALS" -> HomeInsightTone.WARNING
        else -> HomeInsightTone.ACCENT
    }
)
