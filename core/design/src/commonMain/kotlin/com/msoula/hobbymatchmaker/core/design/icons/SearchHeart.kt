package com.msoula.hobbymatchmaker.core.design.icons

/*
The MIT License (MIT)

Copyright (c) 2019-2024 The Bootstrap Authors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BootstrapSearchHeart: ImageVector
    get() {
        if (_BootstrapSearchHeart != null) return _BootstrapSearchHeart!!

        _BootstrapSearchHeart = ImageVector.Builder(
            name = "search-heart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(6.5f, 4.482f)
                curveToRelative(1.664f, -1.673f, 5.825f, 1.254f, 0f, 5.018f)
                curveToRelative(-5.825f, -3.764f, -1.664f, -6.69f, 0f, -5.018f)
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(13f, 6.5f)
                arcToRelative(6.47f, 6.47f, 0f, false, true, -1.258f, 3.844f)
                quadToRelative(0.06f, 0.044f, 0.115f, 0.098f)
                lineToRelative(3.85f, 3.85f)
                arcToRelative(1f, 1f, 0f, false, true, -1.414f, 1.415f)
                lineToRelative(-3.85f, -3.85f)
                arcToRelative(1f, 1f, 0f, false, true, -0.1f, -0.115f)
                horizontalLineToRelative(0.002f)
                arcTo(6.5f, 6.5f, 0f, true, true, 13f, 6.5f)
                moveTo(6.5f, 12f)
                arcToRelative(5.5f, 5.5f, 0f, true, false, 0f, -11f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, 0f, 11f)
            }
        }.build()

        return _BootstrapSearchHeart!!
    }

private var _BootstrapSearchHeart: ImageVector? = null

