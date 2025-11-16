package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.msoula.hobbymatchmaker.core.design.theme.CardSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlin.math.abs

@Composable
fun MovieGenericCard(
    modifier: Modifier = Modifier,
    state: LazyListState,
    index: Int,
    content: @Composable () -> Unit
) {
    val scale by remember {
        derivedStateOf {
            val currentItem =
                state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                    ?: return@derivedStateOf 1.0f
            val halfRowWidth = state.layoutInfo.viewportSize.width / 2
            (
                1f - minOf(
                    1f,
                    abs(currentItem.offset + (currentItem.size / 2) - halfRowWidth).toFloat() / halfRowWidth,
                ) * 0.10f
                )
        }
    }

    Card(
        modifier = modifier
            .width(CardSize.Width300)
            .height(CardSize.Height440)
            .padding(CustomSize.Eight)
            .scale(scale)
            .zIndex(scale * 10),
        shape = RoundedCornerShape(CustomSize.Eight),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (scale > 1f) CustomSize.Sixteen else CustomSize.Four
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        content()
    }
}

@Composable
fun GenericCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CustomSize.Sixteen),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(modifier = Modifier.padding(CustomSize.Sixteen)) {
            content()
        }
    }
}

@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush)
    )
}

@Composable
fun ShimmerRectangle(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    cornerRadius: Dp = 12.dp
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .3f)
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(x = offset, y = 0f),
        end = androidx.compose.ui.geometry.Offset(x = offset + 200f, y = 200f)
    )
}
