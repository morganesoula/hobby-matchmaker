package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_bio_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_name_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_title
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditInformationForm(
    modifier: Modifier = Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    bio: String? = null
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
                contentDescription = stringResource(Res.string.edit_profile_basic_information_name_title),
                singleLine = true,
                onValueChanged = { onNameChanged(it) }
            )
            SpacerHeight8()
            Text(
                text = stringResource(Res.string.edit_profile_basic_information_bio_title),
                color = MaterialTheme.colorScheme.onSurface
            )
            PrimaryTextField(
                modifier = Modifier.fillMaxWidth(),
                text = bio ?: "",
                contentDescription = stringResource(Res.string.edit_profile_basic_information_bio_title),
                singleLine = false,
                onValueChanged = { onBioChanged(it) }
            )
        }
    }
}

@Preview
@Composable
fun EditInformationPreview() {
    ProfileEditInformationForm(
        name = "Test nom",
        onNameChanged = {},
        bio = "Test bio sur une seule ligne ou peut-être plusieurs, qui sait",
        onBioChanged = {}
    )
}
