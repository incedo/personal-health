package com.incedo.personalhealth.feature.home

import kotlin.math.abs
import kotlin.math.sqrt

enum class FitnessBodyProfile {
    MALE,
    FEMALE
}

enum class FitnessBodySide {
    FRONT,
    BACK
}

enum class FitnessPrimaryMuscleGroup(
    val label: String,
    val summary: String,
    val detailPrompt: String
) {
    CHEST(
        label = "Borst",
        summary = "Drukbewegingen en borstvolume",
        detailPrompt = "Werk van bovenste borst naar diepe stretch en koppel daar je presses en fly-varianten aan."
    ),
    SHOULDERS(
        label = "Schouders",
        summary = "Voorkant, zijkant en achterkant van de schouder",
        detailPrompt = "Kies welke deltoid-koppen prioriteit krijgen en bouw daarna presses en raises rond die focus."
    ),
    BACK(
        label = "Rug",
        summary = "Breedte, dikte en scapula controle",
        detailPrompt = "Selecteer of je vandaag meer breedte, middenrug of traps wilt benadrukken."
    ),
    ARMS(
        label = "Armen",
        summary = "Biceps, triceps en onderarmen",
        detailPrompt = "Zoom in op de armkoppen die je wilt raken en combineer compound werk met isolatie."
    ),
    CORE(
        label = "Core",
        summary = "Stabiliteit, rectus en rotatie",
        detailPrompt = "Kies de core-zones die centraal staan en voeg spanning, anti-rotatie en flexie toe."
    ),
    GLUTES(
        label = "Billen",
        summary = "Heupextensie en abductie",
        detailPrompt = "Richt je op kracht in de glutes of juist op stabiliteit rond de heup."
    ),
    LEGS(
        label = "Benen",
        summary = "Quadriceps en hamstrings",
        detailPrompt = "Splits je beendag op in knie-dominant, heup-dominant of een mix van beide."
    ),
    CALVES(
        label = "Kuiten",
        summary = "Explosiviteit en enkelstijfheid",
        detailPrompt = "Bepaal of je meer gastrocnemius of soleus wilt benadrukken."
    )
}

