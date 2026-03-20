package com.incedo.personalhealth.android

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity

internal fun ComponentActivity.openHealthConnectSettings(
    providerPackage: String,
    publishUiFeedback: (String) -> Unit
) {
    val healthConnectSettingsIntent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val healthConnectAppInfoIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$providerPackage")
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

    runCatching {
        startActivity(healthConnectSettingsIntent)
        publishUiFeedback("Health Connect instellingen geopend.")
    }.recoverCatching {
        startActivity(healthConnectAppInfoIntent)
        publishUiFeedback("Health Connect app-instellingen geopend.")
    }.recoverCatching {
        openHealthConnectOnPlayStore(providerPackage, publishUiFeedback)
    }.onFailure {
        publishUiFeedback("Health Connect instellingen konden niet worden geopend.")
        Log.e("PersonalHealthSync", "Opening Health Connect settings failed", it)
    }
}

internal fun ComponentActivity.openHealthConnectOnPlayStore(
    providerPackage: String,
    publishUiFeedback: (String) -> Unit
) {
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$providerPackage")
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=$providerPackage")
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

    runCatching {
        startActivity(marketIntent)
    }.recoverCatching {
        startActivity(webIntent)
    }.onFailure {
        publishUiFeedback("Kon Health Connect Play Store pagina niet openen.")
        Log.e("PersonalHealthSync", "Opening Play Store failed", it)
    }
}
