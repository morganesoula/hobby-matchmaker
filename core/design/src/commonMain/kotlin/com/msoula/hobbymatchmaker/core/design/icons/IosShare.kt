package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val MaterialIconsIosShare: ImageVector
    get() {
        if (_MaterialIconsIosShare != null) return _MaterialIconsIosShare!!

        _MaterialIconsIosShare = ImageVector.Builder(
            name = "ios_share",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = addPathNodes(
                "M16,5l-1.42,1.42l-1.59,-1.59V16h-1.98V4.83L9.42,6.42L8,5l4,-4L16,5z" +
                    "M20,10v11c0,1.1 -0.9,2 -2,2H6c-1.1,0 -2,-0.9 -2,-2V10c0,-1.11 0.9,-2 2,-2h3v2H6v11h12V10h-3V8h3C19.1,8 20,8.89 20,10z"
            ),
            fill = SolidColor(Color.Black)
        ).build()

        return _MaterialIconsIosShare!!
    }

private var _MaterialIconsIosShare: ImageVector? = null
