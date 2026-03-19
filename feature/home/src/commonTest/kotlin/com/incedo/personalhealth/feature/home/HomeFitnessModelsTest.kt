package com.incedo.personalhealth.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HomeFitnessModelsTest {

    @Test
    fun selectPrimaryGroup_clearsOutOfGroupState() {
        val draft = toggleFitnessExercise(
            toggleFitnessMuscleDetail(
                selectFitnessPrimaryGroup(newFitnessSessionDraft(1), FitnessPrimaryMuscleGroup.CHEST),
                FitnessMuscleDetail.UPPER_CHEST
            ),
            FitnessExerciseTemplate.INCLINE_DUMBBELL_PRESS
        )

        val switched = selectFitnessPrimaryGroup(draft, FitnessPrimaryMuscleGroup.BACK)

        assertEquals(FitnessPrimaryMuscleGroup.BACK, switched.selectedPrimaryGroup)
        assertTrue(switched.selectedMuscleDetails.isEmpty())
        assertTrue(switched.exercises.isEmpty())
    }

    @Test
    fun toggleFitnessExercise_addsDetailAndExercise() {
        val updated = toggleFitnessExercise(
            newFitnessSessionDraft(sessionOrdinal = 1),
            FitnessExerciseTemplate.LAT_PULLDOWN
        )

        assertEquals(FitnessPrimaryMuscleGroup.BACK, updated.selectedPrimaryGroup)
        assertEquals(setOf(FitnessMuscleDetail.LATS), updated.selectedMuscleDetails)
        assertEquals(listOf(FitnessExerciseTemplate.LAT_PULLDOWN), updated.exercises.map { it.template })
    }

    @Test
    fun everyDetailMuscle_hasAtLeastOneExerciseTemplate() {
        FitnessMuscleDetail.entries.forEach { detail ->
            val templates = FitnessExerciseTemplate.entries.filter { it.detailMuscle == detail }
            assertTrue(templates.isNotEmpty(), "Expected exercises for ${detail.name}")
        }
    }

    @Test
    fun fitnessDraftCanSave_requiresPrimaryDetailAndExercise() {
        val empty = newFitnessSessionDraft(sessionOrdinal = 1)
        val filled = toggleFitnessExercise(
            toggleFitnessMuscleDetail(
                selectFitnessPrimaryGroup(newFitnessSessionDraft(sessionOrdinal = 1), FitnessPrimaryMuscleGroup.CHEST),
                FitnessMuscleDetail.MID_CHEST
            ),
            FitnessExerciseTemplate.BENCH_PRESS
        )

        assertFalse(fitnessDraftCanSave(empty))
        assertTrue(fitnessDraftCanSave(filled))
    }

    @Test
    fun buildFitnessSession_generatesTitleWhenBlank() {
        val draft = toggleFitnessExercise(
            toggleFitnessMuscleDetail(
                selectFitnessPrimaryGroup(newFitnessSessionDraft(1), FitnessPrimaryMuscleGroup.SHOULDERS),
                FitnessMuscleDetail.SIDE_DELTS
            ),
            FitnessExerciseTemplate.LATERAL_RAISE
        )

        val session = buildFitnessSession(
            draft = draft,
            sessionId = "fitness-auto",
            startedAtEpochMillis = 10L,
            completedAtEpochMillis = 20L
        )

        assertEquals("Schouders • Side delts", session.title)
    }

    @Test
    fun buildFitnessSession_mapsPrimaryAndDetailMuscles() {
        val draft = updateFitnessDraftNotes(
            updateFitnessDraftTitle(
                toggleFitnessExercise(
                    toggleFitnessMuscleDetail(
                        selectFitnessPrimaryGroup(newFitnessSessionDraft(3), FitnessPrimaryMuscleGroup.LEGS),
                        FitnessMuscleDetail.HAMSTRING_LONG
                    ),
                    FitnessExerciseTemplate.ROMANIAN_DEADLIFT
                ),
                "Posterior chain"
            ),
            "Lange excentrische fase"
        )

        val session = buildFitnessSession(
            draft = draft,
            sessionId = "fitness-123",
            startedAtEpochMillis = 100L,
            completedAtEpochMillis = 200L
        )

        assertEquals(FitnessPrimaryMuscleGroup.LEGS.name, session.primaryMuscleGroupId)
        assertEquals("Posterior chain", session.title)
        assertEquals("Lange excentrische fase", session.notes)
        assertEquals(listOf(FitnessMuscleDetail.HAMSTRING_LONG.label), session.muscleGroups.map { it.label })
        assertEquals(FitnessMuscleDetail.HAMSTRING_LONG.focusCue, session.muscleGroups.single().focusCue)
        assertEquals(FitnessMuscleDetail.HAMSTRING_LONG.name, session.exercises.single().detailMuscleId)
    }

    @Test
    fun fitnessSessionDraftFromSession_restoresEditableState() {
        val session = buildFitnessSession(
            draft = toggleFitnessExercise(
                toggleFitnessMuscleDetail(
                    selectFitnessPrimaryGroup(newFitnessSessionDraft(1), FitnessPrimaryMuscleGroup.SHOULDERS),
                    FitnessMuscleDetail.REAR_DELTS
                ),
                FitnessExerciseTemplate.FACE_PULL
            ),
            sessionId = "fitness-22",
            startedAtEpochMillis = 10L,
            completedAtEpochMillis = 20L
        )

        val draft = fitnessSessionDraftFromSession(session)

        assertEquals("fitness-22", draft.sessionId)
        assertEquals(FitnessPrimaryMuscleGroup.SHOULDERS, draft.selectedPrimaryGroup)
        assertEquals(setOf(FitnessMuscleDetail.REAR_DELTS), draft.selectedMuscleDetails)
        assertEquals(listOf(FitnessExerciseTemplate.FACE_PULL), draft.exercises.map { it.template })
    }

    @Test
    fun exerciseTemplatesFor_filtersByDetailWhenSelected() {
        val templates = exerciseTemplatesFor(
            primaryGroup = FitnessPrimaryMuscleGroup.ARMS,
            selectedDetails = setOf(FitnessMuscleDetail.TRICEPS_LONG)
        )

        assertEquals(
            setOf(FitnessExerciseTemplate.OVERHEAD_TRICEPS_EXTENSION, FitnessExerciseTemplate.SKULL_CRUSHER),
            templates.toSet()
        )
    }

    @Test
    fun exerciseTemplatesByDetail_returnsTemplatesForFocusedGroup() {
        val map = exerciseTemplatesByDetail(
            primaryGroup = FitnessPrimaryMuscleGroup.CALVES,
            selectedDetails = emptySet()
        )

        assertNotNull(map[FitnessMuscleDetail.GASTROCNEMIUS])
        assertNotNull(map[FitnessMuscleDetail.SOLEUS])
    }

    @Test
    fun everyPrimaryGroup_exposesDetailMuscles() {
        FitnessPrimaryMuscleGroup.entries.forEach { primaryGroup ->
            assertTrue(detailMusclesFor(primaryGroup).isNotEmpty(), "Expected details for ${primaryGroup.name}")
        }
    }

    @Test
    fun muscleAnchors_matchSideDetailsForEachProfile() {
        FitnessBodyProfile.entries.forEach { profile ->
            FitnessBodySide.entries.forEach { side ->
                val anchors = fitnessMuscleAnchors(profile, side)
                assertTrue(anchors.isNotEmpty(), "Expected anchors for $profile/$side")
                assertTrue(anchors.all { it.detail.region.name == side.name }, "Anchor side mismatch for $profile/$side")
            }
        }
    }

    @Test
    fun resolveFitnessMuscleDetail_usesColorAndPositionAnchors() {
        val chest = resolveFitnessMuscleDetail(
            bodyProfile = FitnessBodyProfile.MALE,
            side = FitnessBodySide.FRONT,
            normalizedX = 0.5f,
            normalizedY = 0.22f,
            sampledColor = RgbColor(253, 190, 102),
            sampledAlpha = 1f
        )
        val glute = resolveFitnessMuscleDetail(
            bodyProfile = FitnessBodyProfile.FEMALE,
            side = FitnessBodySide.BACK,
            normalizedX = 0.5f,
            normalizedY = 0.569f,
            sampledColor = RgbColor(238, 107, 133),
            sampledAlpha = 1f
        )
        val miss = resolveFitnessMuscleDetail(
            bodyProfile = FitnessBodyProfile.MALE,
            side = FitnessBodySide.FRONT,
            normalizedX = 0.5f,
            normalizedY = 0.1f,
            sampledColor = RgbColor(245, 240, 244),
            sampledAlpha = 1f
        )

        assertEquals(FitnessMuscleDetail.UPPER_CHEST, chest)
        assertEquals(FitnessMuscleDetail.GLUTE_MAX, glute)
        assertEquals(null, miss)
    }

    @Test
    fun collectConnectedMuscleRegion_onlyReturnsAdjacentIsland() {
        val regionColor = RgbColor(253, 190, 102)
        val source = FakePixelSource(
            width = 8,
            height = 6,
            default = AnatomyPixel(RgbColor(250, 250, 250), 0f)
        ).apply {
            fillRect(0, 1, 5, 5, regionColor)
            fillRect(6, 1, 2, 2, regionColor)
        }

        val region = collectConnectedMuscleRegion(source, seedX = 1, seedY = 1)

        assertNotNull(region)
        assertEquals(25, region.pixelCount)
        assertEquals(0, region.minX)
        assertEquals(4, region.maxX)
        assertEquals(1, region.minY)
        assertEquals(5, region.maxY)
    }

    @Test
    fun resolveMuscleSelectionRegion_usesConnectedIslandForDetailResolution() {
        val source = FakePixelSource(
            width = 20,
            height = 20,
            default = AnatomyPixel(RgbColor(250, 250, 250), 0f)
        ).apply {
            fillRect(8, 2, 5, 5, RgbColor(253, 190, 102))
            fillRect(2, 12, 5, 5, RgbColor(253, 190, 102))
        }

        val selection = resolveMuscleSelectionRegion(
            pixelSource = source,
            normalizedX = 0.5f,
            normalizedY = 0.22f,
            bodyProfile = FitnessBodyProfile.MALE,
            side = FitnessBodySide.FRONT
        )

        assertNotNull(selection)
        assertEquals(FitnessMuscleDetail.UPPER_CHEST, selection.detail)
        assertNotNull(selection.runtimeSelection.mask)
        assertTrue(selection.runtimeSelection.mask!!.centroidYFraction < 0.4f)
        assertEquals(25, selection.runtimeSelection.mask!!.pixelCount)
    }

    @Test
    fun anatomySelectionMap_roundTripsThroughJson() {
        val regions = listOf(
            AnatomySelectionRegion(
                id = "male-front-chest",
                detail = FitnessMuscleDetail.UPPER_CHEST,
                bodyProfile = FitnessBodyProfile.MALE,
                bodySide = FitnessBodySide.FRONT,
                points = listOf(
                    AnatomySelectionPoint(0.4f, 0.2f),
                    AnatomySelectionPoint(0.6f, 0.2f),
                    AnatomySelectionPoint(0.6f, 0.32f),
                    AnatomySelectionPoint(0.4f, 0.32f)
                )
            )
        )

        val decoded = decodeAnatomySelectionMap(encodeAnatomySelectionMap(regions))

        assertEquals(regions, decoded)
    }

    @Test
    fun findRegion_returnsSmallestContainingPolygon() {
        val regions = listOf(
            AnatomySelectionRegion(
                id = "outer",
                detail = FitnessMuscleDetail.MID_CHEST,
                bodyProfile = FitnessBodyProfile.MALE,
                bodySide = FitnessBodySide.FRONT,
                points = listOf(
                    AnatomySelectionPoint(0.2f, 0.2f),
                    AnatomySelectionPoint(0.8f, 0.2f),
                    AnatomySelectionPoint(0.8f, 0.8f),
                    AnatomySelectionPoint(0.2f, 0.8f)
                )
            ),
            AnatomySelectionRegion(
                id = "inner",
                detail = FitnessMuscleDetail.UPPER_CHEST,
                bodyProfile = FitnessBodyProfile.MALE,
                bodySide = FitnessBodySide.FRONT,
                points = listOf(
                    AnatomySelectionPoint(0.4f, 0.2f),
                    AnatomySelectionPoint(0.6f, 0.2f),
                    AnatomySelectionPoint(0.6f, 0.35f),
                    AnatomySelectionPoint(0.4f, 0.35f)
                )
            )
        )

        val match = regions.findRegion(
            bodyProfile = FitnessBodyProfile.MALE,
            bodySide = FitnessBodySide.FRONT,
            normalizedX = 0.5f,
            normalizedY = 0.3f
        )

        assertEquals("inner", match?.id)
        assertEquals(FitnessMuscleDetail.UPPER_CHEST, match?.detail)
    }

    @Test
    fun nextSuggestedDetail_prefersUnconfiguredDetailOnSide() {
        val regions = listOf(
            AnatomySelectionRegion(
                id = "front-upper-chest",
                detail = FitnessMuscleDetail.UPPER_CHEST,
                bodyProfile = FitnessBodyProfile.MALE,
                bodySide = FitnessBodySide.FRONT,
                points = listOf(
                    AnatomySelectionPoint(0.4f, 0.2f),
                    AnatomySelectionPoint(0.5f, 0.2f),
                    AnatomySelectionPoint(0.5f, 0.3f)
                )
            )
        )

        val next = nextSuggestedDetail(
            bodySide = FitnessBodySide.FRONT,
            currentDetail = FitnessMuscleDetail.UPPER_CHEST,
            regions = regions
        )

        assertEquals(FitnessMuscleDetail.MID_CHEST, next)
    }
}

private class FakePixelSource(
    override val width: Int,
    override val height: Int,
    default: AnatomyPixel
) : AnatomyPixelSource {
    private val pixels = Array(height) { Array(width) { default } }

    override fun pixelAt(x: Int, y: Int): AnatomyPixel = pixels[y][x]

    fun fillRect(startX: Int, startY: Int, rectWidth: Int, rectHeight: Int, color: RgbColor, alpha: Float = 1f) {
        for (y in startY until (startY + rectHeight)) {
            for (x in startX until (startX + rectWidth)) {
                pixels[y][x] = AnatomyPixel(color, alpha)
            }
        }
    }
}
