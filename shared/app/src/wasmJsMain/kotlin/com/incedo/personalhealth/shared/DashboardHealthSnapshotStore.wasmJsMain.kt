package com.incedo.personalhealth.shared

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun dashboardHealthSnapshotGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun dashboardHealthSnapshotSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun dashboardHealthSnapshotRemoveItem(key: String)

actual object PlatformDashboardHealthSnapshotPersistenceDriver : DashboardHealthSnapshotPersistenceDriver {
    private const val KEY = "dashboard_health_snapshot"

    override fun read(): String? = dashboardHealthSnapshotGetItem(KEY)

    override fun write(payload: String) {
        dashboardHealthSnapshotSetItem(KEY, payload)
    }

    override fun clear() {
        dashboardHealthSnapshotRemoveItem(KEY)
    }
}
