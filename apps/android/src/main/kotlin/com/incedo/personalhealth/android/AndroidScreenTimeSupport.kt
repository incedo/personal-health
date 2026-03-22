package com.incedo.personalhealth.android

import androidx.lifecycle.lifecycleScope
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.health.currentEpochMillis
import com.incedo.personalhealth.core.wellbeing.WellbeingEvent
import com.incedo.personalhealth.core.wellbeing.resolveSelectedSocialApps
import com.incedo.personalhealth.integration.appusage.readTodayScreenTimeSummary
import com.incedo.personalhealth.integration.appusage.usageAccessSettingsIntent
import com.incedo.personalhealth.shared.AppBus
import com.incedo.personalhealth.shared.ProfilePreferenceStore
import kotlinx.coroutines.launch

internal fun MainActivity.observeUiWellbeingRequests() {
    lifecycleScope.launch {
        AppBus.events.events.collect { event ->
            when (event) {
                is WellbeingEvent.ScreenTimePermissionRequested -> {
                    startActivity(usageAccessSettingsIntent())
                    AppBus.events.publish(
                        FrontendEvent.UiFeedbackRequested(
                            message = "Open schermtijdtoegang in Android instellingen.",
                            emittedAtEpochMillis = currentEpochMillis()
                        )
                    )
                }

                is WellbeingEvent.ScreenTimeRefreshRequested -> {
                    refreshScreenTimeMetrics()
                }
            }
        }
    }
}

internal fun MainActivity.refreshScreenTimeMetrics() {
    lifecycleScope.launch {
        val selectedApps = resolveSelectedSocialApps(ProfilePreferenceStore.selectedSocialAppPackageIds())
        val summary = readTodayScreenTimeSummary(
            context = this@refreshScreenTimeMetrics,
            selectedSocialApps = selectedApps
        )
        AppBus.events.publish(
            WellbeingEvent.ScreenTimeSummaryUpdated(
                summary = summary,
                emittedAtEpochMillis = currentEpochMillis()
            )
        )
    }
}
