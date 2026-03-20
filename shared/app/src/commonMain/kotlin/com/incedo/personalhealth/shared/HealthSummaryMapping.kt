package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.HomeInsightTone

internal fun FrontendEvent.HealthSummaryItem.toHomeCard(): HomeHealthMetricCard = HomeHealthMetricCard(
    id = metricId,
    title = title,
    value = value,
    detail = detail,
    progress = progress,
    sourceSummary = sourceSummary,
    accent = when (metricId) {
        "steps" -> HomeInsightTone.ACCENT
        "heart_rate" -> HomeInsightTone.WARNING
        "active_energy" -> HomeInsightTone.WARM
        "blood_pressure" -> HomeInsightTone.WARNING
        "body_composition" -> HomeInsightTone.WARM
        else -> HomeInsightTone.ACCENT
    }
)
