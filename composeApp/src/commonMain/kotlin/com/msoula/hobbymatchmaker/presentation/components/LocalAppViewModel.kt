package com.msoula.hobbymatchmaker.presentation.components

import androidx.compose.runtime.compositionLocalOf
import com.msoula.hobbymatchmaker.presentation.AppViewModel

val LocalAppViewModel = compositionLocalOf<AppViewModel> {
    error("No AppViewModel provided")
}
