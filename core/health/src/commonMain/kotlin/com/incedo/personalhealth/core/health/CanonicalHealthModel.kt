package com.incedo.personalhealth.core.health

data class HealthMetricType(
    val key: String,
    val metricId: String,
    val title: String,
    val dataType: CanonicalHealthDataType,
    val unit: String
) {
    val name: String get() = key

    companion object {
        val STEPS = HealthMetricType("STEPS", "steps", "Stappen", CanonicalHealthDataType.STEPS, "count")
        val HEART_RATE_BPM = HealthMetricType("HEART_RATE_BPM", "heart_rate", "Hartslag", CanonicalHealthDataType.HEART_RATE, "bpm")
        val SLEEP_DURATION_MINUTES = HealthMetricType("SLEEP_DURATION_MINUTES", "sleep", "Slaap", CanonicalHealthDataType.SLEEP_SESSION, "min")
        val ACTIVE_ENERGY_KCAL = HealthMetricType("ACTIVE_ENERGY_KCAL", "active_energy", "Actieve energie", CanonicalHealthDataType.ACTIVE_ENERGY, "kcal")
        val BODY_WEIGHT_KG = HealthMetricType("BODY_WEIGHT_KG", "body_weight", "Gewicht", CanonicalHealthDataType.BODY_WEIGHT, "kg")
        val HEIGHT_CM = HealthMetricType("HEIGHT_CM", "height", "Lengte", CanonicalHealthDataType.HEIGHT, "cm")
        val BODY_FAT_PERCENTAGE = HealthMetricType("BODY_FAT_PERCENTAGE", "body_fat_percentage", "Vetpercentage", CanonicalHealthDataType.BODY_FAT_PERCENTAGE, "%")
        val MUSCLE_MASS_KG = HealthMetricType("MUSCLE_MASS_KG", "muscle_mass", "Spiermassa", CanonicalHealthDataType.MUSCLE_MASS, "kg")
        val BONE_MASS_KG = HealthMetricType("BONE_MASS_KG", "bone_mass", "Botmassa", CanonicalHealthDataType.BONE_MASS, "kg")
        val WATER_MASS_KG = HealthMetricType("WATER_MASS_KG", "water_mass", "Lichaamswater", CanonicalHealthDataType.TOTAL_BODY_WATER, "kg")
        val WATER_PERCENTAGE = HealthMetricType("WATER_PERCENTAGE", "water_percentage", "Waterpercentage", CanonicalHealthDataType.HYDRATION, "%")
        val BODY_MASS_INDEX = HealthMetricType("BODY_MASS_INDEX", "body_mass_index", "BMI", CanonicalHealthDataType.BODY_MASS_INDEX, "kg/m2")
        val BASAL_METABOLIC_RATE_KCAL = HealthMetricType("BASAL_METABOLIC_RATE_KCAL", "basal_metabolic_rate", "Ruststofwisseling", CanonicalHealthDataType.BASAL_METABOLIC_RATE, "kcal")
        val SYSTOLIC_BLOOD_PRESSURE_MMHG = HealthMetricType("SYSTOLIC_BLOOD_PRESSURE_MMHG", "blood_pressure_systolic", "Bovendruk", CanonicalHealthDataType.BLOOD_PRESSURE_SYSTOLIC, "mmHg")
        val DIASTOLIC_BLOOD_PRESSURE_MMHG = HealthMetricType("DIASTOLIC_BLOOD_PRESSURE_MMHG", "blood_pressure_diastolic", "Onderdruk", CanonicalHealthDataType.BLOOD_PRESSURE_DIASTOLIC, "mmHg")
        val BLOOD_GLUCOSE_MGDL = HealthMetricType("BLOOD_GLUCOSE_MGDL", "blood_glucose", "Glucose", CanonicalHealthDataType.BLOOD_GLUCOSE, "mg/dL")
        val OXYGEN_SATURATION_PERCENTAGE = HealthMetricType("OXYGEN_SATURATION_PERCENTAGE", "oxygen_saturation", "Zuurstofsaturatie", CanonicalHealthDataType.OXYGEN_SATURATION, "%")
        val BODY_TEMPERATURE_CELSIUS = HealthMetricType("BODY_TEMPERATURE_CELSIUS", "body_temperature", "Lichaamstemperatuur", CanonicalHealthDataType.BODY_TEMPERATURE, "C")
        val HYDRATION_ML = HealthMetricType("HYDRATION_ML", "hydration", "Hydratatie", CanonicalHealthDataType.HYDRATION, "mL")
        val DIETARY_ENERGY_KCAL = HealthMetricType("DIETARY_ENERGY_KCAL", "dietary_energy", "Voeding", CanonicalHealthDataType.DIETARY_ENERGY, "kcal")

        val entries: List<HealthMetricType> = listOf(
            STEPS,
            HEART_RATE_BPM,
            SLEEP_DURATION_MINUTES,
            ACTIVE_ENERGY_KCAL,
            BODY_WEIGHT_KG,
            HEIGHT_CM,
            BODY_FAT_PERCENTAGE,
            MUSCLE_MASS_KG,
            BONE_MASS_KG,
            WATER_MASS_KG,
            WATER_PERCENTAGE,
            BODY_MASS_INDEX,
            BASAL_METABOLIC_RATE_KCAL,
            SYSTOLIC_BLOOD_PRESSURE_MMHG,
            DIASTOLIC_BLOOD_PRESSURE_MMHG,
            BLOOD_GLUCOSE_MGDL,
            OXYGEN_SATURATION_PERCENTAGE,
            BODY_TEMPERATURE_CELSIUS,
            HYDRATION_ML,
            DIETARY_ENERGY_KCAL
        )

        fun fromSerializedName(value: String): HealthMetricType =
            entries.firstOrNull { metric ->
                metric.key == value || metric.metricId.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("Unsupported health metric: $value")
    }
}

enum class HealthDataSource {
    HEALTH_CONNECT,
    SAMSUNG_HEALTH,
    WITHINGS,
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
    val unit: String = metric.unit,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val source: HealthDataSource,
    val metadata: Map<String, String> = emptyMap()
)

interface HealthDataGateway {
    suspend fun readRecords(request: HealthReadRequest): List<HealthRecord>
}
