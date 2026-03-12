package com.incedo.personalhealth.shared

class IOSSharedUiBridge {
    fun makeRootViewController(): Any = createRootViewController()
    fun startIosHealthHistoryImport() = startIosHealthHistoryImportInternal()
    fun startIosHealthLiveSync() = startIosHealthLiveSyncInternal()
}

expect fun createRootViewController(): Any
expect fun startIosHealthHistoryImportInternal()
expect fun startIosHealthLiveSyncInternal()
