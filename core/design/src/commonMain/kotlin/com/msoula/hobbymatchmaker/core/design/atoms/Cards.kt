package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.msoula.hobbymatchmaker.core.design.theme.CardSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun MovieGenericCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    scale: Float
) {
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
