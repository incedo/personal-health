package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface NutritionLogPersistenceDriver {
    fun read(): String?
    fun write(payload: String)
    fun clear()
}

interface NutritionLogStore {
    fun readEntries(): List<NutritionLogEntry>
    fun addEntry(entry: NutritionLogEntry)
    fun deleteEntry(entryId: String)
    fun clear()
}

class PersistedNutritionLogStore(
    private val driver: NutritionLogPersistenceDriver
) : NutritionLogStore {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    override fun readEntries(): List<NutritionLogEntry> = readCatalog()
        .entries
        .sortedByDescending { it.createdAtEpochMillis }

    override fun addEntry(entry: NutritionLogEntry) {
        val updated = readCatalog()
            .entries
            .filterNot { it.id == entry.id } + entry
        writeCatalog(PersistedNutritionLogCatalog(updated.sortedByDescending { it.createdAtEpochMillis }))
    }

    override fun deleteEntry(entryId: String) {
        val updated = readCatalog()
            .entries
            .filterNot { it.id == entryId }
        writeCatalog(PersistedNutritionLogCatalog(updated))
    }

    override fun clear() {
        driver.clear()
    }

    private fun readCatalog(): PersistedNutritionLogCatalog = runCatching {
        val payload = driver.read().orEmpty()
        if (payload.isBlank()) {
            PersistedNutritionLogCatalog()
        } else {
            json.decodeFromString<PersistedNutritionLogCatalog>(payload)
        }
    }.getOrDefault(PersistedNutritionLogCatalog())

    private fun writeCatalog(catalog: PersistedNutritionLogCatalog) {
        driver.write(json.encodeToString(PersistedNutritionLogCatalog.serializer(), catalog))
    }
}

@Serializable
internal data class PersistedNutritionLogCatalog(
    val entries: List<NutritionLogEntry> = emptyList()
)

expect object PlatformNutritionLogPersistenceDriver : NutritionLogPersistenceDriver

expect fun currentNutritionEpochMillis(): Long
