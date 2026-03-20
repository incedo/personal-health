package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import platform.Foundation.NSUserDefaults

@Composable
@OptIn(ExperimentalNativeApi::class)
actual fun PlatformHealthImportPanel(
    onImportDocument: suspend (CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit,
    modifier: Modifier
) {
    if (!Platform.isDebugBinary) return
    HealthPayloadImportPanel(
        title = "Debug import",
        description = "Gebruik canonical JSON of Withings CSV om historische health data in debug builds te laden.",
        initialPayload = loadStoredIosImportPayload(),
        onPersistPayload = ::storeIosImportPayload,
        onImportDocument = onImportDocument,
        onImportMessage = onImportMessage,
        modifier = modifier
    )
}

private const val IOS_DEBUG_IMPORT_KEY = "personal-health.canonical-import.debug.v1"

private fun loadStoredIosImportPayload(): String =
    NSUserDefaults.standardUserDefaults.stringForKey(IOS_DEBUG_IMPORT_KEY).orEmpty()

private fun storeIosImportPayload(payload: String) {
    NSUserDefaults.standardUserDefaults.setObject(payload, IOS_DEBUG_IMPORT_KEY)
}
