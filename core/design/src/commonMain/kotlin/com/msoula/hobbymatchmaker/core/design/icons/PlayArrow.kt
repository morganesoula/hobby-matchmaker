package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Play_arrow: ImageVector
    get() {
        if (_Play_arrow != null) return _Play_arrow!!

        _Play_arrow = ImageVector.Builder(
            name = "Play_arrow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(320f, 760f)
                verticalLineToRelative(-560f)
                lineToRelative(440f, 280f)
                close()
                moveToRelative(80f, -146f)
                lineToRelative(210f, -134f)
                lineToRelative(-210f, -134f)
                close()
            }
        }.build()

        return _Play_arrow!!
    }

private var _Play_arrow: ImageVector? = null

