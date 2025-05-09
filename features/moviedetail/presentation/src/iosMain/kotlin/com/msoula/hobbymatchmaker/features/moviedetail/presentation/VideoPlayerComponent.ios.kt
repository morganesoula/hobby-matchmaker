package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.setValue
import platform.UIKit.UIScreen
import platform.WebKit.WKAudiovisualMediaTypeNone
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import nativeIosPlayerShared.CustomVideoPlayerView

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@Composable
actual fun YoutubeComponent(videoId: String) {
    val url = "https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1"

    UIKitView(
        factory = {
            val view = CustomVideoPlayerView()
            view.configureVideo(url)
            view
        },
        update = { view ->
            view.configureVideo(url)
        }
    )
}

@Composable
actual fun EnterFullScreen() {}

@Composable
actual fun ExitFullScreen() {}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GetScreenHeight(): Float {
    return UIScreen.mainScreen.bounds.useContents { size.height.toFloat() }
}
