package com.incedo.personalhealth.core.recommendations

class StubRecommendationOfDayApi : RecommendationOfDayApi {
    private var callCount: Int = 0

    override suspend fun getRecommendationOfDay(
        request: DailyRecommendationRequest
    ): DailyRecommendation {
        val variant = recommendationVariant(
            request = request,
            variantIndex = callCount++
        )

        return DailyRecommendation(
            title = "Focus van de dag",
            summary = variant.summary,
            guidance = variant.guidance,
            insights = recommendationInsights(request, variantIndex = callCount - 1),
            source = RecommendationSource.STUB
        )
    }
}
