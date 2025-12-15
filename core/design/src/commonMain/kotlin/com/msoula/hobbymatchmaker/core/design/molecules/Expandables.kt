package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.show_less
import com.msoula.hobbymatchmaker.core.design.show_more
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource


@Composable
fun MovieOverviewExpandable(
    modifier: Modifier = Modifier,
    overview: String,
    shouldBeExpandable: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var showExpandButton by remember { mutableStateOf(false) }

    Box(modifier = modifier.animateContentSize()) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(CustomSize.Sixteen))
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(CustomSize.Sixteen)
                    )
                    .padding(horizontal = CustomSize.Eight, vertical = CustomSize.Eight)
            ) {
                Text(
                    text = overview,
                    onTextLayout = { layoutResult ->
                        if (!shouldBeExpandable) showExpandButton = false else
                            if (!expanded && layoutResult.lineCount > 6) {
                                showExpandButton = true
                            }
                    },
                    fontSize = CustomFontSize.Sixteen,
                    lineHeight = CustomFontSize.TwentyFour,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = if (isSystemInDarkTheme()) {
                            Shadow(
                                color = MaterialTheme.colorScheme.scrim,
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        } else null
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = CustomSize.Sixteen),
                    maxLines = if (!expanded && showExpandButton) 5 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (showExpandButton) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = if (expanded) stringResource(Res.string.show_less) else stringResource(
                            Res.string.show_more
                        ),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else {
                SpacerHeight8()
            }
        }
    }
}