enum class FitnessMuscleDetail(
    val label: String,
    val primaryGroup: FitnessPrimaryMuscleGroup,
    val region: MuscleGroupRegion,
    val focusCue: String
) {
    UPPER_CHEST("Bovenborst", FitnessPrimaryMuscleGroup.CHEST, MuscleGroupRegion.FRONT, "Incline press-hoek en gecontroleerde stretch."),
    MID_CHEST("Middenborst", FitnessPrimaryMuscleGroup.CHEST, MuscleGroupRegion.FRONT, "Klassieke horizontale press-lijn."),
    FRONT_DELTS("Front delts", FitnessPrimaryMuscleGroup.SHOULDERS, MuscleGroupRegion.FRONT, "Overhead kracht en voorste schouderkop."),
    SIDE_DELTS("Side delts", FitnessPrimaryMuscleGroup.SHOULDERS, MuscleGroupRegion.FRONT, "Breedte en laterale spanning."),
    REAR_DELTS("Rear delts", FitnessPrimaryMuscleGroup.SHOULDERS, MuscleGroupRegion.BACK, "Houd scapula stabiel en trek breed uit."),
    TRAPS("Trapezius", FitnessPrimaryMuscleGroup.BACK, MuscleGroupRegion.BACK, "Neklijn, shrug patroon en scapula elevatie."),
    RHOMBOIDS("Rhomboids", FitnessPrimaryMuscleGroup.BACK, MuscleGroupRegion.BACK, "Middenrug dikte en retractie."),
    LATS("Lats", FitnessPrimaryMuscleGroup.BACK, MuscleGroupRegion.BACK, "Breedte, depressie en elleboogbaan dicht langs het lichaam."),
    BICEPS_LONG("Biceps long head", FitnessPrimaryMuscleGroup.ARMS, MuscleGroupRegion.FRONT, "Rek in de onderste positie en smalle armhoek."),
    BICEPS_SHORT("Biceps short head", FitnessPrimaryMuscleGroup.ARMS, MuscleGroupRegion.FRONT, "Meer piekcontractie en brede armhoek."),
    TRICEPS_LONG("Triceps long head", FitnessPrimaryMuscleGroup.ARMS, MuscleGroupRegion.BACK, "Overhead extensie en diepe lockout."),
    TRICEPS_LATERAL("Triceps lateral head", FitnessPrimaryMuscleGroup.ARMS, MuscleGroupRegion.BACK, "Strakke kabel of pressdown lijn."),
    UPPER_ABS("Upper abs", FitnessPrimaryMuscleGroup.CORE, MuscleGroupRegion.FRONT, "Spinale flexie met gecontroleerde spanning."),
    OBLIQUES("Obliques", FitnessPrimaryMuscleGroup.CORE, MuscleGroupRegion.FRONT, "Rotatie en anti-rotatie spanning."),
    GLUTE_MAX("Glute max", FitnessPrimaryMuscleGroup.GLUTES, MuscleGroupRegion.BACK, "Heupextensie en zware lockout."),
    GLUTE_MED("Glute medius", FitnessPrimaryMuscleGroup.GLUTES, MuscleGroupRegion.BACK, "Heupstabiliteit en abductie."),
    QUAD_SWEEP("Quad sweep", FitnessPrimaryMuscleGroup.LEGS, MuscleGroupRegion.FRONT, "Knie-dominante spanning en diepe knieflexie."),
    QUAD_TEARDROP("Vastus medialis", FitnessPrimaryMuscleGroup.LEGS, MuscleGroupRegion.FRONT, "Controle rond lockout en staphoogte."),
    HAMSTRING_LONG("Hamstring long head", FitnessPrimaryMuscleGroup.LEGS, MuscleGroupRegion.BACK, "Hip hinge en lange spierlengte."),
    HAMSTRING_MEDIAL("Hamstring medial", FitnessPrimaryMuscleGroup.LEGS, MuscleGroupRegion.BACK, "Gecontroleerde curl en bekkenstabiliteit."),
    GASTROCNEMIUS("Gastrocnemius", FitnessPrimaryMuscleGroup.CALVES, MuscleGroupRegion.BACK, "Staande kuitvarianten met explosieve top."),
    SOLEUS("Soleus", FitnessPrimaryMuscleGroup.CALVES, MuscleGroupRegion.BACK, "Gebogen knie en lange tijd onder spanning.");
}

fun detailMusclesFor(primaryGroup: FitnessPrimaryMuscleGroup): List<FitnessMuscleDetail> =
    FitnessMuscleDetail.entries.filter { it.primaryGroup == primaryGroup }

data class RgbColor(
    val red: Int,
    val green: Int,
    val blue: Int
)

data class MuscleColorAnchor(
    val detail: FitnessMuscleDetail,
    val centerXFraction: Float,
    val centerYFraction: Float,
    val color: RgbColor
)

internal data class AnatomyPixel(
    val color: RgbColor,
    val alpha: Float
)

internal interface AnatomyPixelSource {
    val width: Int
    val height: Int

    fun pixelAt(x: Int, y: Int): AnatomyPixel
}

internal data class MuscleRegionRun(
    val y: Int,
    val startX: Int,
    val endXInclusive: Int
)

internal data class MuscleRegionMask(
    val centroidXFraction: Float,
    val centroidYFraction: Float,
    val averageColor: RgbColor,
    val averageAlpha: Float,
    val pixelCount: Int,
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
    val runs: List<MuscleRegionRun>
)

internal data class ResolvedMuscleSelection(
    val detail: FitnessMuscleDetail,
    val runtimeSelection: RuntimeMuscleSelection
)

fun fitnessMuscleAnchors(
    bodyProfile: FitnessBodyProfile,
    side: FitnessBodySide
): List<MuscleColorAnchor> = when (bodyProfile) {
    FitnessBodyProfile.MALE -> maleAnchors(side)
    FitnessBodyProfile.FEMALE -> femaleAnchors(side)
}

