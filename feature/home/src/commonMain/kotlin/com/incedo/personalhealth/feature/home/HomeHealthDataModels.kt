package com.incedo.personalhealth.feature.home

data class HomeHealthMetricCard(
    val id: String,
    val title: String,
    val value: String,
    val detail: String,
    val progress: Float,
    val sourceSummary: String,
    val accent: HomeInsightTone
)
