package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.discard_changes_discard
import com.msoula.hobbymatchmaker.core.design.discard_changes_keep_editing
import com.msoula.hobbymatchmaker.core.design.discard_changes_message
import com.msoula.hobbymatchmaker.core.design.discard_changes_title
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsWarning
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.resources.stringResource

/**
 * Asks before leaving an edit screen with unsaved changes. The safe action ("Keep editing") is the
 * emphasized one, and dismissing the dialog also keeps editing.
 */
@Composable
fun DiscardChangesDialog(
    onKeepEditing: () -> Unit,
    onDiscard: () -> Unit
) {
    Dialog(onDismissRequest = onKeepEditing) {
        Surface(
            shape = RoundedCornerShape(CustomSize.TwentyFour),
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .3f))
        ) {
            Column(modifier = Modifier.padding(CustomSize.TwentyFour)) {
                Box(
                    modifier = Modifier
                        .size(CustomSize.SixtyFour)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = .15f),
                            RoundedCornerShape(CustomSize.Sixteen)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MaterialIconsWarning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSize.TwentyFour)
                    )
                }

                SpacerHeight16()
                Text(
                    text = stringResource(Res.string.discard_changes_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                SpacerHeight8()
                Text(
                    text = stringResource(Res.string.discard_changes_message),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .75f),
                    style = MaterialTheme.typography.bodyMedium
                )
                SpacerHeight16()

                Button(
                    onClick = onKeepEditing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(CustomSize.Sixteen),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = stringResource(Res.string.discard_changes_keep_editing))
                }
                SpacerHeight8()
                OutlinedButton(
                    onClick = onDiscard,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(CustomSize.Sixteen),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = .6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(Res.string.discard_changes_discard))
                }
            }
        }
    }
}
