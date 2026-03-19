package com.incedo.personalhealth.feature.home

import java.io.File

actual object AnatomySelectionEditorStore {
    private val storageFile = File(
        File(System.getProperty("java.io.tmpdir").ifBlank { "." }),
        "personal-health/anatomy-selection-map-debug.json"
    )

    actual fun loadOverride(): String? = runCatching {
        storageFile.takeIf { it.exists() }?.readText()
    }.getOrNull()

    actual fun saveOverride(payload: String) {
        runCatching {
            storageFile.parentFile?.mkdirs()
            storageFile.writeText(payload)
        }
    }

    actual fun clearOverride() {
        runCatching { if (storageFile.exists()) storageFile.delete() }
    }
}
