package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
actual fun YoutubePlayer(
    modifier: Modifier,
    videoId: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val youTubePlayerView = remember { YouTubePlayerView(context) }

    BackHandler {
        onClose()
    }

    AndroidView(
        factory = {
            youTubePlayerView.apply {
                enableAutomaticInitialization = false
                initialize(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    },
                    true
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}
