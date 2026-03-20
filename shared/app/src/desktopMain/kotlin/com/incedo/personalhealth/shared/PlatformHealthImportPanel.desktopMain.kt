package com.incedo.personalhealth.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument

@Composable
actual fun PlatformHealthImportPanel(
    onImportDocument: suspend (CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit,
    modifier: Modifier
) {
    HealthPayloadImportPanel(
        title = "Desktop import",
        description = "Importeer canonical JSON of Withings CSV om lange gewichts- en bloeddrukhistorie lokaal te testen.",
        initialPayload = "",
        onPersistPayload = {},
        onImportDocument = onImportDocument,
        onImportMessage = onImportMessage,
        modifier = modifier
    )
}
