package com.msoula.hobbymatchmaker.core.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Hide_image: ImageVector
    get() {
        if (_Hide_image != null) return _Hide_image!!

        _Hide_image = ImageVector.Builder(
            name = "Hide_image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveToRelative(840f, -234f)
                lineToRelative(-80f, -80f)
                verticalLineToRelative(-446f)
                horizontalLineTo(314f)
                lineToRelative(-80f, -80f)
                horizontalLineToRelative(526f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                close()
                moveTo(792f, 904f)
                lineToRelative(-64f, -64f)
                horizontalLineTo(200f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-528f)
                lineToRelative(-64f, -64f)
                lineToRelative(56f, -56f)
                lineToRelative(736f, 736f)
                close()
                moveTo(240f, 680f)
                lineToRelative(120f, -160f)
                lineToRelative(90f, 120f)
                lineToRelative(33f, -44f)
                lineToRelative(-283f, -283f)
                verticalLineToRelative(447f)
                horizontalLineToRelative(447f)
                lineToRelative(-80f, -80f)
                close()
                moveToRelative(184f, -144f)
            }
        }.build()

        return _Hide_image!!
    }

private var _Hide_image: ImageVector? = null

