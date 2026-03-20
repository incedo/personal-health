package com.incedo.personalhealth.android

import android.content.Context
import com.incedo.personalhealth.core.events.AppEventBus
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.integration.healthconnect.HealthConnectGateway
import com.incedo.personalhealth.integration.samsunghealth.SamsungHealthGateway

data class AndroidHealthGatewayBundle(
    val primary: HealthConnectGateway,
    val supplemental: HealthDataGateway?
)

fun createAndroidHealthGatewayBundle(
    context: Context,
    eventBus: AppEventBus
): AndroidHealthGatewayBundle {
    val healthConnectGateway = HealthConnectGateway(
        context = context,
        eventBus = eventBus
    )
    val samsungHealthGateway = SamsungHealthGateway(context)
        .takeIf { it.availability().isReady }

    return AndroidHealthGatewayBundle(
        primary = healthConnectGateway,
        supplemental = samsungHealthGateway
    )
}
