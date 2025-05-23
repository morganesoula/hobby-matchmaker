package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import nativeIosPlayerShared.YoutubePlayerContainer
import platform.UIKit.UIScreen

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@Composable
actual fun YoutubeComponent(modifier: Modifier, videoId: String, onClose: () -> Unit) {
    val container = remember { YoutubePlayerContainer() }
    val viewController = remember { container.makeUIViewControllerWithVideoId(videoId = videoId) }

    UIKitView(
        modifier = modifier.fillMaxWidth().height(300.dp),
        factory = { viewController.view },
        update = {}
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GetScreenHeight(): Float {
    return UIScreen.mainScreen.bounds.useContents { size.height.toFloat() }
}
