package com.incedo.personalhealth.shared

import java.io.File

actual object ProfilePreferenceStore {
    private val preferenceFile = resolvePreferenceFile()
    private val schemaVersionFile = resolveSchemaVersionFile()

    actual fun fitnessBodyProfileId(): String? = runCatching {
        preferenceFile.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.isNotBlank() }
    }.getOrNull()

    actual fun setFitnessBodyProfileId(profileId: String) {
        runCatching {
            preferenceFile.parentFile?.mkdirs()
            preferenceFile.writeText(profileId)
        }
    }

    actual fun homeStorageSchemaVersion(): String? = runCatching {
        schemaVersionFile.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.isNotBlank() }
    }.getOrNull()

    actual fun setHomeStorageSchemaVersion(version: String) {
        runCatching {
            schemaVersionFile.parentFile?.mkdirs()
            schemaVersionFile.writeText(version)
        }
    }

    private fun resolvePreferenceFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()

        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/profile-body.pref")
    }

    private fun resolveSchemaVersionFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()

        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/home-storage-schema.pref")
    }
}
