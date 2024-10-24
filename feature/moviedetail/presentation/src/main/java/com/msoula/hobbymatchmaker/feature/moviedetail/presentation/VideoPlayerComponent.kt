package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubePlayerComponent(videoId: String) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = {
            YouTubePlayerView(context).apply {
                addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit
                    ) {
                        activity?.window?.let { window ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                window.insetsController?.let { controller ->
                                    controller.hide(WindowInsets.Type.statusBars()
                                        or WindowInsets.Type.navigationBars())
                                    controller.systemBarsBehavior =
                                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                }
                            } else {
                                @Suppress("DEPRECATION")
                                window.decorView.systemUiVisibility = (
                                    View.SYSTEM_UI_FLAG_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    )
                            }
                        }
                    }

                    override fun onExitFullscreen() {
                        activity?.window?.let { window ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                window.insetsController?.show(WindowInsets.Type.statusBars()
                                    or WindowInsets.Type.navigationBars())
                            } else {
                                @Suppress("DEPRECATION")
                                window.decorView.systemUiVisibility = (
                                    View.SYSTEM_UI_FLAG_VISIBLE
                                    )
                            }
                        }
                    }
                })

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId = videoId, 0f)
                    }
                })

                lifecycleOwner.lifecycle.addObserver(this)
            }
        }
    )
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
