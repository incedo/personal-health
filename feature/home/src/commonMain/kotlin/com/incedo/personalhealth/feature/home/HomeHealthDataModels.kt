package com.incedo.personalhealth.feature.home

data class HomeHealthMetricCard(
    val id: String,
    val metricKey: String,
    val domainId: String,
    val title: String,
    val value: String,
    val detail: String,
    val progress: Float,
    val sourceSummary: String,
    val accent: HomeInsightTone
)
