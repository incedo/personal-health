package com.incedo.personalhealth.android

import android.app.Activity
import com.incedo.personalhealth.integration.samsunghealth.SamsungHealthGateway

suspend fun prepareSamsungHealthGateway(
    activity: Activity,
    shouldRequestPermissions: Boolean
): SamsungHealthGateway? {
    val gateway = SamsungHealthGateway(activity)
    if (!gateway.availability().isReady) return null
    if (gateway.hasRequiredPermissions()) return gateway
    if (!shouldRequestPermissions) return null
    return if (gateway.requestReadPermissions(activity)) gateway else null
}
