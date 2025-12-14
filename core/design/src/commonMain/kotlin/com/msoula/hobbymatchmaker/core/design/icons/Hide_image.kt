package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialIconsHide_image: ImageVector
    get() {
        if (_MaterialIconsHide_image != null) return _MaterialIconsHide_image!!

        _MaterialIconsHide_image = ImageVector.Builder(
            name = "hide_image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color.Black)
                ) {
                    moveTo(0f, 0f)
                    horizontalLineTo(24f)
                    verticalLineTo(24f)
                    horizontalLineTo(0f)
                    verticalLineTo(0f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color.Black)
                ) {
                }
            }
        }.build()

        return _MaterialIconsHide_image!!
    }

private var _MaterialIconsHide_image: ImageVector? = null
