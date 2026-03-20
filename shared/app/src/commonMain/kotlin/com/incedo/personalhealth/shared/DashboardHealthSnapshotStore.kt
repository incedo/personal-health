package com.incedo.personalhealth.shared

internal interface DashboardHealthSnapshotPersistenceDriver {
    fun read(): String?
    fun write(payload: String)
    fun clear()
}

internal class PersistedDashboardHealthSnapshotStore(
    private val driver: DashboardHealthSnapshotPersistenceDriver
) {
    fun readUiState(): DashboardHealthUiState =
        DashboardHealthSnapshotCodec.decode(driver.read().orEmpty())?.toUiState() ?: emptyDashboardHealthUiState()

    fun writeSnapshot(snapshot: DashboardHealthSnapshot) {
        driver.write(DashboardHealthSnapshotCodec.encode(snapshot.toPersistedSnapshot()))
    }

    fun clear() {
        driver.clear()
    }
}

internal fun readPersistedDashboardHealthUiState(): DashboardHealthUiState =
    PersistedDashboardHealthSnapshotStore(PlatformDashboardHealthSnapshotPersistenceDriver).readUiState()

internal fun persistDashboardHealthUiState(
    event: com.incedo.personalhealth.core.health.HealthEvent.DashboardRecordsUpdated
): DashboardHealthUiState {
    val snapshot = buildDashboardHealthSnapshot(event)
    PersistedDashboardHealthSnapshotStore(PlatformDashboardHealthSnapshotPersistenceDriver).writeSnapshot(snapshot)
    return snapshot.uiState
}

expect object PlatformDashboardHealthSnapshotPersistenceDriver : DashboardHealthSnapshotPersistenceDriver
