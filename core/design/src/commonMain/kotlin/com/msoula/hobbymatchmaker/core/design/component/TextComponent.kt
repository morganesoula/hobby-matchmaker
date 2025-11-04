package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    val isDarkTheme = isSystemInDarkTheme()
    var expanded by remember { mutableStateOf(false) }
    var showExpandButton by remember { mutableStateOf(false) }

    Box(modifier = modifier.animateContentSize()) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
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
                        shadow = if (isDarkTheme) {
                            Shadow(
                                color = MaterialTheme.colorScheme.scrim,
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        } else null
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
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
                        text = if (expanded) showLess else showMore,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun rememberSubmitKeyBoardActions(onSubmit: (() -> Unit)?): KeyboardActions {
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current

    return KeyboardActions(
        onNext = { focus.moveFocus(FocusDirection.Down) },
        onDone = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        },
        onSend = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        },
        onGo = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        }
    )
}


