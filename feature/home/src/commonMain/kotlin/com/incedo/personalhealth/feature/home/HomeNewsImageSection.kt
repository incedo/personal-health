package com.incedo.personalhealth.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incedo.personalhealth.core.newssocial.NewsSocialImage
import com.incedo.personalhealth.core.newssocial.NewsSocialImagePost

@Composable
internal fun NewsImageGallerySection(
    posts: List<NewsSocialImagePost>
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        posts.forEach { post ->
            NewsImageGalleryCard(post = post)
        }
    }
}

@Composable
private fun NewsImageGalleryCard(
    post: NewsSocialImagePost
) {
    val palette = homePalette()
    val openLink = rememberPlatformVideoLauncher()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.surfaceRaised,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, palette.surfaceMuted)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${post.sourceLabel} · ${post.engagementLabel}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary
            )
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textPrimary
            )
            Text(
                text = "${post.author.name} · ${post.author.handle} · ${post.author.role}",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val tileWidth = if (maxWidth >= 960.dp) 280.dp else if (maxWidth >= 680.dp) 220.dp else 172.dp
                val tileHeight = if (maxWidth >= 680.dp) 184.dp else 152.dp
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(post.images.take(10), key = { index, image -> "${post.id}-${index}-${image.imageUrl}" }) { index, image ->
                        GalleryImageTile(
                            image = image,
                            index = index,
                            width = tileWidth,
                            height = tileHeight,
                            onOpen = { openLink(image.imageUrl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryImageTile(
    image: NewsSocialImage,
    index: Int,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    onOpen: () -> Unit
) {
    val palette = homePalette()
    val brush = when (index % 3) {
        0 -> Brush.linearGradient(listOf(palette.accent.copy(alpha = 0.95f), palette.warning.copy(alpha = 0.95f)))
        1 -> Brush.linearGradient(listOf(palette.warm.copy(alpha = 0.95f), palette.accent.copy(alpha = 0.95f)))
        else -> Brush.linearGradient(listOf(palette.warning.copy(alpha = 0.95f), palette.warm.copy(alpha = 0.95f)))
    }

    Surface(
        modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp),
        color = palette.surface
    ) {
        Column(
            modifier = Modifier
                .background(brush)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                NewsNetworkImage(
                    imageUrl = image.imageUrl,
                    contentDescription = image.caption,
                    height = height,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    color = Color.Black.copy(alpha = 0.54f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Foto ${index + 1}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(
                text = image.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.buttonContent,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = image.imageUrl.imageLabel(),
                style = MaterialTheme.typography.bodySmall,
                color = palette.buttonContent.copy(alpha = 0.86f)
            )
        }
    }
}

private fun String.imageLabel(): String = substringAfter("://").substringBefore("/").ifBlank { this }
