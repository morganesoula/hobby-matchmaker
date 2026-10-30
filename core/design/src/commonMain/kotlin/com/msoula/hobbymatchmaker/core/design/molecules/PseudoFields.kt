package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapCheck
import com.msoula.hobbymatchmaker.core.design.icons.LucideLock
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsError_outline
import com.msoula.hobbymatchmaker.core.design.models.PseudoStatusUiModel
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_checking
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_counter
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_placeholder
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_taken
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.resources.stringResource

/** Labelled field with an `@` prefix, an `n/max` counter and an availability icon. */
@Composable
fun PseudoTextField(
    label: String,
    pseudo: String,
    maxLength: Int,
    status: PseudoStatusUiModel,
    onPseudoChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val isError = status is PseudoStatusUiModel.Taken || status is PseudoStatusUiModel.Invalid
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (!readOnly) {
                Text(
                    text = stringResource(Res.string.pick_pseudo_counter, pseudo.length, maxLength),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = pseudo,
            onValueChange = { if (it.length <= maxLength) onPseudoChanged(it) },
            readOnly = readOnly,
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(CustomSize.Sixteen),
            placeholder = { Text(stringResource(Res.string.pick_pseudo_placeholder)) },
            prefix = {
                Text(
                    text = "@",
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = CustomSize.Four)
                )
            },
            trailingIcon = {
                if (readOnly) {
                    Icon(
                        imageVector = LucideLock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(IconSize.Sixteen)
                    )
                } else {
                    PseudoStatusIcon(status)
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = primary,
                unfocusedBorderColor = primary.copy(alpha = .3f),
                cursorColor = primary
            )
        )
    }
}

@Composable
private fun PseudoStatusIcon(status: PseudoStatusUiModel) {
    when (status) {
        PseudoStatusUiModel.Checking -> CircularProgressIndicator(
            modifier = Modifier.size(IconSize.TwentyFour),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = CustomSize.Two
        )

        PseudoStatusUiModel.Available -> StatusDot(
            color = MaterialTheme.colorScheme.tertiary,
            content = {
                Icon(
                    imageVector = BootstrapCheck,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(IconSize.Sixteen)
                )
            }
        )

        PseudoStatusUiModel.Taken -> StatusDot(
            color = MaterialTheme.colorScheme.error,
            content = {
                Text(
                    text = "!",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        else -> Unit
    }
}

@Composable
private fun StatusDot(color: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(IconSize.TwentyFour)
            .background(color.copy(alpha = .15f), CircleShape)
            .border(1.dp, color.copy(alpha = .4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/** Line under [PseudoTextField] explaining the current availability status. */
@Composable
fun PseudoStatusFeedback(
    pseudo: String,
    status: PseudoStatusUiModel,
    modifier: Modifier = Modifier
) {
    when (status) {
        // Available needs no line: the green check in the field already says it
        PseudoStatusUiModel.Idle, PseudoStatusUiModel.Available -> Unit
        is PseudoStatusUiModel.Invalid -> ValidationRequirementsList(
            requirements = status.requirements,
            modifier = modifier
        )

        PseudoStatusUiModel.Checking -> FeedbackRow(
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
        ) {
            Text(
                text = stringResource(Res.string.pick_pseudo_checking),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        PseudoStatusUiModel.Taken -> FeedbackRow(
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        ) {
            Text(
                text = stringResource(Res.string.pick_pseudo_taken, pseudo),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun FeedbackRow(
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector = MaterialIconsError_outline,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Four),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(IconSize.Sixteen)
        )
        content()
    }
}
