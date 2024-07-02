package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackBar = compositionLocalOf<SnackbarHostState> { error("No SnackBarHostState provided") }