fun resolveFitnessMuscleDetail(
    bodyProfile: FitnessBodyProfile,
    side: FitnessBodySide,
    normalizedX: Float,
    normalizedY: Float,
    sampledColor: RgbColor?,
    sampledAlpha: Float
): FitnessMuscleDetail? {
    if (sampledColor == null || sampledAlpha < 0.6f || !sampledColor.looksLikeMuscleTint()) {
        return null
    }

    val bestMatch = fitnessMuscleAnchors(bodyProfile, side)
        .map { anchor ->
            val colorDistance = sampledColor.distanceTo(anchor.color) / MAX_COLOR_DISTANCE
            val positionDistance = normalizedDistance(
                normalizedX,
                normalizedY,
                anchor.centerXFraction,
                anchor.centerYFraction
            )
            val score = (colorDistance * 0.72f) + (positionDistance * 0.28f)
            anchor to score
        }
        .minByOrNull { it.second }
        ?: return null

    return if (bestMatch.second <= 0.34f) bestMatch.first.detail else null
}

internal fun resolveMuscleSelectionRegion(
    pixelSource: AnatomyPixelSource,
    normalizedX: Float,
    normalizedY: Float,
    bodyProfile: FitnessBodyProfile,
    side: FitnessBodySide
): ResolvedMuscleSelection? {
    if (pixelSource.width <= 0 || pixelSource.height <= 0) return null
    val seed = findNearestMuscleSeed(
        pixelSource = pixelSource,
        normalizedX = normalizedX,
        normalizedY = normalizedY
    ) ?: return null
    val mask = collectConnectedMuscleRegion(pixelSource, seed.first, seed.second) ?: return null
    val detail = resolveFitnessMuscleDetail(
        bodyProfile = bodyProfile,
        side = side,
        normalizedX = mask.centroidXFraction,
        normalizedY = mask.centroidYFraction,
        sampledColor = mask.averageColor,
        sampledAlpha = mask.averageAlpha
    ) ?: return null
    return ResolvedMuscleSelection(
        detail = detail,
        runtimeSelection = RuntimeMuscleSelection(
            detail = detail,
            mask = mask
        )
    )
}

