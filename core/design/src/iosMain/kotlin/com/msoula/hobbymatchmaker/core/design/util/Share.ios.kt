package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

@Composable
actual fun rememberShareText(): (text: String) -> Unit = remember { { text -> shareText(text) } }

@OptIn(ExperimentalForeignApi::class)
private fun shareText(text: String) {
    var top: UIViewController? = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) top = top.presentedViewController
    val presenter = top ?: return

    val controller = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    // iPad shows the share sheet as a popover, which crashes without an anchor
    controller.popoverPresentationController?.let { popover ->
        popover.sourceView = presenter.view
        popover.sourceRect = presenter.view.bounds.useContents {
            CGRectMake(size.width / 2.0, size.height / 2.0, 0.0, 0.0)
        }
    }
    presenter.presentViewController(controller, animated = true, completion = null)
}
