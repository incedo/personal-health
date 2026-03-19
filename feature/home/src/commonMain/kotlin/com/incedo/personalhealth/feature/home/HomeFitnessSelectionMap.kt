package com.incedo.personalhealth.feature.home

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import personal_health.feature.home.generated.resources.Res

@Serializable
data class AnatomySelectionMapDocument(
    val version: Int = 1,
    val regions: List<AnatomySelectionRegionDocument> = emptyList()
)

@Serializable
data class AnatomySelectionRegionDocument(
    val id: String,
    val detailId: String,
    val bodyProfileId: String,
    val bodySideId: String,
    val points: List<AnatomySelectionPointDocument>
)

@Serializable
data class AnatomySelectionPointDocument(
    val x: Float,
    val y: Float
)

data class AnatomySelectionRegion(
    val id: String,
    val detail: FitnessMuscleDetail,
    val bodyProfile: FitnessBodyProfile,
    val bodySide: FitnessBodySide,
    val points: List<AnatomySelectionPoint>
)

data class AnatomySelectionPoint(
    val x: Float,
    val y: Float
)

internal data class PolygonSelectionMask(
    val points: List<AnatomySelectionPoint>
)

internal data class RuntimeMuscleSelection(
    val detail: FitnessMuscleDetail,
    val polygon: PolygonSelectionMask? = null,
    val mask: MuscleRegionMask? = null
)

private val anatomySelectionJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

internal fun emptyAnatomySelectionMap(): AnatomySelectionMapDocument = AnatomySelectionMapDocument()

internal fun AnatomySelectionMapDocument.toRuntimeRegions(): List<AnatomySelectionRegion> =
    regions.mapNotNull { region ->
        val detail = FitnessMuscleDetail.entries.firstOrNull { it.name == region.detailId } ?: return@mapNotNull null
        val bodyProfile = FitnessBodyProfile.entries.firstOrNull { it.name == region.bodyProfileId } ?: return@mapNotNull null
        val bodySide = FitnessBodySide.entries.firstOrNull { it.name == region.bodySideId } ?: return@mapNotNull null
        AnatomySelectionRegion(
            id = region.id,
            detail = detail,
            bodyProfile = bodyProfile,
            bodySide = bodySide,
            points = region.points.map { AnatomySelectionPoint(it.x, it.y) }
        )
    }

internal fun List<AnatomySelectionRegion>.toDocument(): AnatomySelectionMapDocument =
    AnatomySelectionMapDocument(
        regions = map { region ->
            AnatomySelectionRegionDocument(
                id = region.id,
                detailId = region.detail.name,
                bodyProfileId = region.bodyProfile.name,
                bodySideId = region.bodySide.name,
                points = region.points.map { point ->
                    AnatomySelectionPointDocument(
                        x = point.x,
                        y = point.y
                    )
                }
            )
        }
    )

internal fun encodeAnatomySelectionMap(regions: List<AnatomySelectionRegion>): String =
    anatomySelectionJson.encodeToString(AnatomySelectionMapDocument.serializer(), regions.toDocument())

internal fun decodeAnatomySelectionMap(payload: String): List<AnatomySelectionRegion> =
    anatomySelectionJson
        .decodeFromString(AnatomySelectionMapDocument.serializer(), payload)
        .toRuntimeRegions()

@OptIn(ExperimentalResourceApi::class)
internal suspend fun loadBuiltInAnatomySelectionMap(): List<AnatomySelectionRegion> = runCatching {
    val payload = Res.readBytes("files/anatomy_selection_map.json").decodeToString()
    decodeAnatomySelectionMap(payload)
}.getOrElse { emptyList() }

internal suspend fun loadEffectiveAnatomySelectionMap(): List<AnatomySelectionRegion> {
    val builtIn = loadBuiltInAnatomySelectionMap()
    val overridePayload = AnatomySelectionEditorStore.loadOverride().orEmpty()
    if (overridePayload.isBlank()) return builtIn
    val overrideRegions = runCatching { decodeAnatomySelectionMap(overridePayload) }.getOrElse { emptyList() }
    if (overrideRegions.isEmpty()) return builtIn
    return overrideRegions
}

internal fun List<AnatomySelectionRegion>.findRegion(
    bodyProfile: FitnessBodyProfile,
    bodySide: FitnessBodySide,
    normalizedX: Float,
    normalizedY: Float
): AnatomySelectionRegion? =
    asSequence()
        .filter { it.bodyProfile == bodyProfile && it.bodySide == bodySide }
        .filter { pointInPolygon(normalizedX, normalizedY, it.points) }
        .minByOrNull { polygonArea(it.points) }

internal fun nextSuggestedDetail(
    bodySide: FitnessBodySide,
    currentDetail: FitnessMuscleDetail,
    regions: List<AnatomySelectionRegion>
): FitnessMuscleDetail {
    val sideDetails = FitnessMuscleDetail.entries.filter { it.region.name == bodySide.name }
    val configured = regions
        .filter { it.bodySide == bodySide }
        .map { it.detail }
        .toSet()
    val currentIndex = sideDetails.indexOf(currentDetail).coerceAtLeast(0)
    for (offset in 1..sideDetails.size) {
        val candidate = sideDetails[(currentIndex + offset) % sideDetails.size]
        if (candidate !in configured) return candidate
    }
    return sideDetails[(currentIndex + 1) % sideDetails.size]
}

private fun pointInPolygon(
    x: Float,
    y: Float,
    polygon: List<AnatomySelectionPoint>
): Boolean {
    if (polygon.size < 3) return false
    var inside = false
    var previousIndex = polygon.lastIndex
    for (index in polygon.indices) {
        val current = polygon[index]
        val previous = polygon[previousIndex]
        val intersects = ((current.y > y) != (previous.y > y)) &&
            (x < (previous.x - current.x) * (y - current.y) / ((previous.y - current.y).takeIf { it != 0f } ?: 0.0001f) + current.x)
        if (intersects) inside = !inside
        previousIndex = index
    }
    return inside
}

private fun polygonArea(points: List<AnatomySelectionPoint>): Float {
    if (points.size < 3) return Float.MAX_VALUE
    var sum = 0f
    var previous = points.last()
    points.forEach { current ->
        sum += (previous.x * current.y) - (current.x * previous.y)
        previous = current
    }
    return kotlin.math.abs(sum / 2f)
}
