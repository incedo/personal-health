package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import personal_health.feature.home.generated.resources.Res
import personal_health.feature.home.generated.resources.anatomy_female_back
import personal_health.feature.home.generated.resources.anatomy_female_front
import personal_health.feature.home.generated.resources.anatomy_male_back
import personal_health.feature.home.generated.resources.anatomy_male_front

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FitnessActivityDetailScreen(
    sessions: List<FitnessActivitySession>,
    bodyProfile: FitnessBodyProfile,
    onBack: () -> Unit,
    onOpenDebugEditor: () -> Unit,
    onSaveSession: (FitnessActivitySession) -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    var bodySide by rememberSaveable { mutableStateOf(FitnessBodySide.FRONT) }
    var selectedDetails by rememberSaveable(bodyProfile) { mutableStateOf(emptySet<FitnessMuscleDetail>()) }
    val selectedRegions = remember(bodyProfile) { mutableStateMapOf<FitnessMuscleDetail, RuntimeMuscleSelection>() }
    val mappedRegions by produceState(initialValue = emptyList<AnatomySelectionRegion>(), bodyProfile) {
        value = loadEffectiveAnatomySelectionMap()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val showDualView = maxWidth >= 860.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(if (compact) 14.dp else 18.dp)
        ) {
            HomeHeroCard(
                eyebrow = "Fitness",
                title = "Anatomie als vertrekpunt",
                subtitle = "De oude detailpagina is gereset. We starten opnieuw met het juiste anatomiebeeld als basis voor de spierselectie.",
                accent = palette.warm,
                compact = compact,
                sideContent = {
                    HomeStatusBadge(
                        label = "Profiel",
                        value = if (bodyProfile == FitnessBodyProfile.MALE) "Man" else "Vrouw"
                    )
                }
            )

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.surfaceRaised,
                    contentColor = palette.textPrimary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Terug naar home")
            }

            if (HomeBuildFlags.isDebugEditorEnabled) {
                Button(
                    onClick = onOpenDebugEditor,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentSoft,
                        contentColor = palette.textPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Open debug selectie-editor")
                }
            }

            HomePanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Basisbeeld",
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Het lichaamsprofiel komt uit Profiel. Hier wissel je alleen tussen voorkant en achterkant.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (!showDualView) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FitnessBodySide.entries.forEach { side ->
                        val selected = side == bodySide
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = if (selected) palette.warmSoft else palette.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) palette.warm else palette.surfaceMuted
                            ),
                            onClick = { bodySide = side }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (side == FitnessBodySide.FRONT) "Voorkant" else "Achterkant",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.textPrimary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                AnatomyImagePanel(
                    bodyProfile = bodyProfile,
                    bodySide = bodySide,
                    label = if (bodySide == FitnessBodySide.FRONT) "Voorkant" else "Achterkant",
                    selectedDetails = selectedDetails,
                    selectedRegions = selectedRegions,
                    mappedRegions = mappedRegions,
                    onToggleDetail = { selection ->
                        selectedDetails = if (selection.detail in selectedDetails) {
                            selectedRegions.remove(selection.detail)
                            selectedDetails - selection.detail
                        } else {
                            selectedRegions[selection.detail] = selection.runtimeSelection
                            selectedDetails + selection.detail
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                } else {
                    Text(
                        text = "Op grotere schermen staan beide zijden naast elkaar zodat je de anatomie op ware breedte kunt bekijken.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        AnatomyImagePanel(
                            bodyProfile = bodyProfile,
                            bodySide = FitnessBodySide.FRONT,
                            label = "Voorkant",
                            selectedDetails = selectedDetails,
                            selectedRegions = selectedRegions,
                            mappedRegions = mappedRegions,
                            onToggleDetail = { selection ->
                                selectedDetails = if (selection.detail in selectedDetails) {
                                    selectedRegions.remove(selection.detail)
                                    selectedDetails - selection.detail
                                } else {
                                    selectedRegions[selection.detail] = selection.runtimeSelection
                                    selectedDetails + selection.detail
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        AnatomyImagePanel(
                            bodyProfile = bodyProfile,
                            bodySide = FitnessBodySide.BACK,
                            label = "Achterkant",
                            selectedDetails = selectedDetails,
                            selectedRegions = selectedRegions,
                            mappedRegions = mappedRegions,
                            onToggleDetail = { selection ->
                                selectedDetails = if (selection.detail in selectedDetails) {
                                    selectedRegions.remove(selection.detail)
                                    selectedDetails - selection.detail
                                } else {
                                    selectedRegions[selection.detail] = selection.runtimeSelection
                                    selectedDetails + selection.detail
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                

                Text(
                    text = "Selectie",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedDetails.isEmpty()) {
                    Text(
                        text = "Tik direct op een gekleurde spierzone in het beeld om je focus voor deze zijde te kiezen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        selectedDetails.forEach { detail ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = palette.accentSoft,
                                border = BorderStroke(1.dp, palette.accent)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = detail.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = detail.focusCue,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HomePanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Volgende stap",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "De beeldselectie werkt nu per zijde. Hierna koppelen we de gekozen spierzones aan oefeningen en maken we er het dagprogramma van.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnatomyImagePanel(
    bodyProfile: FitnessBodyProfile,
    bodySide: FitnessBodySide,
    label: String,
    selectedDetails: Set<FitnessMuscleDetail>,
    selectedRegions: Map<FitnessMuscleDetail, RuntimeMuscleSelection>,
    mappedRegions: List<AnatomySelectionRegion>,
    onToggleDetail: (ResolvedMuscleSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    val illustration = anatomyIllustration(bodyProfile, bodySide)
    val anatomyImage = imageResource(illustration)
    val anatomyPixelMap = anatomyImage.toPixelMap()
    val anatomyAspectRatio = anatomyImage.width.toFloat() / anatomyImage.height.toFloat()
    val anatomyBackdrop = if (palette.surface.approximateLuminance() < 0.45f) {
        Color(0xFFF6F2EC)
    } else {
        Color(0xFFFFFBF7)
    }
    val pixelSource = remember(anatomyPixelMap) {
        object : AnatomyPixelSource {
            override val width: Int = anatomyPixelMap.width
            override val height: Int = anatomyPixelMap.height

            override fun pixelAt(x: Int, y: Int): AnatomyPixel {
                val color = anatomyPixelMap[x, y]
                return AnatomyPixel(
                    color = RgbColor(
                        red = (color.red * 255f).toInt(),
                        green = (color.green * 255f).toInt(),
                        blue = (color.blue * 255f).toInt()
                    ),
                    alpha = color.alpha
                )
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val widthPx = constraints.maxWidth.toFloat()
            val imageHeightPx = widthPx / anatomyAspectRatio

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(anatomyAspectRatio)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        color = anatomyBackdrop,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .pointerInput(bodyProfile, bodySide, anatomyImage) {
                        detectTapGestures { tapOffset ->
                            val resolved = resolveTappedMuscleDetail(
                                tapOffset = tapOffset,
                                layoutWidthPx = widthPx,
                                layoutHeightPx = imageHeightPx,
                                pixelSource = pixelSource,
                                mappedRegions = mappedRegions,
                                bodyProfile = bodyProfile,
                                bodySide = bodySide
                            )
                            if (resolved != null) {
                                onToggleDetail(resolved)
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(illustration),
                    contentDescription = if (bodyProfile == FitnessBodyProfile.MALE) {
                        "Mannelijke anatomie illustratie $label"
                    } else {
                        "Vrouwelijke anatomie illustratie $label"
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                val sideRegions = selectedRegions
                    .filterKeys { it.region.name == bodySide.name }
                    .values
                if (sideRegions.isNotEmpty()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val scaleX = size.width / pixelSource.width.toFloat()
                        val scaleY = size.height / pixelSource.height.toFloat()
                        sideRegions.forEach { region ->
                            region.polygon?.let { polygon ->
                                drawPolygon(
                                    points = polygon.points,
                                    fillColor = palette.warm.copy(alpha = 0.18f),
                                    strokeColor = palette.warm
                                )
                            }
                            region.mask?.runs?.forEach { run ->
                                drawRect(
                                    color = palette.warm.copy(alpha = 0.18f),
                                    topLeft = Offset(
                                        x = run.startX * scaleX,
                                        y = run.y * scaleY
                                    ),
                                    size = androidx.compose.ui.geometry.Size(
                                        width = ((run.endXInclusive - run.startX) + 1) * scaleX,
                                        height = scaleY
                                    )
                                )
                            }
                            region.mask?.runs?.forEach { run ->
                                val runs = region.mask.runs
                                val rowAbove = runs
                                    .firstOrNull { it.y == run.y - 1 && overlaps(it, run) }
                                val rowBelow = runs
                                    .firstOrNull { it.y == run.y + 1 && overlaps(it, run) }
                                if (rowAbove == null) {
                                    drawLine(
                                        color = palette.warm,
                                        start = Offset(run.startX * scaleX, run.y * scaleY),
                                        end = Offset((run.endXInclusive + 1) * scaleX, run.y * scaleY),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                                if (rowBelow == null) {
                                    drawLine(
                                        color = palette.warm,
                                        start = Offset(run.startX * scaleX, (run.y + 1) * scaleY),
                                        end = Offset((run.endXInclusive + 1) * scaleX, (run.y + 1) * scaleY),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                                val leftConnected = runs.any {
                                    it.y == run.y && it.endXInclusive == run.startX - 1
                                }
                                val rightConnected = runs.any {
                                    it.y == run.y && it.startX == run.endXInclusive + 1
                                }
                                if (!leftConnected) {
                                    drawLine(
                                        color = palette.warm,
                                        start = Offset(run.startX * scaleX, run.y * scaleY),
                                        end = Offset(run.startX * scaleX, (run.y + 1) * scaleY),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                                if (!rightConnected) {
                                    drawLine(
                                        color = palette.warm,
                                        start = Offset((run.endXInclusive + 1) * scaleX, run.y * scaleY),
                                        end = Offset((run.endXInclusive + 1) * scaleX, (run.y + 1) * scaleY),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        val sideSelections = selectedDetails.filter { it.region.name == bodySide.name }
        if (sideSelections.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sideSelections.forEach { detail ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.warmSoft,
                        border = BorderStroke(1.dp, palette.warm)
                    ) {
                        Text(
                            text = detail.label,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.textPrimary
                        )
                    }
                }
            }
        }
    }
}

internal fun anatomyIllustration(
    bodyProfile: FitnessBodyProfile,
    bodySide: FitnessBodySide
) = when (bodyProfile) {
    FitnessBodyProfile.MALE -> if (bodySide == FitnessBodySide.FRONT) {
        Res.drawable.anatomy_male_front
    } else {
        Res.drawable.anatomy_male_back
    }

    FitnessBodyProfile.FEMALE -> if (bodySide == FitnessBodySide.FRONT) {
        Res.drawable.anatomy_female_front
    } else {
        Res.drawable.anatomy_female_back
    }
}

private fun resolveTappedMuscleDetail(
    tapOffset: Offset,
    layoutWidthPx: Float,
    layoutHeightPx: Float,
    pixelSource: AnatomyPixelSource,
    mappedRegions: List<AnatomySelectionRegion>,
    bodyProfile: FitnessBodyProfile,
    bodySide: FitnessBodySide
): ResolvedMuscleSelection? {
    if (layoutWidthPx <= 0f || layoutHeightPx <= 0f) return null
    val normalizedX = (tapOffset.x / layoutWidthPx).coerceIn(0f, 0.999f)
    val normalizedY = (tapOffset.y / layoutHeightPx).coerceIn(0f, 0.999f)
    mappedRegions.findRegion(
        bodyProfile = bodyProfile,
        bodySide = bodySide,
        normalizedX = normalizedX,
        normalizedY = normalizedY
    )?.let { mapped ->
        return ResolvedMuscleSelection(
            detail = mapped.detail,
            runtimeSelection = RuntimeMuscleSelection(
                detail = mapped.detail,
                polygon = PolygonSelectionMask(mapped.points)
            )
        )
    }
    return resolveMuscleSelectionRegion(
        pixelSource = pixelSource,
        normalizedX = normalizedX,
        normalizedY = normalizedY,
        bodyProfile = bodyProfile,
        side = bodySide
    )
}

private fun Color.looksLikeMuscleTint(): Boolean {
    val maxChannel = maxOf(red, green, blue)
    val minChannel = minOf(red, green, blue)
    if (maxChannel <= 0f) return false
    val saturation = (maxChannel - minChannel) / maxChannel
    val luminance = (red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)
    return saturation >= 0.18f && luminance in 0.05f..0.92f
}

internal fun Color.approximateLuminance(): Float =
    (red * 0.2126f) + (green * 0.7152f) + (blue * 0.0722f)

private fun overlaps(left: MuscleRegionRun, right: MuscleRegionRun): Boolean =
    left.startX <= right.endXInclusive && right.startX <= left.endXInclusive

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPolygon(
    points: List<AnatomySelectionPoint>,
    fillColor: Color,
    strokeColor: Color
) {
    if (points.isEmpty()) return
    val path = androidx.compose.ui.graphics.Path()
    val first = points.first()
    path.moveTo(first.x * size.width, first.y * size.height)
    points.drop(1).forEach { point ->
        path.lineTo(point.x * size.width, point.y * size.height)
    }
    if (points.size >= 3) {
        path.close()
        drawPath(path = path, color = fillColor)
    }
    drawPath(path = path, color = strokeColor, style = Stroke(width = 2.dp.toPx()))
}
