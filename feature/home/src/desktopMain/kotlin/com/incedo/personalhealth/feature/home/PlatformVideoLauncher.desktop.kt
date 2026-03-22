package com.incedo.personalhealth.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler

@Composable
internal actual fun rememberPlatformVideoLauncher(): (String) -> Unit {
    val uriHandler = LocalUriHandler.current
    return remember(uriHandler) { { url -> uriHandler.openUri(url) } }
}
