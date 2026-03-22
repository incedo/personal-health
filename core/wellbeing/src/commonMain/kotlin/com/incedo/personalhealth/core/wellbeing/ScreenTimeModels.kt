package com.incedo.personalhealth.core.wellbeing

data class SocialAppDefinition(
    val packageName: String,
    val displayName: String
)

data class SocialAppUsage(
    val packageName: String,
    val displayName: String,
    val durationMinutes: Int
)

enum class ScreenTimePermissionState {
    GRANTED,
    REQUIRES_ACCESS,
    UNAVAILABLE
}

enum class WellbeingSource {
    APP_USAGE
}

data class ScreenTimeSummary(
    val permissionState: ScreenTimePermissionState,
    val totalScreenMinutes: Int,
    val socialScreenMinutes: Int,
    val selectedSocialApps: List<SocialAppUsage>,
    val source: WellbeingSource = WellbeingSource.APP_USAGE
)

object DefaultSocialAppCatalog {
    val entries: List<SocialAppDefinition> = listOf(
        SocialAppDefinition("com.instagram.android", "Instagram"),
        SocialAppDefinition("com.zhiliaoapp.musically", "TikTok"),
        SocialAppDefinition("com.twitter.android", "X"),
        SocialAppDefinition("com.facebook.katana", "Facebook"),
        SocialAppDefinition("com.facebook.orca", "Messenger"),
        SocialAppDefinition("com.snapchat.android", "Snapchat"),
        SocialAppDefinition("com.reddit.frontpage", "Reddit"),
        SocialAppDefinition("com.linkedin.android", "LinkedIn"),
        SocialAppDefinition("com.whatsapp", "WhatsApp"),
        SocialAppDefinition("org.telegram.messenger", "Telegram"),
        SocialAppDefinition("com.discord", "Discord"),
        SocialAppDefinition("com.google.android.youtube", "YouTube")
    )
}

fun defaultSelectedSocialAppPackages(): Set<String> =
    DefaultSocialAppCatalog.entries.mapTo(linkedSetOf()) { it.packageName }

fun resolveSelectedSocialApps(
    selectedPackages: Set<String>?
): List<SocialAppDefinition> {
    val resolvedPackages = selectedPackages ?: defaultSelectedSocialAppPackages()
    return DefaultSocialAppCatalog.entries.filter { it.packageName in resolvedPackages }
}

fun emptyScreenTimeSummary(
    selectedApps: List<SocialAppDefinition>,
    permissionState: ScreenTimePermissionState = ScreenTimePermissionState.REQUIRES_ACCESS
): ScreenTimeSummary = ScreenTimeSummary(
    permissionState = permissionState,
    totalScreenMinutes = 0,
    socialScreenMinutes = 0,
    selectedSocialApps = selectedApps.map { app ->
        SocialAppUsage(
            packageName = app.packageName,
            displayName = app.displayName,
            durationMinutes = 0
        )
    }
)
