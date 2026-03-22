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
        val BODY_FAT_MASS_KG = HealthMetricType("BODY_FAT_MASS_KG", "body_fat_mass", "Vetmassa", CanonicalHealthDataType.BODY_FAT_MASS, "kg")
        val MUSCLE_MASS_KG = HealthMetricType("MUSCLE_MASS_KG", "muscle_mass", "Spiermassa", CanonicalHealthDataType.MUSCLE_MASS, "kg")
        val MUSCLE_PERCENTAGE = HealthMetricType("MUSCLE_PERCENTAGE", "muscle_percentage", "Spierpercentage", CanonicalHealthDataType.MUSCLE_PERCENTAGE, "%")
        val SKELETAL_MUSCLE_PERCENTAGE = HealthMetricType("SKELETAL_MUSCLE_PERCENTAGE", "skeletal_muscle_percentage", "Skeletspierpercentage", CanonicalHealthDataType.SKELETAL_MUSCLE_PERCENTAGE, "%")
        val FAT_FREE_PERCENTAGE = HealthMetricType("FAT_FREE_PERCENTAGE", "fat_free_percentage", "Vetvrije massa %", CanonicalHealthDataType.FAT_FREE_PERCENTAGE, "%")
        val FAT_FREE_MASS_KG = HealthMetricType("FAT_FREE_MASS_KG", "fat_free_mass", "Vetvrije massa", CanonicalHealthDataType.FAT_FREE_MASS, "kg")
        val BONE_MASS_KG = HealthMetricType("BONE_MASS_KG", "bone_mass", "Botmassa", CanonicalHealthDataType.BONE_MASS, "kg")
        val WATER_MASS_KG = HealthMetricType("WATER_MASS_KG", "water_mass", "Lichaamswater", CanonicalHealthDataType.TOTAL_BODY_WATER, "kg")
        val WATER_PERCENTAGE = HealthMetricType("WATER_PERCENTAGE", "water_percentage", "Waterpercentage", CanonicalHealthDataType.HYDRATION, "%")
        val BODY_MASS_INDEX = HealthMetricType("BODY_MASS_INDEX", "body_mass_index", "BMI", CanonicalHealthDataType.BODY_MASS_INDEX, "kg/m2")
        val BASAL_METABOLIC_RATE_KCAL = HealthMetricType("BASAL_METABOLIC_RATE_KCAL", "basal_metabolic_rate", "Ruststofwisseling", CanonicalHealthDataType.BASAL_METABOLIC_RATE, "kcal")
        val SYSTOLIC_BLOOD_PRESSURE_MMHG = HealthMetricType("SYSTOLIC_BLOOD_PRESSURE_MMHG", "blood_pressure_systolic", "Bovendruk", CanonicalHealthDataType.BLOOD_PRESSURE_SYSTOLIC, "mmHg")
        val DIASTOLIC_BLOOD_PRESSURE_MMHG = HealthMetricType("DIASTOLIC_BLOOD_PRESSURE_MMHG", "blood_pressure_diastolic", "Onderdruk", CanonicalHealthDataType.BLOOD_PRESSURE_DIASTOLIC, "mmHg")
        val MEAN_BLOOD_PRESSURE_MMHG = HealthMetricType("MEAN_BLOOD_PRESSURE_MMHG", "blood_pressure_mean", "Gemiddelde druk", CanonicalHealthDataType.BLOOD_PRESSURE_MEAN, "mmHg")
        val PULSE_RATE_BPM = HealthMetricType("PULSE_RATE_BPM", "pulse_rate", "Pols", CanonicalHealthDataType.PULSE_RATE, "bpm")
        val BLOOD_GLUCOSE_MGDL = HealthMetricType("BLOOD_GLUCOSE_MGDL", "blood_glucose", "Glucose", CanonicalHealthDataType.BLOOD_GLUCOSE, "mg/dL")
        val OXYGEN_SATURATION_PERCENTAGE = HealthMetricType("OXYGEN_SATURATION_PERCENTAGE", "oxygen_saturation", "Zuurstofsaturatie", CanonicalHealthDataType.OXYGEN_SATURATION, "%")
        val BODY_TEMPERATURE_CELSIUS = HealthMetricType("BODY_TEMPERATURE_CELSIUS", "body_temperature", "Lichaamstemperatuur", CanonicalHealthDataType.BODY_TEMPERATURE, "C")
        val HYDRATION_ML = HealthMetricType("HYDRATION_ML", "hydration", "Hydratatie", CanonicalHealthDataType.HYDRATION, "mL")
        val DIETARY_ENERGY_KCAL = HealthMetricType("DIETARY_ENERGY_KCAL", "dietary_energy", "Voeding", CanonicalHealthDataType.DIETARY_ENERGY, "kcal")
        val PROTEIN_G = HealthMetricType("PROTEIN_G", "protein", "Eiwit", CanonicalHealthDataType.PROTEIN, "g")
        val CARBOHYDRATE_G = HealthMetricType("CARBOHYDRATE_G", "carbohydrate", "Koolhydraten", CanonicalHealthDataType.CARBOHYDRATE, "g")
        val TOTAL_FAT_G = HealthMetricType("TOTAL_FAT_G", "total_fat", "Vet totaal", CanonicalHealthDataType.TOTAL_FAT, "g")
        val SATURATED_FAT_G = HealthMetricType("SATURATED_FAT_G", "saturated_fat", "Verzadigd vet", CanonicalHealthDataType.SATURATED_FAT, "g")
        val POLYUNSATURATED_FAT_G = HealthMetricType("POLYUNSATURATED_FAT_G", "polyunsaturated_fat", "Meervoudig onverzadigd vet", CanonicalHealthDataType.POLYUNSATURATED_FAT, "g")
        val MONOUNSATURATED_FAT_G = HealthMetricType("MONOUNSATURATED_FAT_G", "monounsaturated_fat", "Enkelvoudig onverzadigd vet", CanonicalHealthDataType.MONOUNSATURATED_FAT, "g")
        val TRANS_FAT_G = HealthMetricType("TRANS_FAT_G", "trans_fat", "Transvet", CanonicalHealthDataType.TRANS_FAT, "g")
        val DIETARY_FIBER_G = HealthMetricType("DIETARY_FIBER_G", "dietary_fiber", "Vezels", CanonicalHealthDataType.DIETARY_FIBER, "g")
        val SUGAR_G = HealthMetricType("SUGAR_G", "sugar", "Suikers", CanonicalHealthDataType.SUGAR, "g")
        val CHOLESTEROL_MG = HealthMetricType("CHOLESTEROL_MG", "cholesterol", "Cholesterol", CanonicalHealthDataType.CHOLESTEROL, "mg")
        val SODIUM_MG = HealthMetricType("SODIUM_MG", "sodium", "Natrium", CanonicalHealthDataType.SODIUM, "mg")
        val POTASSIUM_MG = HealthMetricType("POTASSIUM_MG", "potassium", "Kalium", CanonicalHealthDataType.POTASSIUM, "mg")
        val CALCIUM_MG = HealthMetricType("CALCIUM_MG", "calcium", "Calcium", CanonicalHealthDataType.CALCIUM, "mg")
        val IRON_MG = HealthMetricType("IRON_MG", "iron", "IJzer", CanonicalHealthDataType.IRON, "mg")
        val VITAMIN_A_MCG = HealthMetricType("VITAMIN_A_MCG", "vitamin_a", "Vitamine A", CanonicalHealthDataType.VITAMIN_A, "mcg")
        val VITAMIN_C_MG = HealthMetricType("VITAMIN_C_MG", "vitamin_c", "Vitamine C", CanonicalHealthDataType.VITAMIN_C, "mg")

        val entries: List<HealthMetricType> = listOf(
            STEPS,
            HEART_RATE_BPM,
            SLEEP_DURATION_MINUTES,
            ACTIVE_ENERGY_KCAL,
            BODY_WEIGHT_KG,
            HEIGHT_CM,
            BODY_FAT_PERCENTAGE,
            BODY_FAT_MASS_KG,
            MUSCLE_MASS_KG,
            MUSCLE_PERCENTAGE,
            SKELETAL_MUSCLE_PERCENTAGE,
            FAT_FREE_PERCENTAGE,
            FAT_FREE_MASS_KG,
            BONE_MASS_KG,
            WATER_MASS_KG,
            WATER_PERCENTAGE,
            BODY_MASS_INDEX,
            BASAL_METABOLIC_RATE_KCAL,
            SYSTOLIC_BLOOD_PRESSURE_MMHG,
            DIASTOLIC_BLOOD_PRESSURE_MMHG,
            MEAN_BLOOD_PRESSURE_MMHG,
            PULSE_RATE_BPM,
            BLOOD_GLUCOSE_MGDL,
            OXYGEN_SATURATION_PERCENTAGE,
            BODY_TEMPERATURE_CELSIUS,
            HYDRATION_ML,
            DIETARY_ENERGY_KCAL,
            PROTEIN_G,
            CARBOHYDRATE_G,
            TOTAL_FAT_G,
            SATURATED_FAT_G,
            POLYUNSATURATED_FAT_G,
            MONOUNSATURATED_FAT_G,
            TRANS_FAT_G,
            DIETARY_FIBER_G,
            SUGAR_G,
            CHOLESTEROL_MG,
            SODIUM_MG,
            POTASSIUM_MG,
            CALCIUM_MG,
            IRON_MG,
            VITAMIN_A_MCG,
            VITAMIN_C_MG
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
