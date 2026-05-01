package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.media.MediaVideoContent
import com.incedo.personalhealth.core.newssocial.NewsSocialVideoLink
import com.incedo.personalhealth.core.newssocial.NewsSocialVideoPost

@Composable
internal fun NewsTrainingVideoStrip(
    videos: List<NewsSocialVideoPost>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        videos.forEachIndexed { index, video ->
            NewsVideoCard(video = video, index = index)
        }
    }
}

@Composable
private fun NewsVideoCard(
    video: NewsSocialVideoPost,
    index: Int
) {
    val palette = homePalette()
    val openVideo = rememberPlatformVideoLauncher()
    val content = video.video.toMediaVideoContent()
    val isNativeVideo = content is MediaVideoContent.Native
    var expanded by remember(video.title) { mutableStateOf(false) }
    val sourceBadge = when (video.video) {
        is NewsSocialVideoLink.Hosted -> "APP"
        is NewsSocialVideoLink.YouTube -> "YT"
    }
    val brush = when (index % 3) {
        0 -> Brush.linearGradient(listOf(palette.warning, palette.warm))
        1 -> Brush.linearGradient(listOf(palette.accent, palette.warning))
        else -> Brush.linearGradient(listOf(palette.warm, palette.accent))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                if (isNativeVideo) {
                    expanded = !expanded
                } else {
                    openVideo(content.launchUrl)
                }
            },
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .width(152.dp)
                        .size(132.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(brush),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.78f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶",
                            style = MaterialTheme.typography.headlineMedium,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.54f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = sourceBadge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = video.duration,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${video.category.uppercase()} · ${video.sourceLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = video.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "${video.author.name} · ${video.author.role}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "${video.cue} · ${video.engagementLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "Beeld: ${video.imageUrl.imageLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary
                    )
                    Button(
                        onClick = {
                            if (isNativeVideo) {
                                expanded = !expanded
                            } else {
                                openVideo(content.launchUrl)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.warning,
                            contentColor = palette.buttonContent
                        )
                    ) {
                        Text(
                            when {
                                isNativeVideo && expanded -> "Sluit video"
                                isNativeVideo -> "Speel in app"
                                else -> "Open in YouTube"
                            }
                        )
                    }
                }
            }
            if (isNativeVideo && expanded) {
                PlatformEmbeddedVideoPlayer(
                    content = content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            } else if (isNativeVideo) {
                Button(
                    onClick = { openVideo(content.launchUrl) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.surface,
                        contentColor = palette.textPrimary
                    )
                ) {
                    Text("Open extern")
                }
            }
        }
    }
}

private fun NewsSocialVideoLink.toMediaVideoContent(): MediaVideoContent = when (this) {
    is NewsSocialVideoLink.Hosted -> MediaVideoContent.Native(uri = uri, previewImageUrl = previewImageUrl)
    is NewsSocialVideoLink.YouTube -> MediaVideoContent.YouTube(
        videoId = videoId,
        launchUrl = launchUrl,
        previewImageUrl = previewImageUrl
    )
}

private fun String.imageLabel(): String = substringAfter("://").substringBefore("/").ifBlank { this }