internal fun collectConnectedMuscleRegion(
    pixelSource: AnatomyPixelSource,
    seedX: Int,
    seedY: Int
): MuscleRegionMask? {
    if (seedX !in 0 until pixelSource.width || seedY !in 0 until pixelSource.height) return null
    val seedPixel = pixelSource.pixelAt(seedX, seedY)
    if (seedPixel.alpha < MIN_MUSCLE_ALPHA || !seedPixel.color.looksLikeMuscleTint()) return null

    val width = pixelSource.width
    val height = pixelSource.height
    val visited = BooleanArray(width * height)
    val queueX = IntArray(width * height)
    val queueY = IntArray(width * height)
    var head = 0
    var tail = 0

    fun enqueue(x: Int, y: Int) {
        val index = y * width + x
        if (visited[index]) return
        visited[index] = true
        queueX[tail] = x
        queueY[tail] = y
        tail += 1
    }

    enqueue(seedX, seedY)

    val points = ArrayList<Int>(1024)
    var redTotal = 0f
    var greenTotal = 0f
    var blueTotal = 0f
    var alphaTotal = 0f
    var xTotal = 0f
    var yTotal = 0f
    var minX = seedX
    var maxX = seedX
    var minY = seedY
    var maxY = seedY

    while (head < tail) {
        val x = queueX[head]
        val y = queueY[head]
        head += 1

        val pixel = pixelSource.pixelAt(x, y)
        if (!pixel.belongsToSeedRegion(seedPixel.color)) continue

        points += x
        points += y
        redTotal += pixel.color.red
        greenTotal += pixel.color.green
        blueTotal += pixel.color.blue
        alphaTotal += pixel.alpha
        xTotal += x.toFloat()
        yTotal += y.toFloat()
        minX = minOf(minX, x)
        maxX = maxOf(maxX, x)
        minY = minOf(minY, y)
        maxY = maxOf(maxY, y)

        if (x > 0) enqueue(x - 1, y)
        if (x < width - 1) enqueue(x + 1, y)
        if (y > 0) enqueue(x, y - 1)
        if (y < height - 1) enqueue(x, y + 1)
    }

    val pointCount = points.size / 2
    if (pointCount < MIN_REGION_PIXEL_COUNT) return null

    val pointsByRow = HashMap<Int, MutableList<Int>>()
    var index = 0
    while (index < points.size) {
        val x = points[index]
        val y = points[index + 1]
        pointsByRow.getOrPut(y) { mutableListOf() }.add(x)
        index += 2
    }

    val sortedRows = pointsByRow.keys.toMutableList().apply { sort() }
    val runs = mutableListOf<MuscleRegionRun>()
    for (row in sortedRows) {
        val xs = pointsByRow[row] ?: continue
        xs.sort()
        var runStart = xs[0]
        var previous = xs[0]
        for (cursor in 1 until xs.size) {
            val current = xs[cursor]
            if (current == previous + 1) {
                previous = current
            } else {
                runs += MuscleRegionRun(y = row, startX = runStart, endXInclusive = previous)
                runStart = current
                previous = current
            }
        }
        runs += MuscleRegionRun(y = row, startX = runStart, endXInclusive = previous)
    }

    return MuscleRegionMask(
        centroidXFraction = (xTotal / pointCount) / (width - 1).coerceAtLeast(1),
        centroidYFraction = (yTotal / pointCount) / (height - 1).coerceAtLeast(1),
        averageColor = RgbColor(
            red = (redTotal / pointCount).toInt(),
            green = (greenTotal / pointCount).toInt(),
            blue = (blueTotal / pointCount).toInt()
        ),
        averageAlpha = alphaTotal / pointCount,
        pixelCount = pointCount,
        minX = minX,
        maxX = maxX,
        minY = minY,
        maxY = maxY,
        runs = runs
    )
}

private fun AnatomyPixel.belongsToSeedRegion(seedColor: RgbColor): Boolean {
    if (alpha < MIN_MUSCLE_ALPHA || !color.looksLikeMuscleTint()) return false
    return color.distanceTo(seedColor) <= CONNECTED_REGION_COLOR_DISTANCE
}

private fun findNearestMuscleSeed(
    pixelSource: AnatomyPixelSource,
    normalizedX: Float,
    normalizedY: Float
): Pair<Int, Int>? {
    val centerX = (normalizedX * (pixelSource.width - 1)).toInt().coerceIn(0, pixelSource.width - 1)
    val centerY = (normalizedY * (pixelSource.height - 1)).toInt().coerceIn(0, pixelSource.height - 1)
    var best: Pair<Int, Int>? = null
    var bestDistance = Int.MAX_VALUE

    for (radius in 0..SEED_SEARCH_RADIUS) {
        for (y in (centerY - radius)..(centerY + radius)) {
            for (x in (centerX - radius)..(centerX + radius)) {
                if (x !in 0 until pixelSource.width || y !in 0 until pixelSource.height) continue
                val pixel = pixelSource.pixelAt(x, y)
                if (pixel.alpha < MIN_MUSCLE_ALPHA || !pixel.color.looksLikeMuscleTint()) continue
                val distance = abs(x - centerX) + abs(y - centerY)
                if (distance < bestDistance) {
                    bestDistance = distance
                    best = x to y
                }
            }
        }
        if (best != null) return best
    }
    return null
}

