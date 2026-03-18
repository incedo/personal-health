package com.incedo.personalhealth.core.health

actual object PlatformLocalActivityStorage {
    actual fun recommendation(): LocalActivityStorageRecommendation = LocalActivityStorageRecommendation(
        platform = "android",
        primaryEngine = LocalActivityStorageEngine.SQLITE,
        fallbackEngine = LocalActivityStorageEngine.MEMORY,
        persistence = LocalActivityStoragePersistence.DEVICE_LOCAL_PERSISTENT,
        rationale = "Use a shared SQLite schema on Android and back it with a native driver. Keep Health Connect mapping in the integration layer and persist only canonical HealthRecord values."
    )
}
