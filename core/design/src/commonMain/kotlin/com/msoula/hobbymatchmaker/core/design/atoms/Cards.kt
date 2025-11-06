package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
