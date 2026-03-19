package com.incedo.personalhealth.feature.home

expect object AnatomySelectionEditorStore {
    fun loadOverride(): String?
    fun saveOverride(payload: String)
    fun clearOverride()
}
