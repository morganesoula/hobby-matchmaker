package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.FormIcon
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_hint
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_title
import com.msoula.hobbymatchmaker.core.design.icons.Add
import com.msoula.hobbymatchmaker.core.design.icons.Sparkle
import com.msoula.hobbymatchmaker.core.design.molecules.InterestDeletableBlock
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditInterestsForm(
    modifier: Modifier = Modifier,
    interests: List<String>,
    onInterestChanged: (List<String>) -> Unit
) {
    var currentInterestInput by remember { mutableStateOf("") }

    GenericCard(
        modifier = modifier.padding(start = CustomSize.Eight, end = CustomSize.Eight),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row {
                FormIcon(
                    icon = Sparkle,
                    size = IconSize.TwentyFour,
                    tint = MaterialTheme.colorScheme.primary
                )
                SpacerWidth4()
                Text(
                    text = stringResource(Res.string.user_profile_interests_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            SpacerHeight16()
            InterestDeletableBlock(interests = interests, onDeleteClicked = { tagToDelete ->
                val updatedInterests = interests.filter { it != tagToDelete }
                onInterestChanged(updatedInterests)
            })
            SpacerHeight16()
            Text(
                text = stringResource(Res.string.edit_profile_add_interests_title),
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentInterestInput,
                    onValueChange = { currentInterestInput = it },
                    label = { Text(text = stringResource(Res.string.edit_profile_add_interests_hint)) },
                    singleLine = true,
                    modifier = modifier
                        .weight(1f)
                        .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
                    colors = HMMTextFieldColors()
                )

                IconButton(
                    onClick = {
                        if (currentInterestInput.isNotBlank()) {
                            onInterestChanged(interests + currentInterestInput.trim())
                            currentInterestInput = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(CustomSize.Eight)
                ) {
                    Icon(
                        imageVector = Add,
                        contentDescription = null,
                        modifier = Modifier.padding(CustomSize.Four)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EditInterestsFormPreview() {
    ProfileEditInterestsForm(
        interests = listOf("Chat", "Lapin", "Chien"),
        onInterestChanged = { list -> }
    )
}
