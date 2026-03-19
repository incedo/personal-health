package com.incedo.personalhealth.feature.home

import platform.Foundation.NSUserDefaults

actual object AnatomySelectionEditorStore {
    private const val KEY = "personal-health.anatomy-selection-map.debug"

    actual fun loadOverride(): String? = NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    actual fun saveOverride(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = KEY)
    }

    actual fun clearOverride() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY)
    }
}
