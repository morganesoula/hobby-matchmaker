package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import nativeIosPlayerShared.YoutubePlayerContainer

@Composable
actual fun YoutubePlayer(
    modifier: Modifier,
    videoId: String,
    onClose: () -> Unit
) {
    val container = remember { YoutubePlayerContainer() }
    val viewController = remember { container.makeUIViewControllerWithVideoId(videoId = videoId) }

    UIKitView(
        modifier = modifier.fillMaxWidth().height(300.dp),
        factory = { viewController.view },
        update = {}
    )
}
