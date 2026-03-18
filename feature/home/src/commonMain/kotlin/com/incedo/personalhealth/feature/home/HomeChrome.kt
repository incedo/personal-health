package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class HomePalette(
    val backdropTop: Color,
    val backdropBottom: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val warm: Color,
    val warmSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val buttonContent: Color
)

private val HomeDarkPalette = HomePalette(
    backdropTop = Color(0xFF071A29),
    backdropBottom = Color(0xFF0B2234),
    surface = Color(0xFF10293D),
    surfaceRaised = Color(0xFF15354B),
    surfaceMuted = Color(0xFF1D425B),
    accent = Color(0xFF19C7B4),
    accentSoft = Color(0xFF124C57),
    warm = Color(0xFFF5C451),
    warmSoft = Color(0xFF4A3A13),
    warning = Color(0xFFFF6F7D),
    warningSoft = Color(0xFF4D2130),
    textPrimary = Color(0xFFF5F7FA),
    textSecondary = Color(0xFFA6B5C4),
    buttonContent = Color(0xFF05262A)
)

private val HomeLightPalette = HomePalette(
    backdropTop = Color(0xFFF5FBFF),
    backdropBottom = Color(0xFFE6F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFF3F8FB),
    surfaceMuted = Color(0xFFD4E2EA),
    accent = Color(0xFF0FA897),
    accentSoft = Color(0xFFD9F5F1),
    warm = Color(0xFFE0A800),
    warmSoft = Color(0xFFF8EDC8),
    warning = Color(0xFFE26376),
    warningSoft = Color(0xFFFADCE1),
    textPrimary = Color(0xFF10293D),
    textSecondary = Color(0xFF5F7488),
    buttonContent = Color(0xFFFFFFFF)
)

@Composable
internal fun homePalette(): HomePalette = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
    HomeDarkPalette
} else {
    HomeLightPalette
}

@Composable
private fun tabAccent(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accent
    HomeTab.SYNC -> homePalette().warm
    HomeTab.PROFILE -> homePalette().warning
}

@Composable
private fun tabAccentSoft(tab: HomeTab): Color = when (tab) {
    HomeTab.DASHBOARD -> homePalette().accentSoft
    HomeTab.SYNC -> homePalette().warmSoft
    HomeTab.PROFILE -> homePalette().warningSoft
}

@Composable
internal fun HomeSectionScreen(
    tab: HomeTab,
    compact: Boolean,
    leadingContent: @Composable ColumnScope.() -> Unit,
    bodyContent: @Composable ColumnScope.() -> Unit
) {
    val palette = homePalette()
    val spacing = if (compact) 14.dp else 18.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        leadingContent()
        HomePanel(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = tab.sectionTitle,
                style = MaterialTheme.typography.titleLarge,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tab.sectionSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))
            bodyContent()
        }
    }
}

@Composable
internal fun HomeSidebar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = homePalette()
    HomePanel(
        modifier = modifier.fillMaxSize(),
        contentPadding = 20.dp
    ) {
        Text(
            text = "Personal Health",
            style = MaterialTheme.typography.titleLarge,
            color = palette.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Eenzelfde navigatiemodel voor home, sync en profiel.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.textSecondary
        )
        Spacer(modifier = Modifier.height(22.dp))
        HomeTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            val background = if (selected) tabAccentSoft(tab) else Color.Transparent
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onTabSelected(tab) },
                color = background,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) tabAccent(tab) else palette.surfaceMuted
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tab.navDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
internal fun HomeBottomTabs(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val palette = homePalette()
    HomePanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                val background = if (selected) tabAccentSoft(tab) else palette.surface
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { onTabSelected(tab) },
                    color = background,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) tabAccent(tab) else palette.surfaceMuted
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun HomeHeroCard(
    eyebrow: String,
    title: String,
    subtitle: String,
    accent: Color,
    compact: Boolean,
    sideContent: @Composable () -> Unit
) {
    val palette = homePalette()
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        val heroPadding = if (compact) 20.dp else 28.dp
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(listOf(accent.copy(alpha = 0.18f), palette.surface)))
                    .padding(heroPadding),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HomeHeroCopy(
                    eyebrow = eyebrow,
                    title = title,
                    subtitle = subtitle
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    sideContent()
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accent.copy(alpha = 0.2f), palette.surface)
                        )
                    )
                    .padding(heroPadding),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeHeroCopy(
                        eyebrow = eyebrow,
                        title = title,
                        subtitle = subtitle
                    )
                }
                sideContent()
            }
        }
    }
}

@Composable
private fun HomeHeroCopy(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    val palette = homePalette()
    Text(
        text = eyebrow.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = palette.textSecondary
    )
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = palette.textPrimary,
        fontWeight = FontWeight.SemiBold
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = palette.textSecondary
    )
}

@Composable
internal fun HomeStatusBadge(
    label: String,
    value: String
) {
    val palette = homePalette()
    Surface(
        color = palette.accentSoft,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.accent)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun HomePanel(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = homePalette()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}

internal enum class HomeTab(
    val label: String,
    val navDescription: String,
    val sectionTitle: String,
    val sectionSubtitle: String
) {
    DASHBOARD(
        label = "Home",
        navDescription = "Dagelijkse status en snelle acties",
        sectionTitle = "Vandaag",
        sectionSubtitle = "Je centrale startpunt voor gezondheid en activiteit."
    ),
    SYNC(
        label = "Sync",
        navDescription = "Brondatastatus en imports",
        sectionTitle = "Sync",
        sectionSubtitle = "Bronnen, kanaalstatus en importacties op een vaste plek."
    ),
    PROFILE(
        label = "Profiel",
        navDescription = "Persoonlijke gegevens en voorkeuren",
        sectionTitle = "Profiel",
        sectionSubtitle = "Instellingen, status en accountinformatie."
    )
}
