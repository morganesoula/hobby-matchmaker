package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableTextComponent(
    modifier: Modifier = Modifier,
    text: String,
    showLess: String,
    showMore: String,
    shouldBeExpandable: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var showExpandButton by remember { mutableStateOf(false) }

    Box(modifier = modifier.animateContentSize()) {
        Column {
            Box(
                modifier = Modifier
                    .then(
                        if (shouldBeExpandable && !expanded && showExpandButton) {
                            Modifier.height(100.dp)
                        } else Modifier.wrapContentHeight()
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.9f),
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = text,
                    onTextLayout = { layoutResult ->
                        if (!shouldBeExpandable) showExpandButton = false else
                            if (!expanded && layoutResult.lineCount > 6) {
                                showExpandButton = true
                            }
                    },
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                )

                if (!expanded && showExpandButton) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            if (showExpandButton) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (expanded) showLess else showMore)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


