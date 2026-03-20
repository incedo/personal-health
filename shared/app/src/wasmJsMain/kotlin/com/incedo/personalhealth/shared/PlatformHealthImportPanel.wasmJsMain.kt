package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument

private const val WEB_IMPORT_STORAGE_KEY = "personal-health.canonical-import.v1"

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun localStorageSetItem(key: String, value: String)

@Composable
actual fun PlatformHealthImportPanel(
    onImportDocument: suspend (CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit,
    modifier: Modifier
) {
    HealthPayloadImportPanel(
        title = "Web import",
        description = "Gebruik canonical JSON of Withings CSV als browserbron voor health records.",
        initialPayload = localStorageGetItem(WEB_IMPORT_STORAGE_KEY).orEmpty(),
        onPersistPayload = { payload -> localStorageSetItem(WEB_IMPORT_STORAGE_KEY, payload) },
        onImportDocument = onImportDocument,
        onImportMessage = onImportMessage,
        modifier = modifier
    )
}
