package com.incedo.personalhealth.feature.home

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberPlatformVideoLauncher(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { url ->
            val nativeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                setPackage("com.google.android.youtube")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(
                if (nativeIntent.resolveActivity(context.packageManager) != null) {
                    nativeIntent
                } else {
                    fallbackIntent
                }
            )
        }
    }
}