private fun maleAnchors(side: FitnessBodySide): List<MuscleColorAnchor> = when (side) {
    FitnessBodySide.FRONT -> listOf(
        MuscleColorAnchor(FitnessMuscleDetail.UPPER_CHEST, 0.500f, 0.220f, RgbColor(253, 190, 102)),
        MuscleColorAnchor(FitnessMuscleDetail.MID_CHEST, 0.500f, 0.300f, RgbColor(113, 47, 53)),
        MuscleColorAnchor(FitnessMuscleDetail.FRONT_DELTS, 0.531f, 0.203f, RgbColor(102, 70, 54)),
        MuscleColorAnchor(FitnessMuscleDetail.SIDE_DELTS, 0.500f, 0.215f, RgbColor(135, 74, 29)),
        MuscleColorAnchor(FitnessMuscleDetail.BICEPS_LONG, 0.397f, 0.373f, RgbColor(114, 83, 96)),
        MuscleColorAnchor(FitnessMuscleDetail.BICEPS_SHORT, 0.650f, 0.340f, RgbColor(250, 128, 80)),
        MuscleColorAnchor(FitnessMuscleDetail.UPPER_ABS, 0.530f, 0.398f, RgbColor(116, 87, 105)),
        MuscleColorAnchor(FitnessMuscleDetail.OBLIQUES, 0.523f, 0.427f, RgbColor(28, 37, 45)),
        MuscleColorAnchor(FitnessMuscleDetail.QUAD_SWEEP, 0.484f, 0.670f, RgbColor(124, 90, 103)),
        MuscleColorAnchor(FitnessMuscleDetail.QUAD_TEARDROP, 0.501f, 0.760f, RgbColor(48, 100, 145))
    )

    FitnessBodySide.BACK -> listOf(
        MuscleColorAnchor(FitnessMuscleDetail.REAR_DELTS, 0.475f, 0.208f, RgbColor(123, 56, 29)),
        MuscleColorAnchor(FitnessMuscleDetail.TRAPS, 0.452f, 0.197f, RgbColor(146, 92, 76)),
        MuscleColorAnchor(FitnessMuscleDetail.RHOMBOIDS, 0.500f, 0.270f, RgbColor(198, 106, 134)),
        MuscleColorAnchor(FitnessMuscleDetail.LATS, 0.512f, 0.341f, RgbColor(47, 53, 67)),
        MuscleColorAnchor(FitnessMuscleDetail.TRICEPS_LONG, 0.349f, 0.350f, RgbColor(202, 142, 152)),
        MuscleColorAnchor(FitnessMuscleDetail.TRICEPS_LATERAL, 0.611f, 0.378f, RgbColor(143, 107, 127)),
        MuscleColorAnchor(FitnessMuscleDetail.GLUTE_MAX, 0.500f, 0.560f, RgbColor(248, 229, 100)),
        MuscleColorAnchor(FitnessMuscleDetail.GLUTE_MED, 0.509f, 0.505f, RgbColor(141, 86, 127)),
        MuscleColorAnchor(FitnessMuscleDetail.HAMSTRING_LONG, 0.500f, 0.720f, RgbColor(225, 218, 49)),
        MuscleColorAnchor(FitnessMuscleDetail.HAMSTRING_MEDIAL, 0.500f, 0.729f, RgbColor(192, 201, 52)),
        MuscleColorAnchor(FitnessMuscleDetail.GASTROCNEMIUS, 0.523f, 0.908f, RgbColor(10, 10, 17)),
        MuscleColorAnchor(FitnessMuscleDetail.SOLEUS, 0.496f, 0.911f, RgbColor(68, 53, 57))
    )
}

