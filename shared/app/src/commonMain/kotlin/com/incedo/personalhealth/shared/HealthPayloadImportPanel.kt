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
import com.incedo.personalhealth.core.health.parseHealthImportPayload
import kotlinx.coroutines.launch

@Composable
internal fun HealthPayloadImportPanel(
    title: String,
    description: String,
    initialPayload: String,
    onPersistPayload: (String) -> Unit,
    onImportDocument: suspend (CanonicalHealthImportDocument) -> Unit,
    onImportMessage: suspend (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var importPayload by rememberSaveable { mutableStateOf(initialPayload) }
    var helperText by rememberSaveable {
        mutableStateOf("Plak canonical JSON of Withings CSV om health data te importeren.")
    }

    LaunchedEffect(initialPayload) {
        if (initialPayload.isBlank()) return@LaunchedEffect
        runCatching { parseHealthImportPayload(initialPayload) }
            .onSuccess { document ->
                onImportDocument(document)
                helperText = "Eerder opgeslagen import geladen."
            }
            .onFailure { error ->
                onImportMessage("Opgeslagen import kon niet worden geladen: ${error.message}")
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
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(
                value = importPayload,
                onValueChange = { importPayload = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                minLines = 10,
                label = { Text("Canonical JSON of Withings CSV") }
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
                            val document = parseHealthImportPayload(importPayload)
                            onPersistPayload(importPayload)
                            onImportDocument(document)
                            helperText = "Import verwerkt: ${document.records.size} records."
                            onImportMessage("Import verwerkt: ${document.records.size} records.")
                        }.onFailure { error ->
                            helperText = "Import mislukt: ${error.message}"
                            onImportMessage("Import mislukt: ${error.message}")
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Importeer health data")
            }
        }
    }
}
