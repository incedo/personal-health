package com.incedo.personalhealth.feature.home

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun fitnessLocalStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun fitnessLocalStorageSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun fitnessLocalStorageRemoveItem(key: String)

@JsFun("() => Date.now()")
private external fun fitnessDateNowEpochMillis(): Double

actual object PlatformFitnessActivityPersistenceDriver : FitnessActivityPersistenceDriver {
    private const val KEY = "fitness_activity_sessions"

    override fun read(): String? = fitnessLocalStorageGetItem(KEY)

    override fun write(payload: String) {
        fitnessLocalStorageSetItem(KEY, payload)
    }

    override fun clear() {
        fitnessLocalStorageRemoveItem(KEY)
    }
}

actual fun currentFitnessEpochMillis(): Long = fitnessDateNowEpochMillis().toLong()
