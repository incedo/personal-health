package com.incedo.personalhealth.shared

import kotlinx.serialization.json.Json

internal object DashboardHealthSnapshotCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    fun decode(payload: String): PersistedDashboardHealthSnapshot? = runCatching {
        json.decodeFromString<PersistedDashboardHealthSnapshot>(payload)
    }.getOrNull()

    fun encode(snapshot: PersistedDashboardHealthSnapshot): String =
        json.encodeToString(PersistedDashboardHealthSnapshot.serializer(), snapshot)
}
