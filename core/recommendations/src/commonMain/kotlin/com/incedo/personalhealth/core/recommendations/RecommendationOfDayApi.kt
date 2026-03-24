package com.incedo.personalhealth.core.recommendations

interface RecommendationOfDayApi {
    suspend fun getRecommendationOfDay(
        request: DailyRecommendationRequest
    ): DailyRecommendation
}
