package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PlatformBackHandler() {
    BackHandler {

    }
}
