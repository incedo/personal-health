package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersistedNutritionLogStoreTest {

    @Test
    fun addAndReadEntries_returnsEntriesSortedByCreatedAt() {
        val driver = InMemoryNutritionDriver()
        val store = PersistedNutritionLogStore(driver)

        store.addEntry(entry(id = "nutrition-1", createdAt = 100L))
        store.addEntry(entry(id = "nutrition-2", createdAt = 300L))

        val entries = store.readEntries()

        assertEquals(listOf("nutrition-2", "nutrition-1"), entries.map { it.id })
    }

    @Test
    fun addEntry_replacesExistingEntry() {
        val driver = InMemoryNutritionDriver()
        val store = PersistedNutritionLogStore(driver)

        store.addEntry(entry(id = "nutrition-1", title = "Ontbijt"))
        store.addEntry(entry(id = "nutrition-1", title = "Lunch"))

        val entries = store.readEntries()

        assertEquals(1, entries.size)
        assertEquals("Lunch", entries.single().title)
    }

    @Test
    fun deleteAndClear_removePersistedEntries() {
        val driver = InMemoryNutritionDriver()
        val store = PersistedNutritionLogStore(driver)
        store.addEntry(entry(id = "nutrition-1"))
        store.addEntry(entry(id = "nutrition-2"))

        store.deleteEntry("nutrition-1")
        assertEquals(listOf("nutrition-2"), store.readEntries().map { it.id })

        store.clear()
        assertTrue(store.readEntries().isEmpty())
    }

    private fun entry(
        id: String,
        title: String = "Nutrition log",
        createdAt: Long = 100L
    ): NutritionLogEntry = NutritionLogEntry(
        id = id,
        createdAtEpochMillis = createdAt,
        title = title,
        details = NutritionLogDetails(
            posterName = "Mila",
            posterHandle = "@mila.health",
            note = "Voorbeeld",
            photos = listOf(
                NutritionPhoto("Foto 1", "data:image/png;base64,Zm9v"),
                NutritionPhoto("Foto 2", null)
            ),
            macroMetrics = listOf(NutritionMetric("Eiwit", "30 g", "140 g", "40 g")),
            microMetrics = listOf(NutritionMetric("Vezels", "8 g", "30 g", "6 g")),
            recipeSections = listOf(
                NutritionRecipeSection(
                    title = "Gang 1",
                    sourceUrl = "https://example.com",
                    ingredients = listOf("Quinoa"),
                    steps = listOf("Roer door")
                )
            )
        )
    )

    private class InMemoryNutritionDriver : NutritionLogPersistenceDriver {
        private var payload: String? = null

        override fun read(): String? = payload

        override fun write(payload: String) {
            this.payload = payload
        }

        override fun clear() {
            payload = null
        }
    }
}
