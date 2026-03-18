package com.incedo.personalhealth.core.health

actual object PlatformLocalActivityStorage {
    actual fun recommendation(): LocalActivityStorageRecommendation = LocalActivityStorageRecommendation(
        platform = "desktop",
        primaryEngine = LocalActivityStorageEngine.SQLITE,
        fallbackEngine = LocalActivityStorageEngine.MEMORY,
        persistence = LocalActivityStoragePersistence.DEVICE_LOCAL_PERSISTENT,
        rationale = "Desktop can use the same shared SQLite schema as mobile with a JVM driver, which keeps query behavior aligned across form factors."
    )
}
