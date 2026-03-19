package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
enum class MuscleGroupRegion {
    FRONT,
    BACK
}

@Serializable
data class MuscleGroup(
    val id: String,
    val label: String,
    val region: MuscleGroupRegion,
    val primaryGroupId: String? = null,
    val focusCue: String? = null
)

@Serializable
data class FitnessExercise(
    val id: String,
    val name: String,
    val setCount: Int,
    val repsPerSet: Int,
    val weightKg: Int,
    val primaryMuscleGroupId: String? = null,
    val detailMuscleId: String? = null
)

@Serializable
data class FitnessActivitySession(
    val id: String,
    val title: String,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long,
    val notes: String = "",
    val primaryMuscleGroupId: String? = null,
    val muscleGroups: List<MuscleGroup> = emptyList(),
    val exercises: List<FitnessExercise>
)

interface FitnessActivityPersistenceDriver {
    fun read(): String?
    fun write(payload: String)
    fun clear()
}

interface FitnessActivityStore {
    fun readSessions(): List<FitnessActivitySession>
    fun upsertSession(session: FitnessActivitySession)
    fun deleteSession(sessionId: String)
    fun clear()
}

class PersistedFitnessActivityStore(
    private val driver: FitnessActivityPersistenceDriver
) : FitnessActivityStore {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    override fun readSessions(): List<FitnessActivitySession> = readCatalog()
        .sessions
        .sortedByDescending { it.startedAtEpochMillis }

    override fun upsertSession(session: FitnessActivitySession) {
        val updatedSessions = readCatalog()
            .sessions
            .filterNot { it.id == session.id } + session
        writeCatalog(PersistedFitnessActivityCatalog(updatedSessions.sortedByDescending { it.startedAtEpochMillis }))
    }

    override fun deleteSession(sessionId: String) {
        val updatedSessions = readCatalog()
            .sessions
            .filterNot { it.id == sessionId }
        writeCatalog(PersistedFitnessActivityCatalog(updatedSessions))
    }

    override fun clear() {
        driver.clear()
    }

    private fun readCatalog(): PersistedFitnessActivityCatalog = runCatching {
        val payload = driver.read().orEmpty()
        if (payload.isBlank()) {
            PersistedFitnessActivityCatalog()
        } else {
            json.decodeFromString<PersistedFitnessActivityCatalog>(payload)
        }
    }.getOrDefault(PersistedFitnessActivityCatalog())

    private fun writeCatalog(catalog: PersistedFitnessActivityCatalog) {
        driver.write(json.encodeToString(PersistedFitnessActivityCatalog.serializer(), catalog))
    }
}

@Serializable
internal data class PersistedFitnessActivityCatalog(
    val sessions: List<FitnessActivitySession> = emptyList()
)

expect object PlatformFitnessActivityPersistenceDriver : FitnessActivityPersistenceDriver

expect fun currentFitnessEpochMillis(): Long
