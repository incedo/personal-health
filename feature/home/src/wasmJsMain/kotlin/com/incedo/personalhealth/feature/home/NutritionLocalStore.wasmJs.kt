package com.incedo.personalhealth.feature.home

@JsFun("key => globalThis.localStorage.getItem(key)")
private external fun nutritionLocalStorageGetItem(key: String): String?

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun nutritionLocalStorageSetItem(key: String, value: String)

@JsFun("key => globalThis.localStorage.removeItem(key)")
private external fun nutritionLocalStorageRemoveItem(key: String)

@JsFun("() => Date.now()")
private external fun nutritionDateNowEpochMillis(): Double

actual object PlatformNutritionLogPersistenceDriver : NutritionLogPersistenceDriver {
    private const val KEY = "nutrition_log_entries"

    override fun read(): String? = nutritionLocalStorageGetItem(KEY)

    override fun write(payload: String) {
        nutritionLocalStorageSetItem(KEY, payload)
    }

    override fun clear() {
        nutritionLocalStorageRemoveItem(KEY)
    }
}

actual fun currentNutritionEpochMillis(): Long = nutritionDateNowEpochMillis().toLong()
