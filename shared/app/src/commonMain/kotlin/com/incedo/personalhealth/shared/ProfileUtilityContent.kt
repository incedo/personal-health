package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.events.FrontendEvent
import com.incedo.personalhealth.core.events.SyncState
import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.currentEpochMillis
import com.incedo.personalhealth.core.wellbeing.WellbeingEvent
import com.incedo.personalhealth.core.wellbeing.ScreenTimeSummary
import com.incedo.personalhealth.core.wellbeing.SocialAppDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun ColumnScope.HealthSyncToolsContent(
    healthSyncState: SyncState,
    healthSyncChannel: String,
    lastReadSummary: String,
    importStrategy: HealthImportStrategy,
    intentReceivedCount: Int,
    intentSkippedCount: Int,
    intentAppliedCount: Int,
    intentFailedCount: Int,
    metricEventCounts: Map<HealthMetricType, Int>,
    latestUiMessage: String,
    importRequestCount: Int,
    onImportAction: (HealthImportActionId) -> Unit,
    onRecalculateDebug: (() -> Unit)?,
    onImportDocument: suspend (com.incedo.personalhealth.core.health.CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit
) {
    HealthSyncStatusCard(
        syncState = healthSyncState,
        channel = healthSyncChannel,
        lastReadSummary = lastReadSummary
    )
    HealthSyncStatsCard(
        strategy = importStrategy,
        intentReceivedCount = intentReceivedCount,
        intentSkippedCount = intentSkippedCount,
        intentAppliedCount = intentAppliedCount,
        intentFailedCount = intentFailedCount,
        metricEventCounts = metricEventCounts,
        onAction = onImportAction,
        onRecalculateDebug = onRecalculateDebug,
        latestUiMessage = latestUiMessage,
        importRequestCount = importRequestCount,
        importInProgress = healthSyncState == SyncState.SYNCING
    )
    PlatformHealthImportPanel(
        onImportDocument = onImportDocument,
        onImportMessage = onImportMessage
    )
}

@Composable
internal fun ColumnScope.ProfileUtilityContent(
    screenTimeSummary: ScreenTimeSummary,
    availableApps: List<SocialAppDefinition>,
    selectedPackages: Set<String>,
    latestUiMessage: String,
    onTogglePackage: (String, Boolean) -> Unit,
    onRequestAccess: () -> Unit
) {
    ScreenTimeSettingsCard(
        summary = screenTimeSummary,
        availableApps = availableApps,
        selectedPackages = selectedPackages,
        onTogglePackage = onTogglePackage,
        onRequestAccess = onRequestAccess
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
            Text("Laatste status", style = MaterialTheme.typography.titleSmall)
            Text(latestUiMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun ColumnScope.ProfileWellbeingContent(
    appScope: CoroutineScope,
    screenTimeSummary: ScreenTimeSummary,
    selectedSocialPackages: Set<String>,
    latestUiMessage: String,
    onSelectedPackagesChanged: (Set<String>) -> Unit
) {
    ProfileUtilityContent(
        screenTimeSummary = screenTimeSummary,
        availableApps = com.incedo.personalhealth.core.wellbeing.resolveSelectedSocialApps(
            com.incedo.personalhealth.core.wellbeing.defaultSelectedSocialAppPackages()
        ),
        selectedPackages = selectedSocialPackages,
        latestUiMessage = latestUiMessage,
        onTogglePackage = { packageName, checked ->
            val updated = selectedSocialPackages.toMutableSet().apply {
                if (checked) add(packageName) else remove(packageName)
            }
            onSelectedPackagesChanged(updated)
            ProfilePreferenceStore.setSelectedSocialAppPackageIds(updated)
            appScope.launch {
                AppBus.events.publish(WellbeingEvent.ScreenTimeRefreshRequested(currentEpochMillis()))
            }
        },
        onRequestAccess = {
            appScope.launch {
                AppBus.events.publish(WellbeingEvent.ScreenTimePermissionRequested(currentEpochMillis()))
            }
        }
    )
}
