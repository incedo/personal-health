package com.incedo.personalhealth.core.health

enum class HealthMetricType {
    STEPS,
    HEART_RATE_BPM,
    SLEEP_DURATION_MINUTES,
    ACTIVE_ENERGY_KCAL,
    BODY_WEIGHT_KG
}

enum class HealthDataSource {
    HEALTH_CONNECT,
    HEALTHKIT,
    UNKNOWN
}

data class HealthReadRequest(
    val metrics: Set<HealthMetricType>,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val limit: Int = 1000
)

data class HealthRecord(
    val id: String,
    val metric: HealthMetricType,
    val value: Double,
    val unit: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val source: HealthDataSource,
    val metadata: Map<String, String> = emptyMap()
)

interface HealthDataGateway {
    suspend fun readRecords(request: HealthReadRequest): List<HealthRecord>
}
