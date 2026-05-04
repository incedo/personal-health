package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.designsystem.PhSectionHeader
import com.incedo.personalhealth.core.designsystem.PhTag
import com.incedo.personalhealth.core.designsystem.PhTagTone
import com.incedo.personalhealth.core.designsystem.PhTheme

@Composable
internal fun HomeDevTestScreen(
    compact: Boolean,
    onBack: () -> Unit,
    syncContent: @Composable ColumnScope.() -> Unit,
    profileContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(if (compact) 14.dp else 18.dp)
    ) {
        HomeHeroCard(
            eyebrow = "Dev/Test",
            title = "Import, sync en testtools",
            subtitle = "Interne tools voor health imports, recalculatie, platform checks en permission simulatie.",
            accent = homePalette().warning,
            compact = compact,
            sideContent = { HomeStatusBadge(label = "Mode", value = "Dev") }
        )
        OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
            Text("Terug")
        }
        HomePanel(modifier = Modifier.fillMaxWidth()) {
            PhSectionHeader(title = "Health import & sync", kicker = "Debug") {
                PhTag("Niet voor normaal gebruik", tone = PhTagTone.Warning)
            }
            Text(
                text = "Alle importachtige flows staan hier, zodat Profiel zelf schoon blijft.",
                style = PhTheme.typography.bodySmall,
                color = PhTheme.colors.textMuted,
                modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
            )
            syncContent()
        }
        HomePanel(modifier = Modifier.fillMaxWidth()) {
            PhSectionHeader(title = "Platform wellbeing checks", kicker = "Test")
            Text(
                text = "Screen-time permissies en geselecteerde testapps blijven beschikbaar voor ontwikkel- en testwerk.",
                style = PhTheme.typography.bodySmall,
                color = PhTheme.colors.textMuted,
                modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
            )
            profileContent()
        }
    }
}
