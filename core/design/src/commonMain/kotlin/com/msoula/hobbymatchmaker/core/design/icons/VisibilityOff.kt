package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Visibility_off: ImageVector
    get() {
        if (_Visibility_off != null) return _Visibility_off!!

        _Visibility_off = ImageVector.Builder(
            name = "Visibility_off",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveToRelative(644f, -428f)
                lineToRelative(-58f, -58f)
                quadToRelative(9f, -47f, -27f, -88f)
                reflectiveQuadToRelative(-93f, -32f)
                lineToRelative(-58f, -58f)
                quadToRelative(17f, -8f, 34.5f, -12f)
                reflectiveQuadToRelative(37.5f, -4f)
                quadToRelative(75f, 0f, 127.5f, 52.5f)
                reflectiveQuadTo(660f, 460f)
                quadToRelative(0f, 20f, -4f, 37.5f)
                reflectiveQuadTo(644f, 532f)
                moveToRelative(128f, 126f)
                lineToRelative(-58f, -56f)
                quadToRelative(38f, -29f, 67.5f, -63.5f)
                reflectiveQuadTo(832f, 460f)
                quadToRelative(-50f, -101f, -143.5f, -160.5f)
                reflectiveQuadTo(480f, 240f)
                quadToRelative(-29f, 0f, -57f, 4f)
                reflectiveQuadToRelative(-55f, 12f)
                lineToRelative(-62f, -62f)
                quadToRelative(41f, -17f, 84f, -25.5f)
                reflectiveQuadToRelative(90f, -8.5f)
                quadToRelative(151f, 0f, 269f, 83.5f)
                reflectiveQuadTo(920f, 460f)
                quadToRelative(-23f, 59f, -60.5f, 109.5f)
                reflectiveQuadTo(772f, 658f)
                moveToRelative(20f, 246f)
                lineTo(624f, 738f)
                quadToRelative(-35f, 11f, -70.5f, 16.5f)
                reflectiveQuadTo(480f, 760f)
                quadToRelative(-151f, 0f, -269f, -83.5f)
                reflectiveQuadTo(40f, 460f)
                quadToRelative(21f, -53f, 53f, -98.5f)
                reflectiveQuadToRelative(73f, -81.5f)
                lineTo(56f, 168f)
                lineToRelative(56f, -56f)
                lineToRelative(736f, 736f)
                close()
                moveTo(222f, 336f)
                quadToRelative(-29f, 26f, -53f, 57f)
                reflectiveQuadToRelative(-41f, 67f)
                quadToRelative(50f, 101f, 143.5f, 160.5f)
                reflectiveQuadTo(480f, 680f)
                quadToRelative(20f, 0f, 39f, -2.5f)
                reflectiveQuadToRelative(39f, -5.5f)
                lineToRelative(-36f, -38f)
                quadToRelative(-11f, 3f, -21f, 4.5f)
                reflectiveQuadToRelative(-21f, 1.5f)
                quadToRelative(-75f, 0f, -127.5f, -52.5f)
                reflectiveQuadTo(300f, 460f)
                quadToRelative(0f, -11f, 1.5f, -21f)
                reflectiveQuadToRelative(4.5f, -21f)
                close()
                moveToRelative(168f, 168f)
            }
        }.build()

        return _Visibility_off!!
    }

private var _Visibility_off: ImageVector? = null

