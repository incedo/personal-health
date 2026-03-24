package com.incedo.personalhealth.core.media

data class MediaVideoItem(
    val id: String,
    val title: String,
    val source: String,
    val duration: String,
    val category: String,
    val cue: String,
    val content: MediaVideoContent
)

sealed interface MediaVideoContent {
    val launchUrl: String
    val previewImageUrl: String?

    data class YouTube(
        val videoId: String,
        override val launchUrl: String,
        override val previewImageUrl: String = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    ) : MediaVideoContent

    data class Native(
        val uri: String,
        override val previewImageUrl: String? = null,
        override val launchUrl: String = uri
    ) : MediaVideoContent
}
