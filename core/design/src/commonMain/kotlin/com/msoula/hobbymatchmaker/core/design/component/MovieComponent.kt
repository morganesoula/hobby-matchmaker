package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun HMMShimmerEffect(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {

    if (isLoading) {
        Box(
            modifier =
            modifier
                .size(300.dp, 450.dp)
                .clip(RoundedCornerShape(5))
                .shimmerEffect(),
        )
        Spacer(modifier = Modifier.width(16.dp))
    } else {
        contentAfterLoading()
    }
}

fun Modifier.shimmerEffect(): Modifier =
    composed {
        var size by remember {
            mutableStateOf(IntSize.Zero)
        }

        val transition = rememberInfiniteTransition(label = "")
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec =
            infiniteRepeatable(
                animation = tween(1000),
            ),
            label = "",
        )

        background(
            brush =
            Brush.linearGradient(
                colors =
                listOf(
                    Color(0xFFB8B5B5),
                    Color(0xFF8F8B8B),
                    Color(0xFFB8B5B5),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
            ),
        )
            .onGloballyPositioned {
                size = it.size
            }
    }
