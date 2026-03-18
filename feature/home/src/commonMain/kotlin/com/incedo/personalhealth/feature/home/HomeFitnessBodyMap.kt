package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Composable
internal fun MuscleGroupBodySelector(
    selectedMuscleGroups: Set<FitnessMuscleGroup>,
    onToggleMuscleGroup: (FitnessMuscleGroup) -> Unit
) {
    val palette = homePalette()
    val frontGroups = FitnessMuscleGroup.entries.filter { it.region == MuscleGroupRegion.FRONT }
    val backGroups = FitnessMuscleGroup.entries.filter { it.region == MuscleGroupRegion.BACK }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Spiergroepen",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Selecteer de zones die je vandaag hebt getraind. De oefenlijst wordt daarop gefilterd.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MuscleRegionFigure(
                title = "Voorkant",
                groups = frontGroups,
                selectedMuscleGroups = selectedMuscleGroups,
                onToggleMuscleGroup = onToggleMuscleGroup,
                modifier = Modifier.weight(1f)
            )
            MuscleRegionFigure(
                title = "Achterkant",
                groups = backGroups,
                selectedMuscleGroups = selectedMuscleGroups,
                onToggleMuscleGroup = onToggleMuscleGroup,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MuscleRegionFigure(
    title: String,
    groups: List<FitnessMuscleGroup>,
    selectedMuscleGroups: Set<FitnessMuscleGroup>,
    onToggleMuscleGroup: (FitnessMuscleGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    Surface(
        modifier = modifier,
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.62f),
                contentAlignment = Alignment.TopCenter
            ) {
                BodySilhouette(
                    modifier = Modifier.matchParentSize(),
                    region = groups.firstOrNull()?.region ?: MuscleGroupRegion.FRONT,
                    selectedMuscleGroups = selectedMuscleGroups
                )
                groups.forEach { group ->
                    val selected = group in selectedMuscleGroups
                    val position = muscleGroupPlacement(group)
                    MuscleHotspot(
                        label = group.label,
                        selected = selected,
                        xOffset = position.first,
                        yOffset = position.second,
                        onClick = { onToggleMuscleGroup(group) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BodySilhouette(
    modifier: Modifier = Modifier,
    region: MuscleGroupRegion,
    selectedMuscleGroups: Set<FitnessMuscleGroup>
) {
    val palette = homePalette()
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val outline = palette.surfaceMuted
        val highlight = palette.warm
        val baseFill = palette.surface.copy(alpha = 0.7f)

        fun drawZone(
            group: FitnessMuscleGroup,
            topLeft: Offset,
            zoneSize: Size,
            radius: Float
        ) {
            drawRoundRect(
                color = if (group in selectedMuscleGroups) highlight else baseFill,
                topLeft = topLeft,
                size = zoneSize,
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        if (region == MuscleGroupRegion.FRONT) {
            drawZone(FitnessMuscleGroup.SHOULDERS_FRONT, Offset(centerX - width * 0.23f, height * 0.23f), Size(width * 0.46f, height * 0.08f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CHEST, Offset(centerX - width * 0.18f, height * 0.30f), Size(width * 0.36f, height * 0.12f), width * 0.06f)
            drawZone(FitnessMuscleGroup.BICEPS, Offset(centerX - width * 0.30f, height * 0.29f), Size(width * 0.10f, height * 0.18f), width * 0.05f)
            drawZone(FitnessMuscleGroup.BICEPS, Offset(centerX + width * 0.20f, height * 0.29f), Size(width * 0.10f, height * 0.18f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CORE, Offset(centerX - width * 0.12f, height * 0.43f), Size(width * 0.24f, height * 0.16f), width * 0.05f)
            drawZone(FitnessMuscleGroup.QUADS, Offset(centerX - width * 0.18f, height * 0.60f), Size(width * 0.12f, height * 0.20f), width * 0.05f)
            drawZone(FitnessMuscleGroup.QUADS, Offset(centerX + width * 0.06f, height * 0.60f), Size(width * 0.12f, height * 0.20f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CALVES, Offset(centerX - width * 0.17f, height * 0.82f), Size(width * 0.10f, height * 0.13f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CALVES, Offset(centerX + width * 0.07f, height * 0.82f), Size(width * 0.10f, height * 0.13f), width * 0.05f)
        } else {
            drawZone(FitnessMuscleGroup.UPPER_BACK, Offset(centerX - width * 0.19f, height * 0.25f), Size(width * 0.38f, height * 0.14f), width * 0.05f)
            drawZone(FitnessMuscleGroup.LATS, Offset(centerX - width * 0.23f, height * 0.39f), Size(width * 0.46f, height * 0.18f), width * 0.05f)
            drawZone(FitnessMuscleGroup.TRICEPS, Offset(centerX - width * 0.30f, height * 0.32f), Size(width * 0.10f, height * 0.20f), width * 0.05f)
            drawZone(FitnessMuscleGroup.TRICEPS, Offset(centerX + width * 0.20f, height * 0.32f), Size(width * 0.10f, height * 0.20f), width * 0.05f)
            drawZone(FitnessMuscleGroup.GLUTES, Offset(centerX - width * 0.16f, height * 0.58f), Size(width * 0.32f, height * 0.11f), width * 0.06f)
            drawZone(FitnessMuscleGroup.HAMSTRINGS, Offset(centerX - width * 0.18f, height * 0.68f), Size(width * 0.12f, height * 0.18f), width * 0.05f)
            drawZone(FitnessMuscleGroup.HAMSTRINGS, Offset(centerX + width * 0.06f, height * 0.68f), Size(width * 0.12f, height * 0.18f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CALVES_BACK, Offset(centerX - width * 0.17f, height * 0.86f), Size(width * 0.10f, height * 0.10f), width * 0.05f)
            drawZone(FitnessMuscleGroup.CALVES_BACK, Offset(centerX + width * 0.07f, height * 0.86f), Size(width * 0.10f, height * 0.10f), width * 0.05f)
        }

        drawCircle(
            color = outline,
            radius = width * 0.11f,
            center = Offset(centerX, height * 0.12f),
            style = Stroke(width = 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(centerX - width * 0.16f, height * 0.21f),
            size = Size(width * 0.32f, height * 0.28f),
            cornerRadius = CornerRadius(width * 0.12f, width * 0.12f),
            style = Stroke(width = 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(centerX - width * 0.27f, height * 0.25f),
            size = Size(width * 0.11f, height * 0.27f),
            cornerRadius = CornerRadius(width * 0.06f, width * 0.06f),
            style = Stroke(width = 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(centerX + width * 0.16f, height * 0.25f),
            size = Size(width * 0.11f, height * 0.27f),
            cornerRadius = CornerRadius(width * 0.06f, width * 0.06f),
            style = Stroke(width = 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(centerX - width * 0.18f, height * 0.52f),
            size = Size(width * 0.12f, height * 0.31f),
            cornerRadius = CornerRadius(width * 0.06f, width * 0.06f),
            style = Stroke(width = 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(centerX + width * 0.06f, height * 0.52f),
            size = Size(width * 0.12f, height * 0.31f),
            cornerRadius = CornerRadius(width * 0.06f, width * 0.06f),
            style = Stroke(width = 4f)
        )
    }
}

@Composable
private fun MuscleHotspot(
    label: String,
    selected: Boolean,
    xOffset: Dp,
    yOffset: Dp,
    onClick: () -> Unit
) {
    val palette = homePalette()
    Surface(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) palette.warm else palette.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) palette.warm else palette.surfaceMuted
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = if (selected) palette.buttonContent else Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) palette.buttonContent else palette.surfaceMuted)
            ) {}
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) palette.buttonContent else palette.textPrimary
            )
        }
    }
}

private fun muscleGroupPlacement(group: FitnessMuscleGroup): Pair<Dp, Dp> = when (group) {
    FitnessMuscleGroup.CHEST -> 0.dp to 60.dp
    FitnessMuscleGroup.SHOULDERS_FRONT -> 0.dp to 12.dp
    FitnessMuscleGroup.BICEPS -> (-62).dp to 118.dp
    FitnessMuscleGroup.CORE -> 0.dp to 176.dp
    FitnessMuscleGroup.QUADS -> (-52).dp to 264.dp
    FitnessMuscleGroup.CALVES -> (-42).dp to 350.dp
    FitnessMuscleGroup.UPPER_BACK -> 0.dp to 38.dp
    FitnessMuscleGroup.LATS -> 58.dp to 120.dp
    FitnessMuscleGroup.TRICEPS -> 66.dp to 200.dp
    FitnessMuscleGroup.GLUTES -> 0.dp to 246.dp
    FitnessMuscleGroup.HAMSTRINGS -> 56.dp to 308.dp
    FitnessMuscleGroup.CALVES_BACK -> 46.dp to 370.dp
}
