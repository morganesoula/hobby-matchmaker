package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Film: ImageVector
    get() {
        if (_Film != null) return _Film!!

        _Film = ImageVector.Builder(
            name = "Film",
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
                moveTo(3.375f, 19.5f)
                horizontalLineTo(20.625f)
                moveTo(3.375f, 19.5f)
                curveTo(2.75368f, 19.5f, 2.25f, 18.9963f, 2.25f, 18.375f)
                moveTo(3.375f, 19.5f)
                horizontalLineTo(4.875f)
                curveTo(5.49632f, 19.5f, 6f, 18.9963f, 6f, 18.375f)
                moveTo(2.25f, 18.375f)
                verticalLineTo(5.625f)
                moveTo(2.25f, 18.375f)
                verticalLineTo(16.875f)
                curveTo(2.25f, 16.2537f, 2.75368f, 15.75f, 3.375f, 15.75f)
                moveTo(21.75f, 18.375f)
                verticalLineTo(5.625f)
                moveTo(21.75f, 18.375f)
                curveTo(21.75f, 18.9963f, 21.2463f, 19.5f, 20.625f, 19.5f)
                moveTo(21.75f, 18.375f)
                verticalLineTo(16.875f)
                curveTo(21.75f, 16.2537f, 21.2463f, 15.75f, 20.625f, 15.75f)
                moveTo(20.625f, 19.5f)
                horizontalLineTo(19.125f)
                curveTo(18.5037f, 19.5f, 18f, 18.9963f, 18f, 18.375f)
                moveTo(20.625f, 4.5f)
                horizontalLineTo(3.375f)
                moveTo(20.625f, 4.5f)
                curveTo(21.2463f, 4.5f, 21.75f, 5.00368f, 21.75f, 5.625f)
                moveTo(20.625f, 4.5f)
                horizontalLineTo(19.125f)
                curveTo(18.5037f, 4.5f, 18f, 5.00368f, 18f, 5.625f)
                moveTo(21.75f, 5.625f)
                verticalLineTo(7.125f)
                curveTo(21.75f, 7.74632f, 21.2463f, 8.25f, 20.625f, 8.25f)
                moveTo(3.375f, 4.5f)
                curveTo(2.75368f, 4.5f, 2.25f, 5.00368f, 2.25f, 5.625f)
                moveTo(3.375f, 4.5f)
                horizontalLineTo(4.875f)
                curveTo(5.49632f, 4.5f, 6f, 5.00368f, 6f, 5.625f)
                moveTo(2.25f, 5.625f)
                verticalLineTo(7.125f)
                curveTo(2.25f, 7.74632f, 2.75368f, 8.25f, 3.375f, 8.25f)
                moveTo(3.375f, 8.25f)
                horizontalLineTo(4.875f)
                moveTo(3.375f, 8.25f)
                curveTo(2.75368f, 8.25f, 2.25f, 8.75368f, 2.25f, 9.375f)
                verticalLineTo(10.875f)
                curveTo(2.25f, 11.4963f, 2.75368f, 12f, 3.375f, 12f)
                moveTo(4.875f, 8.25f)
                curveTo(5.49632f, 8.25f, 6f, 7.74632f, 6f, 7.125f)
                verticalLineTo(5.625f)
                moveTo(4.875f, 8.25f)
                curveTo(5.49632f, 8.25f, 6f, 8.75368f, 6f, 9.375f)
                verticalLineTo(10.875f)
                moveTo(6f, 5.625f)
                verticalLineTo(10.875f)
                moveTo(6f, 5.625f)
                curveTo(6f, 5.00368f, 6.50368f, 4.5f, 7.125f, 4.5f)
                horizontalLineTo(16.875f)
                curveTo(17.4963f, 4.5f, 18f, 5.00368f, 18f, 5.625f)
                moveTo(19.125f, 8.25f)
                horizontalLineTo(20.625f)
                moveTo(19.125f, 8.25f)
                curveTo(18.5037f, 8.25f, 18f, 7.74632f, 18f, 7.125f)
                verticalLineTo(5.625f)
                moveTo(19.125f, 8.25f)
                curveTo(18.5037f, 8.25f, 18f, 8.75368f, 18f, 9.375f)
                verticalLineTo(10.875f)
                moveTo(20.625f, 8.25f)
                curveTo(21.2463f, 8.25f, 21.75f, 8.75368f, 21.75f, 9.375f)
                verticalLineTo(10.875f)
                curveTo(21.75f, 11.4963f, 21.2463f, 12f, 20.625f, 12f)
                moveTo(18f, 5.625f)
                verticalLineTo(10.875f)
                moveTo(7.125f, 12f)
                horizontalLineTo(16.875f)
                moveTo(7.125f, 12f)
                curveTo(6.50368f, 12f, 6f, 11.4963f, 6f, 10.875f)
                moveTo(7.125f, 12f)
                curveTo(6.50368f, 12f, 6f, 12.5037f, 6f, 13.125f)
                moveTo(6f, 10.875f)
                curveTo(6f, 11.4963f, 5.49632f, 12f, 4.875f, 12f)
                moveTo(18f, 10.875f)
                curveTo(18f, 11.4963f, 17.4963f, 12f, 16.875f, 12f)
                moveTo(18f, 10.875f)
                curveTo(18f, 11.4963f, 18.5037f, 12f, 19.125f, 12f)
                moveTo(16.875f, 12f)
                curveTo(17.4963f, 12f, 18f, 12.5037f, 18f, 13.125f)
                moveTo(6f, 18.375f)
                verticalLineTo(13.125f)
                moveTo(6f, 18.375f)
                curveTo(6f, 18.9963f, 6.50368f, 19.5f, 7.125f, 19.5f)
                horizontalLineTo(16.875f)
                curveTo(17.4963f, 19.5f, 18f, 18.9963f, 18f, 18.375f)
                moveTo(6f, 18.375f)
                verticalLineTo(16.875f)
                curveTo(6f, 16.2537f, 5.49632f, 15.75f, 4.875f, 15.75f)
                moveTo(18f, 18.375f)
                verticalLineTo(13.125f)
                moveTo(18f, 18.375f)
                verticalLineTo(16.875f)
                curveTo(18f, 16.2537f, 18.5037f, 15.75f, 19.125f, 15.75f)
                moveTo(18f, 13.125f)
                verticalLineTo(14.625f)
                curveTo(18f, 15.2463f, 18.5037f, 15.75f, 19.125f, 15.75f)
                moveTo(18f, 13.125f)
                curveTo(18f, 12.5037f, 18.5037f, 12f, 19.125f, 12f)
                moveTo(6f, 13.125f)
                verticalLineTo(14.625f)
                curveTo(6f, 15.2463f, 5.49632f, 15.75f, 4.875f, 15.75f)
                moveTo(6f, 13.125f)
                curveTo(6f, 12.5037f, 5.49632f, 12f, 4.875f, 12f)
                moveTo(3.375f, 12f)
                horizontalLineTo(4.875f)
                moveTo(3.375f, 12f)
                curveTo(2.75368f, 12f, 2.25f, 12.5037f, 2.25f, 13.125f)
                verticalLineTo(14.625f)
                curveTo(2.25f, 15.2463f, 2.75368f, 15.75f, 3.375f, 15.75f)
                moveTo(19.125f, 12f)
                horizontalLineTo(20.625f)
                moveTo(20.625f, 12f)
                curveTo(21.2463f, 12f, 21.75f, 12.5037f, 21.75f, 13.125f)
                verticalLineTo(14.625f)
                curveTo(21.75f, 15.2463f, 21.2463f, 15.75f, 20.625f, 15.75f)
                moveTo(3.375f, 15.75f)
                horizontalLineTo(4.875f)
                moveTo(19.125f, 15.75f)
                horizontalLineTo(20.625f)
            }
        }.build()

        return _Film!!
    }

private var _Film: ImageVector? = null
