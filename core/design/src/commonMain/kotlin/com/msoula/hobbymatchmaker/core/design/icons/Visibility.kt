package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Visibility: ImageVector
    get() {
        if (_Visibility != null) return _Visibility!!

        _Visibility = ImageVector.Builder(
            name = "Visibility",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(480f, 640f)
                quadToRelative(75f, 0f, 127.5f, -52.5f)
                reflectiveQuadTo(660f, 460f)
                reflectiveQuadToRelative(-52.5f, -127.5f)
                reflectiveQuadTo(480f, 280f)
                reflectiveQuadToRelative(-127.5f, 52.5f)
                reflectiveQuadTo(300f, 460f)
                reflectiveQuadToRelative(52.5f, 127.5f)
                reflectiveQuadTo(480f, 640f)
                moveToRelative(0f, -72f)
                quadToRelative(-45f, 0f, -76.5f, -31.5f)
                reflectiveQuadTo(372f, 460f)
                reflectiveQuadToRelative(31.5f, -76.5f)
                reflectiveQuadTo(480f, 352f)
                reflectiveQuadToRelative(76.5f, 31.5f)
                reflectiveQuadTo(588f, 460f)
                reflectiveQuadToRelative(-31.5f, 76.5f)
                reflectiveQuadTo(480f, 568f)
                moveToRelative(0f, 192f)
                quadToRelative(-146f, 0f, -266f, -81.5f)
                reflectiveQuadTo(40f, 460f)
                quadToRelative(54f, -137f, 174f, -218.5f)
                reflectiveQuadTo(480f, 160f)
                reflectiveQuadToRelative(266f, 81.5f)
                reflectiveQuadTo(920f, 460f)
                quadToRelative(-54f, 137f, -174f, 218.5f)
                reflectiveQuadTo(480f, 760f)
                moveToRelative(0f, -80f)
                quadToRelative(113f, 0f, 207.5f, -59.5f)
                reflectiveQuadTo(832f, 460f)
                quadToRelative(-50f, -101f, -144.5f, -160.5f)
                reflectiveQuadTo(480f, 240f)
                reflectiveQuadToRelative(-207.5f, 59.5f)
                reflectiveQuadTo(128f, 460f)
                quadToRelative(50f, 101f, 144.5f, 160.5f)
                reflectiveQuadTo(480f, 680f)
            }
        }.build()

        return _Visibility!!
    }

private var _Visibility: ImageVector? = null

