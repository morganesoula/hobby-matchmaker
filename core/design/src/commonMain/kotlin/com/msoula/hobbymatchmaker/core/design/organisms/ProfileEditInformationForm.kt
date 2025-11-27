package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_description
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_bio_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_name_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_pseudo_hint
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_pseudo_not_available
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_pseudo_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_title
import com.msoula.hobbymatchmaker.core.design.icons.Lightbulb
import com.msoula.hobbymatchmaker.core.design.icons.PersonExclamation
import com.msoula.hobbymatchmaker.core.design.molecules.TipTextField
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirementsList
import com.msoula.hobbymatchmaker.core.design.molecules.WarningTextField
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditInformationForm(
    modifier: Modifier = Modifier,
    name: String,
    pseudo: String,
    onNameChanged: (String) -> Unit,
    onPseudoChanged: (String) -> Unit,
    onPseudoFocusLost: () -> Unit,
    isPseudoAvailable: Boolean?,
    onBioChanged: (String) -> Unit,
    bio: String? = null,
    nameRequirements: List<ValidationRequirement> = emptyList(),
    pseudoRequirements: List<ValidationRequirement> = emptyList(),
) {
    val pseudoFieldFocused = rememberSaveable { mutableStateOf(false) }

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
            Text(
                text = stringResource(Res.string.edit_profile_basic_information_pseudo_title),
                color = MaterialTheme.colorScheme.onSurface
            )
            SpacerHeight8()

            TipTextField(
                hintText = stringResource(Res.string.edit_profile_basic_information_pseudo_hint),
                icon = Lightbulb
            )

            PrimaryTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        val wasFocused = pseudoFieldFocused.value
                        pseudoFieldFocused.value = focusState.isFocused

                        if (wasFocused && !focusState.isFocused && pseudo.isNotBlank()) {
                            onPseudoFocusLost()
                        }
                    },
                text = pseudo,
                singleLine = true,
                onValueChanged = onPseudoChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            if (!pseudoFieldFocused.value && pseudo.isNotBlank()) {
                isPseudoAvailable?.let { available ->
                    if (!available) {
                        WarningTextField(
                            warningText = stringResource(Res.string.edit_profile_basic_information_pseudo_not_available),
                        )
                        SpacerHeight16()
                    }
                }
            }

            if (pseudoRequirements.isNotEmpty() && pseudoFieldFocused.value) {
                if (pseudoRequirements.any { !it.isValid }) {
                    ValidationRequirementsList(
                        requirements = pseudoRequirements
                    )

                    SpacerHeight8()
                }
            }

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

@Preview
@Composable
fun EditInformationPreview() {
    ProfileEditInformationForm(
        name = "Test nom",
        pseudo = "Test pseudo",
        onNameChanged = {},
        onPseudoChanged = {},
        onPseudoFocusLost = {},
        isPseudoAvailable = true,
        bio = "Test bio sur une seule ligne ou peut-être plusieurs, qui sait",
        onBioChanged = {}
    )
}
