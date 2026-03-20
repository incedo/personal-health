package com.incedo.personalhealth.feature.home

import java.io.File

actual object PlatformNutritionLogPersistenceDriver : NutritionLogPersistenceDriver {
    private val storageFile = resolveStorageFile()

    override fun read(): String? = runCatching {
        storageFile.takeIf { it.exists() }?.readText()
    }.getOrNull()

    override fun write(payload: String) {
        runCatching {
            storageFile.parentFile?.mkdirs()
            storageFile.writeText(payload)
        }
    }

    override fun clear() {
        runCatching {
            if (storageFile.exists()) storageFile.delete()
        }
    }

    private fun resolveStorageFile(): File {
        val roots = listOf(
            System.getProperty("java.io.tmpdir"),
            System.getProperty("user.home"),
            "."
        ).filterNotNull()
        val root = roots.firstOrNull { it.isNotBlank() } ?: "."
        return File(File(root), "personal-health/nutrition-logs.json")
    }
}

actual fun currentNutritionEpochMillis(): Long = System.currentTimeMillis()
