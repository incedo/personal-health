package com.incedo.personalhealth.feature.home

actual object AnatomySelectionEditorStore {
    private var payload: String? = null

    actual fun loadOverride(): String? = payload

    actual fun saveOverride(payload: String) {
        this.payload = payload
    }

    actual fun clearOverride() {
        payload = null
    }
}
