package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.setValue
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIInterfaceOrientationLandscapeRight
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun YoutubeComponent(videoId: String) {
    UIKitView(
        factory = {
            val webView =
                WKWebView(frame = CGRectZero.readValue(), configuration = WKWebViewConfiguration())
            val request =
                NSURLRequest(uRL = NSURL(string = "https://www.youtube/com/embed/$videoId"))
            webView.loadRequest(request)
            webView
        },
        update = { webView ->
            val request =
                NSURLRequest(uRL = NSURL(string = "https://www.youtube.com/embed/$videoId"))
            webView.loadRequest(request)
        }
    )
}

@Composable
actual fun EnterFullScreen() {
    val window = UIApplication.sharedApplication.keyWindow
    window?.rootViewController?.let { rootViewController ->
        val fullScreenController = FullScreenViewController()
        rootViewController.presentViewController(fullScreenController, true) {
            fullScreenController.forceLandscapeOrientation()
        }
    }
}

@Composable
actual fun ExitFullScreen() {
    val window = UIApplication.sharedApplication.keyWindow
    window?.rootViewController?.presentedViewController?.let { controller ->
        if (controller is FullScreenViewController) {
            controller.exitFullScreen()
        }
    }
}

class FullScreenViewController : UIViewController(null, null) {
    fun forceLandscapeOrientation() {
        val value = NSNumber(UIInterfaceOrientationLandscapeRight.toInt())
        UIDevice.currentDevice.setValue(value, forKey = "orientation")
    }

    fun exitFullScreen() {
        dismissViewControllerAnimated(true) {
            val value = NSNumber(UIInterfaceOrientationPortrait.toInt())
            UIDevice.currentDevice.setValue(value, forKey = "orientation")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GetScreenHeight(): Float {
    return UIScreen.mainScreen.bounds.useContents { size.height.toFloat() }
}
