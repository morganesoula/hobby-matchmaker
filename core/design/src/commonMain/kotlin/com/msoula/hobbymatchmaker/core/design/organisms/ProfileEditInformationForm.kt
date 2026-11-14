package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_bio_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_name_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_optional
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirementsList
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private const val NAME_MAX_LENGTH = 40
private const val BIO_MAX_LENGTH = 120

@Composable
fun ProfileEditInformationForm(
    modifier: Modifier = Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    bio: String? = null,
    nameRequirements: ImmutableList<ValidationRequirement> = persistentListOf(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        EditSectionLabel(text = stringResource(Res.string.edit_profile_basic_information_name_title))
        PrimaryTextField(
            modifier = Modifier.fillMaxWidth(),
            text = name,
            singleLine = true,
            onValueChanged = onNameChanged,
            maxChar = NAME_MAX_LENGTH,
            leadingIcon = {
                Icon(
                    imageVector = BootstrapPerson,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.Sixteen)
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        if (nameRequirements.any { !it.isValid }) {
            ValidationRequirementsList(requirements = nameRequirements)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = CustomSize.Eight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditSectionLabel(text = stringResource(Res.string.edit_profile_basic_information_bio_title))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Text(
                    text = stringResource(Res.string.edit_profile_optional),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = CustomSize.Eight, vertical = CustomSize.Four)
                )
            }
        }
        PrimaryTextField(
            modifier = Modifier.fillMaxWidth(),
            text = bio ?: "",
            singleLine = false,
            minLines = 3,
            maxChar = BIO_MAX_LENGTH,
            onValueChanged = onBioChanged,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            showSupportingText = true
        )
    }
}

@Composable
internal fun EditSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
    )
}
