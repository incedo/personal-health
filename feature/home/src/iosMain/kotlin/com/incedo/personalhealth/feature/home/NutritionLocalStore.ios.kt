package com.incedo.personalhealth.feature.home

import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
import platform.Foundation.timeIntervalSince1970

actual object PlatformNutritionLogPersistenceDriver : NutritionLogPersistenceDriver {
    private const val KEY = "nutrition_log_entries"

    override fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    override fun write(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = KEY)
    }

    override fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
    }
}

actual fun currentNutritionEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
