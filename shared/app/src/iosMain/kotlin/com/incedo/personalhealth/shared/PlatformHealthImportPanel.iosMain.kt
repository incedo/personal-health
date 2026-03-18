package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.health.CanonicalHealthImportDocument
import com.incedo.personalhealth.core.health.parseCanonicalHealthImportDocument
import kotlinx.coroutines.launch
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

    val scope = rememberCoroutineScope()
    var importPayload by rememberSaveable { mutableStateOf(loadStoredIosImportPayload()) }
    var helperText by rememberSaveable { mutableStateOf("Plak canonical JSON om fake health data in debug builds te laden.") }

    LaunchedEffect(Unit) {
        if (importPayload.isBlank()) return@LaunchedEffect

        runCatching {
            parseCanonicalHealthImportDocument(importPayload)
        }.onSuccess { document ->
            onImportDocument(document)
            helperText = "Eerder opgeslagen debug import geladen."
        }.onFailure { error ->
            onImportMessage("Opgeslagen debug import kon niet worden geladen: ${error.message}")
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text("Debug import", style = MaterialTheme.typography.titleSmall)
            Text(
                "Alleen zichtbaar in debug builds. Gebruik canonical JSON om fake stappen en andere health records te tonen.",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = importPayload,
                onValueChange = { importPayload = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                minLines = 10,
                label = { Text("Canonical health import JSON") }
            )
            Text(
                helperText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        runCatching {
                            val document = parseCanonicalHealthImportDocument(importPayload)
                            storeIosImportPayload(importPayload)
                            onImportDocument(document)
                            helperText = "Debug import verwerkt: ${document.records.size} records."
                            onImportMessage("Debug import verwerkt: ${document.records.size} canonical records.")
                        }.onFailure { error ->
                            helperText = "Debug import mislukt: ${error.message}"
                            onImportMessage("Debug import mislukt: ${error.message}")
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Laad fake health data")
            }
        }
    }
}

private const val IOS_DEBUG_IMPORT_KEY = "personal-health.canonical-import.debug.v1"

private fun loadStoredIosImportPayload(): String =
    NSUserDefaults.standardUserDefaults.stringForKey(IOS_DEBUG_IMPORT_KEY).orEmpty()

private fun storeIosImportPayload(payload: String) {
    NSUserDefaults.standardUserDefaults.setObject(payload, IOS_DEBUG_IMPORT_KEY)
}
