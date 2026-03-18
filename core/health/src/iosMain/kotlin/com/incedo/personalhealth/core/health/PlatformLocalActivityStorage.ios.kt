package com.incedo.personalhealth.core.health

actual object PlatformLocalActivityStorage {
    actual fun recommendation(): LocalActivityStorageRecommendation = LocalActivityStorageRecommendation(
        platform = "ios",
        primaryEngine = LocalActivityStorageEngine.SQLITE,
        fallbackEngine = LocalActivityStorageEngine.MEMORY,
        persistence = LocalActivityStoragePersistence.DEVICE_LOCAL_PERSISTENT,
        rationale = "Prefer the same shared SQLite schema used on Android and Desktop. HealthKit stays behind the gateway and the local store keeps only canonical records and sync metadata."
    )
}
