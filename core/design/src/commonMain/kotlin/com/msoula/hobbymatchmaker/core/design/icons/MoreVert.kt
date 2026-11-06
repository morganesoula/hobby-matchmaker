package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbolsMoreVert: ImageVector
    get() {
        if (_MaterialSymbolsMoreVert != null) return _MaterialSymbolsMoreVert!!

        _MaterialSymbolsMoreVert = ImageVector.Builder(
            name = "more_vert",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                listOf(240f, 480f, 720f).forEach { centerY ->
                    moveTo(400f, centerY)
                    arcToRelative(80f, 80f, 0f, true, true, 160f, 0f)
                    arcToRelative(80f, 80f, 0f, true, true, -160f, 0f)
                    close()
                }
            }
        }.build()

        return _MaterialSymbolsMoreVert!!
    }

private var _MaterialSymbolsMoreVert: ImageVector? = null
