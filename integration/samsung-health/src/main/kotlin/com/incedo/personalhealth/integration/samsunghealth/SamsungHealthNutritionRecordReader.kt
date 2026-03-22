package com.incedo.personalhealth.integration.samsunghealth

import com.incedo.personalhealth.core.health.HealthMetricType
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.Ordering

internal val samsungNutritionMetrics = setOf(
    HealthMetricType.DIETARY_ENERGY_KCAL,
    HealthMetricType.PROTEIN_G,
    HealthMetricType.CARBOHYDRATE_G,
    HealthMetricType.TOTAL_FAT_G,
    HealthMetricType.SATURATED_FAT_G,
    HealthMetricType.POLYUNSATURATED_FAT_G,
    HealthMetricType.MONOUNSATURATED_FAT_G,
    HealthMetricType.TRANS_FAT_G,
    HealthMetricType.DIETARY_FIBER_G,
    HealthMetricType.SUGAR_G,
    HealthMetricType.CHOLESTEROL_MG,
    HealthMetricType.SODIUM_MG,
    HealthMetricType.POTASSIUM_MG,
    HealthMetricType.CALCIUM_MG,
    HealthMetricType.IRON_MG,
    HealthMetricType.VITAMIN_A_MCG,
    HealthMetricType.VITAMIN_C_MG
)

internal suspend fun readNutritionRecords(
    store: HealthDataStore,
    request: HealthReadRequest
): List<HealthRecord> = store.readData(
    DataTypes.NUTRITION.readDataRequestBuilder
        .setLocalTimeFilter(request.toLocalTimeFilter())
        .setOrdering(Ordering.DESC)
        .setLimit(request.limit)
        .build()
).dataList.flatMap { point ->
    val start = point.startTime?.toEpochMilli() ?: return@flatMap emptyList()
    val end = (point.endTime ?: point.startTime)?.toEpochMilli() ?: start
    buildList {
        addMetricIfRequested(request, point, DataType.NutritionType.CALORIES, HealthMetricType.DIETARY_ENERGY_KCAL, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.PROTEIN, HealthMetricType.PROTEIN_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.CARBOHYDRATE, HealthMetricType.CARBOHYDRATE_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.TOTAL_FAT, HealthMetricType.TOTAL_FAT_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.SATURATED_FAT, HealthMetricType.SATURATED_FAT_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.POLYSATURATED_FAT, HealthMetricType.POLYUNSATURATED_FAT_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.MONOSATURATED_FAT, HealthMetricType.MONOUNSATURATED_FAT_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.TRANS_FAT, HealthMetricType.TRANS_FAT_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.DIETARY_FIBER, HealthMetricType.DIETARY_FIBER_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.SUGAR, HealthMetricType.SUGAR_G, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.CHOLESTEROL, HealthMetricType.CHOLESTEROL_MG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.SODIUM, HealthMetricType.SODIUM_MG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.POTASSIUM, HealthMetricType.POTASSIUM_MG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.CALCIUM, HealthMetricType.CALCIUM_MG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.IRON, HealthMetricType.IRON_MG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.VITAMIN_A, HealthMetricType.VITAMIN_A_MCG, start, end)
        addMetricIfRequested(request, point, DataType.NutritionType.VITAMIN_C, HealthMetricType.VITAMIN_C_MG, start, end)
    }
}
