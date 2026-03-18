package com.incedo.personalhealth.core.health

actual object PlatformLocalActivityStorage {
    actual fun recommendation(): LocalActivityStorageRecommendation = LocalActivityStorageRecommendation(
        platform = "web",
        primaryEngine = LocalActivityStorageEngine.SQLITE_WASM,
        fallbackEngine = LocalActivityStorageEngine.INDEXED_DB,
        persistence = LocalActivityStoragePersistence.BROWSER_LOCAL_PERSISTENT,
        rationale = "Use SQLite compiled to WebAssembly when browser persistence support is good enough, ideally with OPFS-backed storage. Fall back to IndexedDB behind the same repository contract when SQLite WASM integration or browser support is insufficient."
    )
}
