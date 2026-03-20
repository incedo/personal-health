package com.incedo.personalhealth.feature.home

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun trackedActivityStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun trackedActivityStorageSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun trackedActivityStorageRemoveItem(key: String)

actual object PlatformActivityTrackingPersistenceDriver : ActivityTrackingPersistenceDriver {
    private const val KEY = "tracked_activity_sessions"

    override fun read(): String? = trackedActivityStorageGetItem(KEY)

    override fun write(payload: String) {
        trackedActivityStorageSetItem(KEY, payload)
    }

    override fun clear() {
        trackedActivityStorageRemoveItem(KEY)
    }
}
