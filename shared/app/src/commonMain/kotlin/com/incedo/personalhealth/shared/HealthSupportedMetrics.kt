package com.incedo.personalhealth.shared

internal val supportedHealthMetricLabels = listOf(
    "stappen",
    "hartslag",
    "slaap",
    "actieve energie",
    "gewicht",
    "body composition",
    "bloeddruk"
)

internal fun supportedHealthMetricSummary(): String = supportedHealthMetricLabels.joinToString()
