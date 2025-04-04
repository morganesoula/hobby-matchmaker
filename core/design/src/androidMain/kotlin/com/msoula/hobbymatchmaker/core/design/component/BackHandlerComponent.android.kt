package com.msoula.hobbymatchmaker.core.design.component

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.LocalContext
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PlatformBackHandler() {
    val activity = LocalContext.current as? Activity

    BackHandler {
        activity?.finishAffinity()
        exitProcess(0)
    }
}
