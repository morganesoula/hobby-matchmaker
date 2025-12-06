package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Clock: ImageVector
    get() {
        if (_Clock != null) return _Clock!!

        _Clock = ImageVector.Builder(
            name = "Clock",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(7.50009f, 0.877014f)
                curveTo(3.84241f, 0.877014f, 0.877258f, 3.84216f, 0.877258f, 7.49984f)
                curveTo(0.877258f, 11.1575f, 3.8424f, 14.1227f, 7.50009f, 14.1227f)
                curveTo(11.1578f, 14.1227f, 14.1229f, 11.1575f, 14.1229f, 7.49984f)
                curveTo(14.1229f, 3.84216f, 11.1577f, 0.877014f, 7.50009f, 0.877014f)
                close()
                moveTo(1.82726f, 7.49984f)
                curveTo(1.82726f, 4.36683f, 4.36708f, 1.82701f, 7.50009f, 1.82701f)
                curveTo(10.6331f, 1.82701f, 13.1729f, 4.36683f, 13.1729f, 7.49984f)
                curveTo(13.1729f, 10.6328f, 10.6331f, 13.1727f, 7.50009f, 13.1727f)
                curveTo(4.36708f, 13.1727f, 1.82726f, 10.6328f, 1.82726f, 7.49984f)
                close()
                moveTo(8f, 4.50001f)
                curveTo(8f, 4.22387f, 7.77614f, 4.00001f, 7.5f, 4.00001f)
                curveTo(7.22386f, 4.00001f, 7f, 4.22387f, 7f, 4.50001f)
                verticalLineTo(7.50001f)
                curveTo(7f, 7.63262f, 7.05268f, 7.7598f, 7.14645f, 7.85357f)
                lineTo(9.14645f, 9.85357f)
                curveTo(9.34171f, 10.0488f, 9.65829f, 10.0488f, 9.85355f, 9.85357f)
                curveTo(10.0488f, 9.65831f, 10.0488f, 9.34172f, 9.85355f, 9.14646f)
                lineTo(8f, 7.29291f)
                verticalLineTo(4.50001f)
                close()
            }
        }.build()

        return _Clock!!
    }

private var _Clock: ImageVector? = null

