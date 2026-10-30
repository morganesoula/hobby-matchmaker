package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_bio_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_name_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_pseudo_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_title
import com.msoula.hobbymatchmaker.core.design.models.PseudoStatusUiModel
import com.msoula.hobbymatchmaker.core.design.molecules.PseudoTextField
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirementsList
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileEditInformationForm(
    modifier: Modifier = Modifier,
    name: String,
    pseudo: String,
    onNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    bio: String? = null,
    nameRequirements: ImmutableList<ValidationRequirement> = persistentListOf(),
) {
    GenericCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = CustomSize.Eight, end = CustomSize.Eight),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Text(
                text = stringResource(Res.string.edit_profile_basic_information_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            SpacerHeight16()
            Text(
                text = stringResource(Res.string.edit_profile_basic_information_name_title),
                color = MaterialTheme.colorScheme.onSurface
            )
            PrimaryTextField(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                singleLine = true,
                onValueChanged = onNameChanged,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            SpacerHeight4()

            if (nameRequirements.isNotEmpty()) {
                if (nameRequirements.any { !it.isValid }) {
                    ValidationRequirementsList(
                        requirements = nameRequirements
                    )

                    SpacerHeight8()
                }
            }

            SpacerHeight8()
            // The pseudo is chosen once during onboarding (PickPseudo) and never edited here
            PseudoTextField(
                label = stringResource(Res.string.edit_profile_basic_information_pseudo_title),
                pseudo = pseudo,
                maxLength = pseudo.length,
                status = PseudoStatusUiModel.Idle,
                onPseudoChanged = {},
                readOnly = true
            )
            SpacerHeight16()

            Text(
                text = stringResource(Res.string.edit_profile_basic_information_bio_title),
                color = MaterialTheme.colorScheme.onSurface
            )
            PrimaryTextField(
                modifier = Modifier.fillMaxWidth(),
                text = bio ?: "",
                singleLine = false,
                onValueChanged = onBioChanged,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                showSupportingText = true
            )
        }
    }
}
