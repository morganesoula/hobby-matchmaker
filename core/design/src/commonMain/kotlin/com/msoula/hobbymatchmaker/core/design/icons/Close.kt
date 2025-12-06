package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChromeClose: ImageVector
    get() {
        if (_ChromeClose != null) return _ChromeClose!!

        _ChromeClose = ImageVector.Builder(
            name = "ChromeClose",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(7.116f, 8f)
                lineToRelative(-4.558f, 4.558f)
                lineToRelative(0.884f, 0.884f)
                lineTo(8f, 8.884f)
                lineToRelative(4.558f, 4.558f)
                lineToRelative(0.884f, -0.884f)
                lineTo(8.884f, 8f)
                lineToRelative(4.558f, -4.558f)
                lineToRelative(-0.884f, -0.884f)
                lineTo(8f, 7.116f)
                lineTo(3.442f, 2.558f)
                lineToRelative(-0.884f, 0.884f)
                lineTo(7.116f, 8f)
                close()
            }
        }.build()

        return _ChromeClose!!
    }

private var _ChromeClose: ImageVector? = null

