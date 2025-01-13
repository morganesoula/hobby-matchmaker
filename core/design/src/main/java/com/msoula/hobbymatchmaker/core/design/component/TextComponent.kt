package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun ExpandableTextComponent(
    modifier: Modifier = Modifier,
    text: String,
    showLess: String,
    showMore: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

    val textLayoutResult = textLayoutResultState.value

    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect
    }

    if (textLayoutResult != null) {
        when {
            isExpanded -> {
                finalText = text
            }

            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(6 - 1).coerceAtMost(text.length)
                val adjustedText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLast(showMore.length)
                    .dropLastWhile { it == ' ' || it == '.' }

                finalText = adjustedText
                isClickable = true
            }
        }
    }

    val displayText = buildAnnotatedString {
        append(finalText)
        if (isExpanded) {
            append(" ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(showLess)
            }
        } else {
            append(" ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(showMore)
            }
        }
    }

    Text(
        text = displayText,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = if (isExpanded) Int.MAX_VALUE else 6,
        onTextLayout = { textLayoutResultState.value = it },
        modifier = modifier
            .clickable(isClickable) { isExpanded = !isExpanded }
            .animateContentSize()
    )
}
