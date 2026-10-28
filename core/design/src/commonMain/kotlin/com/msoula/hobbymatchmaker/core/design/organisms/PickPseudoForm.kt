package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.icons.LucideInfo
import com.msoula.hobbymatchmaker.core.design.icons.LucideLock
import com.msoula.hobbymatchmaker.core.design.models.PseudoStatusUiModel
import com.msoula.hobbymatchmaker.core.design.molecules.InfoNoteCard
import com.msoula.hobbymatchmaker.core.design.molecules.PseudoStatusFeedback
import com.msoula.hobbymatchmaker.core.design.molecules.PseudoTextField
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_first_name_label
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_note_body
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_note_lead
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_pseudo_label
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickPseudoForm(
    firstName: String,
    isFirstNameEditable: Boolean,
    pseudo: String,
    pseudoMaxLength: Int,
    pseudoStatus: PseudoStatusUiModel,
    onFirstNameChanged: (String) -> Unit,
    onPseudoChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)) {
            Text(
                text = stringResource(Res.string.pick_pseudo_first_name_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PrimaryTextField(
                modifier = Modifier.fillMaxWidth(),
                text = firstName,
                singleLine = true,
                readOnly = !isFirstNameEditable,
                onValueChanged = onFirstNameChanged,
                trailingIcon = if (isFirstNameEditable) null else {
                    {
                        Icon(
                            imageVector = LucideLock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(IconSize.Sixteen)
                        )
                    }
                }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)) {
            PseudoTextField(
                label = stringResource(Res.string.pick_pseudo_pseudo_label),
                pseudo = pseudo,
                maxLength = pseudoMaxLength,
                status = pseudoStatus,
                onPseudoChanged = onPseudoChanged
            )
            PseudoStatusFeedback(pseudo = pseudo.trim(), status = pseudoStatus)
        }

        InfoNoteCard(
            icon = LucideInfo,
            lead = stringResource(Res.string.pick_pseudo_note_lead),
            text = stringResource(Res.string.pick_pseudo_note_body)
        )
    }
}
