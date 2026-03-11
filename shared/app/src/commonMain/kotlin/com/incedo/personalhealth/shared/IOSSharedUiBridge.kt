package com.incedo.personalhealth.shared

class IOSSharedUiBridge {
    fun makeRootViewController(): Any = createRootViewController()
}

expect fun createRootViewController(): Any
