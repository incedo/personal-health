package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.coaches.CoachSearchItem
import com.incedo.personalhealth.core.coaches.coachTypeLabel

@Composable
internal fun CoachSearchSection(
    query: String,
    selectedItemId: String?,
    results: List<CoachSearchItem>,
    onQueryChanged: (String) -> Unit,
    onSelectItem: (CoachSearchItem) -> Unit
) {
    val palette = homePalette()
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Zoek coach, bedrijf of type") },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = coachEditorFieldColors()
    )
    Spacer(modifier = Modifier.height(14.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        results.take(6).forEach { item ->
            val selected = item.id == selectedItemId
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onSelectItem(item) },
                color = if (selected) palette.warningSoft else palette.surfaceRaised,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) palette.warning else palette.surfaceMuted
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = coachTypeLabel(item.type),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) palette.warning else palette.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.companyName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary
                        )
                        Text(
                            text = item.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}
