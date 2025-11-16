package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Person: ImageVector
    get() {
        if (_Person != null) return _Person!!

        _Person = ImageVector.Builder(
            name = "Person",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(7.5f, 0.875f)
                curveTo(5.49797f, 0.875f, 3.875f, 2.49797f, 3.875f, 4.5f)
                curveTo(3.875f, 6.15288f, 4.98124f, 7.54738f, 6.49373f, 7.98351f)
                curveTo(5.2997f, 8.12901f, 4.27557f, 8.55134f, 3.50407f, 9.31167f)
                curveTo(2.52216f, 10.2794f, 2.02502f, 11.72f, 2.02502f, 13.5999f)
                curveTo(2.02502f, 13.8623f, 2.23769f, 14.0749f, 2.50002f, 14.0749f)
                curveTo(2.76236f, 14.0749f, 2.97502f, 13.8623f, 2.97502f, 13.5999f)
                curveTo(2.97502f, 11.8799f, 3.42786f, 10.7206f, 4.17091f, 9.9883f)
                curveTo(4.91536f, 9.25463f, 6.02674f, 8.87499f, 7.49995f, 8.87499f)
                curveTo(8.97317f, 8.87499f, 10.0846f, 9.25463f, 10.8291f, 9.98831f)
                curveTo(11.5721f, 10.7206f, 12.025f, 11.8799f, 12.025f, 13.5999f)
                curveTo(12.025f, 13.8623f, 12.2376f, 14.0749f, 12.5f, 14.0749f)
                curveTo(12.7623f, 14.075f, 12.975f, 13.8623f, 12.975f, 13.6f)
                curveTo(12.975f, 11.72f, 12.4778f, 10.2794f, 11.4959f, 9.31166f)
                curveTo(10.7244f, 8.55135f, 9.70025f, 8.12903f, 8.50625f, 7.98352f)
                curveTo(10.0187f, 7.5474f, 11.125f, 6.15289f, 11.125f, 4.5f)
                curveTo(11.125f, 2.49797f, 9.50203f, 0.875f, 7.5f, 0.875f)
                close()
                moveTo(4.825f, 4.5f)
                curveTo(4.825f, 3.02264f, 6.02264f, 1.825f, 7.5f, 1.825f)
                curveTo(8.97736f, 1.825f, 10.175f, 3.02264f, 10.175f, 4.5f)
                curveTo(10.175f, 5.97736f, 8.97736f, 7.175f, 7.5f, 7.175f)
                curveTo(6.02264f, 7.175f, 4.825f, 5.97736f, 4.825f, 4.5f)
                close()
            }
        }.build()

        return _Person!!
    }

private var _Person: ImageVector? = null

