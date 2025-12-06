package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.msoula.hobbymatchmaker.core.design.util.UIText

data class EmptyStateConfig(
    val icon: ImageVector,
    val title: UIText,
    val description: UIText? = null,
    val ctaText: UIText? = null,
    val ctaAction: (() -> Unit)? = null
)
