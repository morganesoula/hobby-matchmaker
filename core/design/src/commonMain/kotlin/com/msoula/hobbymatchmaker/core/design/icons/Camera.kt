package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Camera: ImageVector
    get() {
        if (_Camera != null) return _Camera!!

        _Camera = ImageVector.Builder(
            name = "Camera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6.82689f, 6.1749f)
                curveTo(6.46581f, 6.75354f, 5.86127f, 7.13398f, 5.186f, 7.22994f)
                curveTo(4.80655f, 7.28386f, 4.42853f, 7.34223f, 4.05199f, 7.40497f)
                curveTo(2.99912f, 7.58042f, 2.25f, 8.50663f, 2.25f, 9.57402f)
                verticalLineTo(18f)
                curveTo(2.25f, 19.2426f, 3.25736f, 20.25f, 4.5f, 20.25f)
                horizontalLineTo(19.5f)
                curveTo(20.7426f, 20.25f, 21.75f, 19.2426f, 21.75f, 18f)
                verticalLineTo(9.57403f)
                curveTo(21.75f, 8.50664f, 21.0009f, 7.58043f, 19.948f, 7.40498f)
                curveTo(19.5715f, 7.34223f, 19.1934f, 7.28387f, 18.814f, 7.22995f)
                curveTo(18.1387f, 7.13398f, 17.5342f, 6.75354f, 17.1731f, 6.17491f)
                lineTo(16.3519f, 4.85889f)
                curveTo(15.9734f, 4.25237f, 15.3294f, 3.85838f, 14.6155f, 3.82005f)
                curveTo(13.7496f, 3.77355f, 12.8775f, 3.75f, 12f, 3.75f)
                curveTo(11.1225f, 3.75f, 10.2504f, 3.77355f, 9.3845f, 3.82005f)
                curveTo(8.6706f, 3.85838f, 8.02658f, 4.25237f, 7.64809f, 4.85889f)
                lineTo(6.82689f, 6.1749f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.5f, 12.75f)
                curveTo(16.5f, 15.2353f, 14.4853f, 17.25f, 12f, 17.25f)
                curveTo(9.51472f, 17.25f, 7.5f, 15.2353f, 7.5f, 12.75f)
                curveTo(7.5f, 10.2647f, 9.51472f, 8.25f, 12f, 8.25f)
                curveTo(14.4853f, 8.25f, 16.5f, 10.2647f, 16.5f, 12.75f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18.75f, 10.5f)
                horizontalLineTo(18.7575f)
                verticalLineTo(10.5075f)
                horizontalLineTo(18.75f)
                verticalLineTo(10.5f)
                close()
            }
        }.build()

        return _Camera!!
    }

private var _Camera: ImageVector? = null

