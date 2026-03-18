package com.incedo.personalhealth.feature.home

import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
import platform.Foundation.timeIntervalSince1970

actual object PlatformFitnessActivityPersistenceDriver : FitnessActivityPersistenceDriver {
    private const val KEY = "fitness_activity_sessions"

    override fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    override fun write(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = KEY)
    }

    override fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
    }
}

actual fun currentFitnessEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
