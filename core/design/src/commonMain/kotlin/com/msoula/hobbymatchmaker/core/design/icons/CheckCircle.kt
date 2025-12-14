package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FeatherCheckCircle: ImageVector
    get() {
        if (_FeatherCheckCircle != null) return _FeatherCheckCircle!!

        _FeatherCheckCircle = ImageVector.Builder(
            name = "check-circle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 11.08f)
                verticalLineTo(12f)
                arcToRelative(10f, 10f, 0f, true, true, -5.93f, -9.14f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 4f)
                lineTo(12f, 14.01f)
                lineTo(9f, 11.01f)
            }
        }.build()

        return _FeatherCheckCircle!!
    }

private var _FeatherCheckCircle: ImageVector? = null



