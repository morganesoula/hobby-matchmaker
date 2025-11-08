package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.util.UIText
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun MediumTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun MediumBodyText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    softWrap: Boolean? = null,
    overflow: TextOverflow? = null
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = fontWeight,
        textAlign = textAlign,
        softWrap = softWrap == true,
        overflow = overflow ?: TextOverflow.Clip
    )
}

@Composable
fun SmallBodyText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            fontStyle = FontStyle.Italic
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ErrorText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun EmptyDataText(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(Res.string.no_data),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun Modifier.keyboardDismissOnTap(): Modifier {
    val controller = LocalSoftwareKeyboardController.current
    return clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { controller?.hide() }
}

@Composable
fun UIText.asString(): String = when (this) {
    is UIText.Plain -> value
    is UIText.Resource -> stringResource(res, *args.toTypedArray())
}
