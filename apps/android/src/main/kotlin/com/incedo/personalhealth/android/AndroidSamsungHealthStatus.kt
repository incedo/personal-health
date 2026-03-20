package com.incedo.personalhealth.android

import android.content.Context
import com.incedo.personalhealth.integration.samsunghealth.currentSamsungHealthAvailability

fun samsungHealthAvailabilityMessage(context: Context): String? {
    val availability = currentSamsungHealthAvailability(context)
    return when {
        availability.isReady -> "Samsung Health Data SDK klaar als primaire Android databron voor slaap, actieve energie en gewicht."
        availability.appVersion != null -> {
            "Samsung Health beschikbaar (${availability.appVersion}), maar de Data SDK-binary is nog niet gelinkt."
        }
        else -> null
    }
}
