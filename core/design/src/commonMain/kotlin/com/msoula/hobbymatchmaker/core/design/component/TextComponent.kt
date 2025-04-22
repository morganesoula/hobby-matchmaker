package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
                    .height(if (!shouldBeExpandable) Dp.Unspecified else if (expanded) Dp.Unspecified else 100.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = text,
                    onTextLayout = { layoutResult ->
                        if (!shouldBeExpandable) showExpandButton = false else
                            if (!expanded && layoutResult.lineCount > 6) {
                                showExpandButton = true
                            }
                    }
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
            }
        }
    }
}


