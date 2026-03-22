package com.incedo.personalhealth.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.wellbeing.ScreenTimePermissionState
import com.incedo.personalhealth.core.wellbeing.ScreenTimeSummary
import com.incedo.personalhealth.core.wellbeing.SocialAppDefinition

@Composable
internal fun ScreenTimeSettingsCard(
    summary: ScreenTimeSummary,
    availableApps: List<SocialAppDefinition>,
    selectedPackages: Set<String>,
    onTogglePackage: (String, Boolean) -> Unit,
    onRequestAccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Schermtijd", style = MaterialTheme.typography.titleSmall)
            Text(
                when (summary.permissionState) {
                    ScreenTimePermissionState.GRANTED -> "Totaal ${summary.totalScreenMinutes} min, social ${summary.socialScreenMinutes} min vandaag."
                    ScreenTimePermissionState.REQUIRES_ACCESS -> "Usage Access is nog niet actief."
                    ScreenTimePermissionState.UNAVAILABLE -> "Schermtijd is niet beschikbaar op dit platform."
                },
                style = MaterialTheme.typography.bodySmall
            )
            if (summary.permissionState != ScreenTimePermissionState.GRANTED) {
                Button(onClick = onRequestAccess) {
                    Text("Geef schermtijdtoegang")
                }
            }
            Text(
                "Social apps",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            availableApps.forEach { app ->
                val usage = summary.selectedSocialApps.firstOrNull { it.packageName == app.packageName }?.durationMinutes ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = app.packageName in selectedPackages,
                            onCheckedChange = { checked -> onTogglePackage(app.packageName, checked) }
                        )
                        Text(app.displayName, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("$usage min", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
