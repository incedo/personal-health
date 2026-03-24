package com.incedo.personalhealth.core.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaVideoRepositoryTest {
    @Test
    fun stubRepository_exposesYoutubeAndNativeVideos() {
        val videos = StubMediaVideoRepository.newsAndSupportVideos()

        assertEquals(3, videos.size)
        assertTrue(videos.any { it.content is MediaVideoContent.YouTube })
        assertTrue(videos.any { it.content is MediaVideoContent.Native })
        assertEquals("mobility-hips-lower-back", videos.first().id)
    }
}
