package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BootstrapSendCheck: ImageVector
    get() {
        if (_BootstrapSendCheck != null) return _BootstrapSendCheck!!

        _BootstrapSendCheck = ImageVector.Builder(
            name = "send-check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(15.964f, 0.686f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, -0.65f, -0.65f)
                lineTo(0.767f, 5.855f)
                arcToRelative(0.75f, 0.75f, 0f, false, false, -0.124f, 1.329f)
                lineToRelative(4.995f, 3.178f)
                lineToRelative(1.531f, 2.406f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, 0.844f, -0.536f)
                lineTo(6.637f, 10.07f)
                lineToRelative(7.494f, -7.494f)
                lineToRelative(-1.895f, 4.738f)
                arcToRelative(0.5f, 0.5f, 0f, true, false, 0.928f, 0.372f)
                close()
                moveToRelative(-2.54f, 1.183f)
                lineTo(5.93f, 9.363f)
                lineTo(1.591f, 6.602f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(16f, 12.5f)
                arcToRelative(3.5f, 3.5f, 0f, true, true, -7f, 0f)
                arcToRelative(3.5f, 3.5f, 0f, false, true, 7f, 0f)
                moveToRelative(-1.993f, -1.679f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, -0.686f, 0.172f)
                lineToRelative(-1.17f, 1.95f)
                lineToRelative(-0.547f, -0.547f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, -0.708f, 0.708f)
                lineToRelative(0.774f, 0.773f)
                arcToRelative(0.75f, 0.75f, 0f, false, false, 1.174f, -0.144f)
                lineToRelative(1.335f, -2.226f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, -0.172f, -0.686f)
            }
        }.build()

        return _BootstrapSendCheck!!
    }

private var _BootstrapSendCheck: ImageVector? = null
