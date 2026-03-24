package com.incedo.personalhealth.core.coaches

data class CoachSearchItem(
    val id: String,
    val name: String,
    val companyName: String,
    val type: CoachType,
    val location: String,
    val imageDataUrl: String? = null
)

object CoachSearchStubService {
    private val catalog = listOf(
        CoachSearchItem(
            id = "coach-stub-1",
            name = "Sanne Vermeer",
            companyName = "Peak Motion Studio",
            type = CoachType.PERSONAL_TRAINER,
            location = "Utrecht"
        ),
        CoachSearchItem(
            id = "coach-stub-2",
            name = "Milan de Groot",
            companyName = "Fuel Forward",
            type = CoachType.DIETITIAN,
            location = "Amsterdam"
        ),
        CoachSearchItem(
            id = "coach-stub-3",
            name = "Naomi Chen",
            companyName = "Circadian Lab",
            type = CoachType.LIFESTYLE_COACH,
            location = "Rotterdam"
        ),
        CoachSearchItem(
            id = "coach-stub-4",
            name = "Coach Nova",
            companyName = "Personal Health AI",
            type = CoachType.AI_COACH,
            location = "In app"
        ),
        CoachSearchItem(
            id = "coach-stub-5",
            name = "Jasper Malik",
            companyName = "Atlas Performance",
            type = CoachType.PERSONAL_TRAINER,
            location = "Eindhoven"
        ),
        CoachSearchItem(
            id = "coach-stub-6",
            name = "Lotte van Rijn",
            companyName = "Restore Collective",
            type = CoachType.LIFESTYLE_COACH,
            location = "Den Haag"
        )
    )

    fun search(query: String): List<CoachSearchItem> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return catalog
        return catalog.filter { item ->
            item.name.lowercase().contains(normalizedQuery) ||
                item.companyName.lowercase().contains(normalizedQuery) ||
                coachTypeLabel(item.type).lowercase().contains(normalizedQuery) ||
                item.location.lowercase().contains(normalizedQuery)
        }
    }
}
