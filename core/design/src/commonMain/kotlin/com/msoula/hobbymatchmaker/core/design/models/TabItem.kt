package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class TabItem(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
)
