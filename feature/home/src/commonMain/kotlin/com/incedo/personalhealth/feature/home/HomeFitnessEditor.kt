package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FitnessAnatomyEditorScreen(
    bodyProfile: FitnessBodyProfile,
    onBack: () -> Unit,
    compact: Boolean
) {
    val palette = homePalette()
    val scope = rememberCoroutineScope()
    var bodySide by rememberSaveable { mutableStateOf(FitnessBodySide.FRONT) }
    var selectedDetail by rememberSaveable(bodyProfile, bodySide) {
        mutableStateOf(FitnessMuscleDetail.entries.first { it.region.name == bodySide.name })
    }
    var allRegions by remember { mutableStateOf<List<AnatomySelectionRegion>>(emptyList()) }
    var draftPoints by remember(bodyProfile, bodySide) { mutableStateOf(emptyList<AnatomySelectionPoint>()) }
    var editingRegionId by remember(bodyProfile, bodySide) { mutableStateOf<String?>(null) }
    var importPayload by remember { mutableStateOf("") }
    var helperText by rememberSaveable {
        mutableStateOf("Tik op de afbeelding om punten toe te voegen. Tik op een bestaand vlak om het direct te editen.")
    }

    LaunchedEffect(Unit) {
        val loaded = loadEffectiveAnatomySelectionMap()
        allRegions = loaded
        importPayload = encodeAnatomySelectionMap(loaded)
    }

    fun persist(regions: List<AnatomySelectionRegion>, helper: String) {
        val payload = encodeAnatomySelectionMap(regions)
        allRegions = regions
        importPayload = payload
        helperText = helper
        scope.launch { AnatomySelectionEditorStore.saveOverride(payload) }
    }

    fun resetDraft(nextDetail: FitnessMuscleDetail = selectedDetail) {
        draftPoints = emptyList()
        editingRegionId = null
        selectedDetail = nextDetail
    }

    fun loadRegionIntoEditor(region: AnatomySelectionRegion) {
        bodySide = region.bodySide
        selectedDetail = region.detail
        editingRegionId = region.id
        draftPoints = region.points
        helperText = "${region.detail.label} geladen. Tik op een bestaand punt om het te verwijderen of voeg nieuwe punten toe."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(if (compact) 14.dp else 18.dp)
    ) {
        HomeHeroCard(
            eyebrow = "Debug editor",
            title = "Anatomie selectie map",
            subtitle = "Los debug-scherm om polygonen per spierdetail te tekenen. De runtime leest daarna dezelfde genormaliseerde map in.",
            accent = palette.accent,
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
            Text("Terug")
        }

        HomePanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Editor",
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FitnessBodySide.entries.forEach { side ->
                    val selected = bodySide == side
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) palette.accentSoft else palette.surface,
                        onClick = {
                            bodySide = side
                            resetDraft(FitnessMuscleDetail.entries.first { it.region.name == side.name })
                        }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (side == FitnessBodySide.FRONT) "Voorkant" else "Achterkant")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            AnatomyEditorCanvas(
                bodyProfile = bodyProfile,
                bodySide = bodySide,
                draftPoints = draftPoints,
                selectedDetail = selectedDetail,
                editingRegionId = editingRegionId,
                regions = allRegions.filter { it.bodyProfile == bodyProfile && it.bodySide == bodySide },
                onCanvasInteraction = { action ->
                    when (action) {
                        is EditorCanvasAction.AddPoint -> {
                            draftPoints = draftPoints + action.point
                            helperText = "${draftPoints.size + 1} punten voor ${selectedDetail.label}."
                        }

                        is EditorCanvasAction.RemoveDraftPoint -> {
                            draftPoints = draftPoints.filterIndexed { index, _ -> index != action.index }
                            helperText = "Punt verwijderd uit ${selectedDetail.label}."
                        }

                        is EditorCanvasAction.SelectExistingRegion -> {
                            loadRegionIntoEditor(action.region)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Spierdetail",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FitnessMuscleDetail.entries
                    .filter { it.region.name == bodySide.name }
                    .forEach { detail ->
                        val selected = selectedDetail == detail
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) palette.warmSoft else palette.surface,
                            modifier = Modifier.clickable {
                                selectedDetail = detail
                                editingRegionId = allRegions
                                    .firstOrNull {
                                        it.bodyProfile == bodyProfile &&
                                            it.bodySide == bodySide &&
                                            it.detail == detail
                                    }
                                    ?.id
                                draftPoints = if (editingRegionId == null) emptyList() else {
                                    allRegions.first { it.id == editingRegionId }.points
                                }
                                helperText = if (editingRegionId == null) {
                                    "${detail.label} geselecteerd. Start met punten zetten."
                                } else {
                                    "${detail.label} heeft al een regio. Je bewerkt die nu."
                                }
                            }
                        ) {
                            Text(
                                text = detail.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                color = palette.textPrimary
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (draftPoints.isNotEmpty()) {
                Text(
                    text = "Punten",
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    draftPoints.forEachIndexed { index, point ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surfaceRaised,
                            modifier = Modifier.clickable {
                                draftPoints = draftPoints.filterIndexed { pointIndex, _ -> pointIndex != index }
                                helperText = "Punt ${index + 1} verwijderd."
                            }
                        ) {
                            Text(
                                text = "${index + 1}: ${(point.x * 100).toInt()}, ${(point.y * 100).toInt()}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                color = palette.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { if (draftPoints.isNotEmpty()) draftPoints = draftPoints.dropLast(1) },
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Undo punt") }
                Button(
                    onClick = {
                        resetDraft()
                        helperText = "Teken een nieuwe regio voor ${selectedDetail.label}."
                    },
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Nieuwe selectie") }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (draftPoints.size < 3) return@Button
                    val regionId = editingRegionId ?: "${bodyProfile.name}-${bodySide.name}-${selectedDetail.name}-${allRegions.count { it.detail == selectedDetail }}"
                    val nextRegion = AnatomySelectionRegion(
                        id = regionId,
                        detail = selectedDetail,
                        bodyProfile = bodyProfile,
                        bodySide = bodySide,
                        points = draftPoints
                    )
                    val updated = allRegions.filterNot { it.id == regionId } + nextRegion
                    persist(updated, "${selectedDetail.label} opgeslagen. Klaar voor de volgende spier.")
                    resetDraft(nextSuggestedDetail(bodySide, selectedDetail, updated))
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (editingRegionId == null) "Regio opslaan en volgende" else "Regio bijwerken en volgende")
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Bestaande regio's",
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            val visibleRegions = allRegions.filter { it.bodyProfile == bodyProfile && it.bodySide == bodySide }
            if (visibleRegions.isEmpty()) {
                Text("Nog geen regio's opgeslagen voor deze zijde.", color = palette.textSecondary)
            } else {
                visibleRegions.forEach { region ->
                    val selected = editingRegionId == region.id
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) palette.accentSoft else palette.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(region.detail.label, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                                Text("${region.points.size} punten", color = palette.textSecondary)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { loadRegionIntoEditor(region) },
                                    shape = RoundedCornerShape(14.dp)
                                ) { Text("Edit") }
                                Button(
                                    onClick = {
                                        val updated = allRegions.filterNot { it.id == region.id }
                                        persist(updated, "${region.detail.label} verwijderd.")
                                        if (editingRegionId == region.id) {
                                            resetDraft(nextSuggestedDetail(bodySide, region.detail, updated))
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                ) { Text("Delete") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        allRegions = emptyList()
                        resetDraft()
                        importPayload = encodeAnatomySelectionMap(emptyList())
                        helperText = "Debug override geleegd."
                        scope.launch { AnatomySelectionEditorStore.clearOverride() }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Reset debug override")
                }
                Button(
                    onClick = {
                        val loaded = runCatching { decodeAnatomySelectionMap(importPayload) }.getOrNull()
                        if (loaded != null) {
                            persist(loaded, "JSON import geladen in debug override.")
                            resetDraft(nextSuggestedDetail(bodySide, selectedDetail, loaded))
                        } else {
                            helperText = "JSON import kon niet worden gelezen."
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Importeer JSON")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = importPayload,
                onValueChange = { importPayload = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 12,
                label = { Text("Debug JSON / resource payload") }
            )
        }
    }
}

@Composable
private fun AnatomyEditorCanvas(
    bodyProfile: FitnessBodyProfile,
    bodySide: FitnessBodySide,
    draftPoints: List<AnatomySelectionPoint>,
    selectedDetail: FitnessMuscleDetail,
    editingRegionId: String?,
    regions: List<AnatomySelectionRegion>,
    onCanvasInteraction: (EditorCanvasAction) -> Unit
) {
    val palette = homePalette()
    val illustration = anatomyIllustration(bodyProfile, bodySide)
    val anatomyImage = imageResource(illustration)
    val anatomyAspectRatio = anatomyImage.width.toFloat() / anatomyImage.height.toFloat()
    val anatomyBackdrop = if (palette.surface.approximateLuminance() < 0.45f) Color(0xFFF6F2EC) else Color(0xFFFFFBF7)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(anatomyAspectRatio)
                .clip(RoundedCornerShape(28.dp))
                .background(anatomyBackdrop)
                .pointerInput(bodyProfile, bodySide, draftPoints, regions, editingRegionId) {
                    detectTapGestures { tap ->
                        val x = (tap.x / size.width).coerceIn(0f, 1f)
                        val y = (tap.y / size.height).coerceIn(0f, 1f)
                        val tapPoint = AnatomySelectionPoint(x, y)

                        nearestDraftPointIndex(draftPoints, tapPoint)?.let { pointIndex ->
                            onCanvasInteraction(EditorCanvasAction.RemoveDraftPoint(pointIndex))
                            return@detectTapGestures
                        }

                        regions.findRegion(
                            bodyProfile = bodyProfile,
                            bodySide = bodySide,
                            normalizedX = x,
                            normalizedY = y
                        )?.let { region ->
                            if (region.id != editingRegionId || draftPoints.isEmpty()) {
                                onCanvasInteraction(EditorCanvasAction.SelectExistingRegion(region))
                                return@detectTapGestures
                            }
                        }

                        onCanvasInteraction(EditorCanvasAction.AddPoint(tapPoint))
                    }
                }
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(illustration),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                regions.forEach { region ->
                    val isEditing = region.id == editingRegionId
                    val isSameDetail = region.detail == selectedDetail
                    drawPolygon(
                        region.points,
                        fillColor = when {
                            isEditing -> palette.warm.copy(alpha = 0.18f)
                            isSameDetail -> palette.accent.copy(alpha = 0.16f)
                            else -> palette.accent.copy(alpha = 0.08f)
                        },
                        strokeColor = when {
                            isEditing -> palette.warm
                            isSameDetail -> palette.accent
                            else -> palette.surfaceMuted
                        },
                        strokeWidthDp = if (isEditing) 3.dp else 2.dp
                    )
                }
                if (draftPoints.isNotEmpty()) {
                    drawPolygon(
                        draftPoints,
                        fillColor = palette.warm.copy(alpha = 0.24f),
                        strokeColor = palette.warm,
                        strokeWidthDp = 3.dp
                    )
                    draftPoints.forEachIndexed { index, point ->
                        val center = Offset(point.x * size.width, point.y * size.height)
                        drawCircle(
                            color = palette.warm,
                            radius = 6.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = anatomyBackdrop,
                            radius = 2.5.dp.toPx(),
                            center = center
                        )
                    }
                }
            }
        }
    }
}

private sealed interface EditorCanvasAction {
    data class AddPoint(val point: AnatomySelectionPoint) : EditorCanvasAction
    data class RemoveDraftPoint(val index: Int) : EditorCanvasAction
    data class SelectExistingRegion(val region: AnatomySelectionRegion) : EditorCanvasAction
}

private fun nearestDraftPointIndex(
    points: List<AnatomySelectionPoint>,
    tapPoint: AnatomySelectionPoint,
    maxDistance: Float = 0.025f
): Int? {
    val indexed = points.mapIndexed { index, point ->
        index to sqrt(((point.x - tapPoint.x) * (point.x - tapPoint.x)) + ((point.y - tapPoint.y) * (point.y - tapPoint.y)))
    }
    val nearest = indexed.minByOrNull { it.second } ?: return null
    return nearest.first.takeIf { nearest.second <= maxDistance }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPolygon(
    points: List<AnatomySelectionPoint>,
    fillColor: Color,
    strokeColor: Color,
    strokeWidthDp: androidx.compose.ui.unit.Dp
) {
    if (points.isEmpty()) return
    val path = Path()
    val first = points.first()
    path.moveTo(first.x * size.width, first.y * size.height)
    points.drop(1).forEach { point ->
        path.lineTo(point.x * size.width, point.y * size.height)
    }
    if (points.size >= 3) {
        path.close()
        drawPath(path = path, color = fillColor, style = Fill)
    }
    drawPath(path = path, color = strokeColor, style = Stroke(width = strokeWidthDp.toPx()))
}
