package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LucideHeart: ImageVector
    get() {
        if (_LucideHeart != null) return _LucideHeart!!

        _LucideHeart = ImageVector.Builder(
            name = "heart",
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
                moveTo(2f, 9.5f)
                arcToRelative(5.5f, 5.5f, 0f, false, true, 9.591f, -3.676f)
                arcToRelative(0.56f, 0.56f, 0f, false, false, 0.818f, 0f)
                arcTo(5.49f, 5.49f, 0f, false, true, 22f, 9.5f)
                curveToRelative(0f, 2.29f, -1.5f, 4f, -3f, 5.5f)
                lineToRelative(-5.492f, 5.313f)
                arcToRelative(2f, 2f, 0f, false, true, -3f, 0.019f)
                lineTo(5f, 15f)
                curveToRelative(-1.5f, -1.5f, -3f, -3.2f, -3f, -5.5f)
            }
        }.build()

        return _LucideHeart!!
    }

private var _LucideHeart: ImageVector? = null
