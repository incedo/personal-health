package com.incedo.personalhealth.integration.appusage

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import android.provider.Settings
import com.incedo.personalhealth.core.wellbeing.ScreenTimePermissionState
import com.incedo.personalhealth.core.wellbeing.ScreenTimeSummary
import com.incedo.personalhealth.core.wellbeing.SocialAppDefinition
import com.incedo.personalhealth.core.wellbeing.SocialAppUsage
import com.incedo.personalhealth.core.wellbeing.emptyScreenTimeSummary
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

fun hasUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun readTodayScreenTimeSummary(
    context: Context,
    selectedSocialApps: List<SocialAppDefinition>,
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): ScreenTimeSummary {
    if (!hasUsageAccess(context)) {
        return emptyScreenTimeSummary(
            selectedApps = selectedSocialApps,
            permissionState = ScreenTimePermissionState.REQUIRES_ACCESS
        )
    }

    val startOfDay = now.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()
    val endEpochMillis = now.toEpochMilli()
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        ?: return emptyScreenTimeSummary(
            selectedApps = selectedSocialApps,
            permissionState = ScreenTimePermissionState.UNAVAILABLE
        )

    val stats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        startOfDay,
        endEpochMillis
    ).filter { it.totalTimeInForeground > 0L }

    val totalMinutes = stats.sumOf { it.totalTimeInForeground }.toMinutes()
    val durationsByPackage = stats.associate { it.packageName to it.totalTimeInForeground.toMinutes() }
    val socialUsages = selectedSocialApps.map { app ->
        SocialAppUsage(
            packageName = app.packageName,
            displayName = app.displayName,
            durationMinutes = durationsByPackage[app.packageName] ?: 0
        )
    }.sortedByDescending { it.durationMinutes }

    return ScreenTimeSummary(
        permissionState = ScreenTimePermissionState.GRANTED,
        totalScreenMinutes = totalMinutes,
        socialScreenMinutes = socialUsages.sumOf { it.durationMinutes },
        selectedSocialApps = socialUsages
    )
}

fun usageAccessSettingsIntent() = android.content.Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

private fun Long.toMinutes(): Int = (this / 60_000.0).roundToInt()
