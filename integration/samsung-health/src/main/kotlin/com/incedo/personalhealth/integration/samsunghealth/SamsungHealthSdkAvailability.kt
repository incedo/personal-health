package com.incedo.personalhealth.integration.samsunghealth

import android.content.Context
import android.os.Build

private const val SAMSUNG_HEALTH_PACKAGE = "com.sec.android.app.shealth"
private const val SAMSUNG_HEALTH_MIN_VERSION = "6.30.2"
private const val SAMSUNG_HEALTH_DATA_SERVICE = "com.samsung.android.sdk.health.data.HealthDataService"

enum class SamsungHealthSdkStatus {
    READY,
    ANDROID_VERSION_UNSUPPORTED,
    APP_NOT_INSTALLED,
    APP_VERSION_UNSUPPORTED,
    SDK_BINARY_MISSING
}

data class SamsungHealthSdkAvailability(
    val status: SamsungHealthSdkStatus,
    val appVersion: String? = null
) {
    val isReady: Boolean get() = status == SamsungHealthSdkStatus.READY
}

fun currentSamsungHealthAvailability(context: Context): SamsungHealthSdkAvailability {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        return SamsungHealthSdkAvailability(SamsungHealthSdkStatus.ANDROID_VERSION_UNSUPPORTED)
    }

    val packageInfo = runCatching {
        context.packageManager.getPackageInfo(SAMSUNG_HEALTH_PACKAGE, 0)
    }.getOrNull() ?: return SamsungHealthSdkAvailability(SamsungHealthSdkStatus.APP_NOT_INSTALLED)

    val versionName = packageInfo.versionName.orEmpty()
    if (compareSamsungHealthVersions(versionName, SAMSUNG_HEALTH_MIN_VERSION) < 0) {
        return SamsungHealthSdkAvailability(
            status = SamsungHealthSdkStatus.APP_VERSION_UNSUPPORTED,
            appVersion = versionName
        )
    }

    val sdkLinked = runCatching {
        Class.forName(SAMSUNG_HEALTH_DATA_SERVICE)
    }.isSuccess

    return if (sdkLinked) {
        SamsungHealthSdkAvailability(
            status = SamsungHealthSdkStatus.READY,
            appVersion = versionName
        )
    } else {
        SamsungHealthSdkAvailability(
            status = SamsungHealthSdkStatus.SDK_BINARY_MISSING,
            appVersion = versionName
        )
    }
}

internal fun compareSamsungHealthVersions(left: String, right: String): Int {
    val leftParts = left.split('.').map { it.toIntOrNull() ?: 0 }
    val rightParts = right.split('.').map { it.toIntOrNull() ?: 0 }
    val size = maxOf(leftParts.size, rightParts.size)

    repeat(size) { index ->
        val leftValue = leftParts.getOrElse(index) { 0 }
        val rightValue = rightParts.getOrElse(index) { 0 }
        if (leftValue != rightValue) return leftValue.compareTo(rightValue)
    }

    return 0
}
