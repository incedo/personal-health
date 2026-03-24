package com.incedo.personalhealth.shared

import com.incedo.personalhealth.core.wellbeing.ScreenTimePermissionState
import com.incedo.personalhealth.core.wellbeing.ScreenTimeSummary
import com.incedo.personalhealth.feature.home.HomeHealthMetricCard
import com.incedo.personalhealth.feature.home.HomeInsightTone

private const val SCREEN_TIME_DOMAIN_ID = "WELLBEING"

internal fun buildScreenTimeMetricCards(
    summary: ScreenTimeSummary
): List<HomeHealthMetricCard> {
    val cards = mutableListOf(
        summaryCard(
            id = "screen_time_total",
            title = "Schermtijd totaal",
            value = if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
                "${summary.totalScreenMinutes} min"
            } else {
                "Toegang nodig"
            },
            detail = if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
                "Vandaag over alle apps"
            } else {
                "Geef Usage Access in Profiel om schermtijd te lezen"
            },
            progress = (summary.totalScreenMinutes / 360f).coerceIn(0f, 1f),
            accent = if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
                HomeInsightTone.WARNING
            } else {
                HomeInsightTone.WARM
            }
        ),
        summaryCard(
            id = "screen_time_social",
            title = "Social apps",
            value = if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
                "${summary.socialScreenMinutes} min"
            } else {
                "Toegang nodig"
            },
            detail = if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
                "Vandaag voor geselecteerde social apps"
            } else {
                "Selecteer apps in Profiel en geef Usage Access"
            },
            progress = (summary.socialScreenMinutes / 180f).coerceIn(0f, 1f),
            accent = HomeInsightTone.WARNING
        )
    )

    if (summary.permissionState == ScreenTimePermissionState.GRANTED) {
        cards += summary.selectedSocialApps
            .filter { it.durationMinutes > 0 }
            .take(6)
            .map { app ->
                summaryCard(
                    id = "screen_time_${app.packageName}",
                    title = app.displayName,
                    value = "${app.durationMinutes} min",
                    detail = "Vandaag social app usage",
                    progress = (app.durationMinutes / 120f).coerceIn(0f, 1f),
                    accent = HomeInsightTone.WARM
                )
            }
    }

    return cards
}

private fun summaryCard(
    id: String,
    title: String,
    value: String,
    detail: String,
    progress: Float,
    accent: HomeInsightTone
) = HomeHealthMetricCard(
    id = id,
    metricKey = id.uppercase(),
    domainId = SCREEN_TIME_DOMAIN_ID,
    title = title,
    value = value,
    detail = detail,
    progress = progress,
    sourceSummary = "App usage",
    accent = accent
)
