package com.incedo.personalhealth.feature.home

enum class HomeWeightRange(val label: String) {
    WEEK("Week"),
    MONTH("Maand"),
    QUARTER("Kwartaal"),
    SEMESTER("Semester"),
    YEAR("Jaar"),
    ALL("Alles")
}

data class HomeWeightTimeline(
    val range: HomeWeightRange,
    val title: String,
    val points: List<WeightTimelinePoint>
)

data class HomeWeightChartCatalog(
    val timelines: Map<HomeWeightRange, HomeWeightTimeline>
) {
    fun timelineFor(range: HomeWeightRange): HomeWeightTimeline =
        timelines[range] ?: HomeWeightTimeline(range = range, title = range.label, points = emptyList())
}