private fun femaleAnchors(side: FitnessBodySide): List<MuscleColorAnchor> = when (side) {
    FitnessBodySide.FRONT -> listOf(
        MuscleColorAnchor(FitnessMuscleDetail.UPPER_CHEST, 0.500f, 0.229f, RgbColor(235, 124, 53)),
        MuscleColorAnchor(FitnessMuscleDetail.MID_CHEST, 0.500f, 0.310f, RgbColor(39, 24, 21)),
        MuscleColorAnchor(FitnessMuscleDetail.FRONT_DELTS, 0.516f, 0.170f, RgbColor(18, 14, 21)),
        MuscleColorAnchor(FitnessMuscleDetail.SIDE_DELTS, 0.518f, 0.223f, RgbColor(39, 27, 34)),
        MuscleColorAnchor(FitnessMuscleDetail.BICEPS_LONG, 0.419f, 0.392f, RgbColor(82, 60, 62)),
        MuscleColorAnchor(FitnessMuscleDetail.BICEPS_SHORT, 0.639f, 0.350f, RgbColor(233, 86, 131)),
        MuscleColorAnchor(FitnessMuscleDetail.UPPER_ABS, 0.512f, 0.404f, RgbColor(138, 101, 121)),
        MuscleColorAnchor(FitnessMuscleDetail.OBLIQUES, 0.501f, 0.420f, RgbColor(138, 90, 116)),
        MuscleColorAnchor(FitnessMuscleDetail.QUAD_SWEEP, 0.516f, 0.689f, RgbColor(77, 88, 101)),
        MuscleColorAnchor(FitnessMuscleDetail.QUAD_TEARDROP, 0.527f, 0.752f, RgbColor(56, 64, 88))
    )

    FitnessBodySide.BACK -> listOf(
        MuscleColorAnchor(FitnessMuscleDetail.REAR_DELTS, 0.471f, 0.222f, RgbColor(82, 52, 40)),
        MuscleColorAnchor(FitnessMuscleDetail.TRAPS, 0.445f, 0.185f, RgbColor(36, 20, 32)),
        MuscleColorAnchor(FitnessMuscleDetail.RHOMBOIDS, 0.500f, 0.279f, RgbColor(106, 60, 114)),
        MuscleColorAnchor(FitnessMuscleDetail.LATS, 0.510f, 0.354f, RgbColor(18, 22, 16)),
        MuscleColorAnchor(FitnessMuscleDetail.TRICEPS_LONG, 0.359f, 0.350f, RgbColor(200, 66, 130)),
        MuscleColorAnchor(FitnessMuscleDetail.TRICEPS_LATERAL, 0.589f, 0.386f, RgbColor(105, 68, 80)),
        MuscleColorAnchor(FitnessMuscleDetail.GLUTE_MAX, 0.500f, 0.569f, RgbColor(238, 107, 133)),
        MuscleColorAnchor(FitnessMuscleDetail.GLUTE_MED, 0.500f, 0.520f, RgbColor(199, 85, 172)),
        MuscleColorAnchor(FitnessMuscleDetail.HAMSTRING_LONG, 0.500f, 0.729f, RgbColor(179, 188, 59)),
        MuscleColorAnchor(FitnessMuscleDetail.HAMSTRING_MEDIAL, 0.503f, 0.737f, RgbColor(143, 149, 88)),
        MuscleColorAnchor(FitnessMuscleDetail.GASTROCNEMIUS, 0.505f, 0.890f, RgbColor(54, 42, 50)),
        MuscleColorAnchor(FitnessMuscleDetail.SOLEUS, 0.523f, 0.902f, RgbColor(36, 33, 28))
    )
}

private fun RgbColor.looksLikeMuscleTint(): Boolean {
    val maxChannel = maxOf(red, green, blue).toFloat()
    val minChannel = minOf(red, green, blue).toFloat()
    if (maxChannel <= 0f) return false
    val saturation = (maxChannel - minChannel) / maxChannel
    val luminance = ((red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)) / 255f
    return saturation >= 0.18f && luminance in 0.05f..0.92f
}

private fun RgbColor.distanceTo(other: RgbColor): Float {
    val redDelta = (red - other.red).toFloat()
    val greenDelta = (green - other.green).toFloat()
    val blueDelta = (blue - other.blue).toFloat()
    return sqrt((redDelta * redDelta) + (greenDelta * greenDelta) + (blueDelta * blueDelta))
}

private fun normalizedDistance(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float
): Float {
    val dx = x1 - x2
    val dy = y1 - y2
    return sqrt((dx * dx) + (dy * dy))
}

private const val MAX_COLOR_DISTANCE = 441.67294f
private const val CONNECTED_REGION_COLOR_DISTANCE = 32f
private const val MIN_MUSCLE_ALPHA = 0.6f
private const val MIN_REGION_PIXEL_COUNT = 24
private const val SEED_SEARCH_RADIUS = 10
