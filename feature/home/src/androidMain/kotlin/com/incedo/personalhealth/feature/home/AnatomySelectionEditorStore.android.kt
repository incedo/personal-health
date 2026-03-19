package com.incedo.personalhealth.feature.home

actual object AnatomySelectionEditorStore {
    actual fun loadOverride(): String? = null

    actual fun saveOverride(payload: String) = Unit

    actual fun clearOverride() = Unit
}
