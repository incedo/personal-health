package com.incedo.personalhealth.feature.home

import java.io.File

actual object PlatformFitnessActivityPersistenceDriver : FitnessActivityPersistenceDriver {
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
        val home = System.getProperty("user.home").orEmpty().ifBlank { "." }
        return File(File(home), ".personal-health/fitness-activities.json")
    }
}

actual fun currentFitnessEpochMillis(): Long = System.currentTimeMillis()
