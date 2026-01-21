package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BootstrapArrowRight: ImageVector
    get() {
        if (_BootstrapArrowRight != null) return _BootstrapArrowRight!!

        _BootstrapArrowRight = ImageVector.Builder(
            name = "arrow-right",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(1f, 8f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
                horizontalLineToRelative(11.793f)
                lineToRelative(-3.147f, -3.146f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.708f, -0.708f)
                lineToRelative(4f, 4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 0.708f)
                lineToRelative(-4f, 4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -0.708f, -0.708f)
                lineTo(13.293f, 8.5f)
                horizontalLineTo(1.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 1f, 8f)
            }
        }.build()

        return _BootstrapArrowRight!!
    }

private var _BootstrapArrowRight: ImageVector? = null

