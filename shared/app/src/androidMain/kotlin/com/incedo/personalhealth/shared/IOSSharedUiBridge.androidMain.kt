package com.incedo.personalhealth.shared

actual fun createRootViewController(): Any {
    error("createRootViewController is only available on iOS targets")
}

actual fun startIosHealthHistoryImportInternal() {
    // no-op: iOS-only flow
}

actual fun startIosHealthLiveSyncInternal() {
    // no-op: iOS-only flow
}
