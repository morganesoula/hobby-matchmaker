package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val MaterialIconsWarning: ImageVector
    get() {
        if (_MaterialIconsWarning != null) return _MaterialIconsWarning!!

        _MaterialIconsWarning = ImageVector.Builder(
            name = "warning",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = addPathNodes("M1,21h22L12,2 1,21zM13,18h-2v-2h2v2zM13,14h-2v-4h2v4z"),
            fill = SolidColor(Color.Black)
        ).build()

        return _MaterialIconsWarning!!
    }

private var _MaterialIconsWarning: ImageVector? = null
