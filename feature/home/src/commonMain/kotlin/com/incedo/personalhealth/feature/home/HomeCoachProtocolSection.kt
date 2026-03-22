package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.goals.CoachProtocol
import com.incedo.personalhealth.core.goals.CoachProtocolId
import com.incedo.personalhealth.core.goals.CoachSupportTab
import com.incedo.personalhealth.core.goals.coachProtocols

@Composable
internal fun CoachProtocolCard(
    selectedProtocol: CoachProtocol,
    supportTabs: List<CoachSupportTab>,
    onProtocolSelected: (CoachProtocolId) -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenLogbook: () -> Unit,
    onOpenProfile: () -> Unit,
    selectionEnabled: Boolean = true
) {
    val palette = homePalette()
    HomePanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Jouw protocol",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Kies een leefprotocol op basis van het type ritme dat je wilt volgen. De namen zijn neutraal, maar de inhoud is opgebouwd uit herkenbare protocolelementen zoals licht, voedingstiming, meten, training en herstel.",
            style = MaterialTheme.typography.bodyLarge,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (selectionEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                coachProtocols.forEach { protocol ->
                    FilterChip(
                        selected = protocol.id == selectedProtocol.id,
                        onClick = { onProtocolSelected(protocol.id) },
                        label = { Text(protocol.title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = palette.surface,
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, palette.surfaceMuted)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = selectedProtocol.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = selectedProtocol.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
                CoachProtocolStripe(
                    title = "Ritme",
                    value = selectedProtocol.rhythm
                )
                selectedProtocol.anchors.forEach { anchor ->
                    CoachProtocolStripe(
                        title = "Anker",
                        value = anchor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Onderbouwing",
            style = MaterialTheme.typography.titleMedium,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            supportTabs.forEach { tab ->
                CoachProtocolLinkButton(
                    label = supportTabLabel(tab),
                    onClick = when (tab) {
                        CoachSupportTab.DASHBOARD -> onOpenDashboard
                        CoachSupportTab.LOGBOOK -> onOpenLogbook
                        CoachSupportTab.PROFILE -> onOpenProfile
                    }
                )
            }
        }
    }
}

private fun supportTabLabel(tab: CoachSupportTab): String = when (tab) {
    CoachSupportTab.DASHBOARD -> "Open Home voor trends en signalen"
    CoachSupportTab.LOGBOOK -> "Open Log voor gedrag en uitvoering"
    CoachSupportTab.PROFILE -> "Open Profiel voor voorkeuren en basis"
}

@Composable
internal fun CoachProtocolStripe(
    title: String,
    value: String
) {
    val palette = homePalette()
    Surface(
        color = palette.warningSoft,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textPrimary
            )
        }
    }
}

@Composable
private fun CoachProtocolLinkButton(
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(label)
    }
}
