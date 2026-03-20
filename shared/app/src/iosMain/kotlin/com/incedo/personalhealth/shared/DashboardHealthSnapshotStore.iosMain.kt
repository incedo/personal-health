package com.incedo.personalhealth.shared

import platform.Foundation.NSUserDefaults

actual object PlatformDashboardHealthSnapshotPersistenceDriver : DashboardHealthSnapshotPersistenceDriver {
    private const val KEY = "dashboard_health_snapshot"

    override fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    override fun write(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = KEY)
    }

    override fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
    }
}
