package com.incedo.personalhealth.feature.home

import platform.Foundation.NSUserDefaults

actual object PlatformActivityTrackingPersistenceDriver : ActivityTrackingPersistenceDriver {
    private const val KEY = "tracked_activity_sessions"

    override fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    override fun write(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = KEY)
    }

    override fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
    }
}
