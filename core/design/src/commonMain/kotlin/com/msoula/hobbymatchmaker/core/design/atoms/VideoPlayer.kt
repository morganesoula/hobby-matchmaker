package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun YoutubePlayer(modifier: Modifier = Modifier, videoId: String, onClose: () -> Unit)

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoId: String,
    onPlayerClosing: () -> Unit
) {
    YoutubePlayer(videoId = videoId) {
        onPlayerClosing()
    }
}
