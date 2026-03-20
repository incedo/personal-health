package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface ActivityTrackingPersistenceDriver {
    fun read(): String?
    fun write(payload: String)
    fun clear()
}

interface ActivityTrackingStore {
    fun readSnapshot(): ActivityTrackingSnapshot
    fun startActivity(type: QuickActivityType, nowEpochMillis: Long): ActivityTrackingSnapshot
    fun stopActiveActivity(nowEpochMillis: Long): ActivityTrackingSnapshot
    fun clear()
}

class PersistedActivityTrackingStore(
    private val driver: ActivityTrackingPersistenceDriver
) : ActivityTrackingStore {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    override fun readSnapshot(): ActivityTrackingSnapshot = readCatalog().toSnapshot()

    override fun startActivity(type: QuickActivityType, nowEpochMillis: Long): ActivityTrackingSnapshot {
        val nextCatalog = PersistedActivityTrackingCatalog(
            activeSession = ActiveQuickActivitySession(
                id = "${type.name.lowercase()}-$nowEpochMillis",
                type = type,
                startedAtEpochMillis = nowEpochMillis
            ),
            completedSessions = readCatalog().completedSessions.sortedByDescending { it.completedAtEpochMillis }
        )
        writeCatalog(nextCatalog)
        return nextCatalog.toSnapshot()
    }

    override fun stopActiveActivity(nowEpochMillis: Long): ActivityTrackingSnapshot {
        val currentCatalog = readCatalog()
        val activeSession = currentCatalog.activeSession ?: return currentCatalog.toSnapshot()
        val completedSession = CompletedQuickActivitySession(
            id = activeSession.id,
            type = activeSession.type,
            startedAtEpochMillis = activeSession.startedAtEpochMillis,
            completedAtEpochMillis = nowEpochMillis
        )
        val nextCatalog = PersistedActivityTrackingCatalog(
            activeSession = null,
            completedSessions = (listOf(completedSession) + currentCatalog.completedSessions)
                .sortedByDescending { it.completedAtEpochMillis }
        )
        writeCatalog(nextCatalog)
        return nextCatalog.toSnapshot()
    }

    override fun clear() {
        driver.clear()
    }

    private fun readCatalog(): PersistedActivityTrackingCatalog = runCatching {
        val payload = driver.read().orEmpty()
        if (payload.isBlank()) {
            PersistedActivityTrackingCatalog()
        } else {
            json.decodeFromString<PersistedActivityTrackingCatalog>(payload)
        }
    }.getOrDefault(PersistedActivityTrackingCatalog())

    private fun writeCatalog(catalog: PersistedActivityTrackingCatalog) {
        driver.write(json.encodeToString(PersistedActivityTrackingCatalog.serializer(), catalog))
    }
}

@Serializable
internal data class PersistedActivityTrackingCatalog(
    val activeSession: ActiveQuickActivitySession? = null,
    val completedSessions: List<CompletedQuickActivitySession> = emptyList()
) {
    fun toSnapshot(): ActivityTrackingSnapshot = ActivityTrackingSnapshot(
        activeSession = activeSession,
        completedSessions = completedSessions.sortedByDescending { it.completedAtEpochMillis }
    )
}

expect object PlatformActivityTrackingPersistenceDriver : ActivityTrackingPersistenceDriver
