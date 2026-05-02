package com.incedo.personalhealth.shared

@JsFun("() => globalThis.location?.search || ''")
private external fun locationSearch(): String

actual fun isOnboardingPreviewRequested(): Boolean {
    return locationSearch()
        .split("&", "?")
        .any { it == "onboarding=preview" || it == "preview=onboarding" }
}
