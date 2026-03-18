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
    val scope = rememberCoroutineScope()
    var importPayload by rememberSaveable { mutableStateOf(localStorageGetItem(WEB_IMPORT_STORAGE_KEY).orEmpty()) }
    var helperText by rememberSaveable { mutableStateOf("Plak hier een canonical health import JSON-document voor web.") }

    LaunchedEffect(Unit) {
        val storedPayload = localStorageGetItem(WEB_IMPORT_STORAGE_KEY).orEmpty()
        if (storedPayload.isBlank()) return@LaunchedEffect

        runCatching {
            parseCanonicalHealthImportDocument(storedPayload)
        }.onSuccess { document ->
            onImportDocument(document)
            helperText = "Eerder geïmporteerde webdata geladen."
        }.onFailure { error ->
            onImportMessage("Opgeslagen webimport kon niet worden geladen: ${error.message}")
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
            Text("Web import", style = MaterialTheme.typography.titleSmall)
            Text(
                "Gebruik canonical JSON als browserbron voor stappen en andere health records.",
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
                            localStorageSetItem(WEB_IMPORT_STORAGE_KEY, importPayload)
                            onImportDocument(document)
                            helperText = "Import verwerkt: ${document.records.size} canonical records."
                            onImportMessage("Web import verwerkt: ${document.records.size} records.")
                        }.onFailure { error ->
                            helperText = "Import mislukt: ${error.message}"
                            onImportMessage("Web import mislukt: ${error.message}")
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Importeer webdata")
            }
        }
    }
}
