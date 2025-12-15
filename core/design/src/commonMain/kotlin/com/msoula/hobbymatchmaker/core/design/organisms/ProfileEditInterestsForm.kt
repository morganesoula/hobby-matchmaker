package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_description
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_hint
import com.msoula.hobbymatchmaker.core.design.edit_profile_add_interests_title
import com.msoula.hobbymatchmaker.core.design.icons.HeroiconsSparkles
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsAdd_2
import com.msoula.hobbymatchmaker.core.design.icons.VscodeCodiconsLightbulb
import com.msoula.hobbymatchmaker.core.design.molecules.InterestDeletableBlock
import com.msoula.hobbymatchmaker.core.design.molecules.TipTextField
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditInterestsForm(
    modifier: Modifier = Modifier,
    interests: ImmutableList<String>,
    onInterestChanged: (List<String>) -> Unit
) {
    var currentInterestInput by remember { mutableStateOf("") }

    fun validateAndAddInterests(input: String): List<String> {
        val tags = input.split(",")
            .map { it.trim() }
            .filter { tag ->
                tag.isNotBlank() &&
                    tag.any { it.isLetter() } &&
                    !interests.contains(tag.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
            }
            .distinct()
            .take(5 - interests.size)
            .map { text ->
                text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }

        return tags
    }

    GenericCard(
        modifier = modifier.padding(start = CustomSize.Eight, end = CustomSize.Eight),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row {
                FormIcon(
                    icon = HeroiconsSparkles,
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
            SpacerHeight8()

            TipTextField(
                hintText = stringResource(Res.string.edit_profile_add_interests_description),
                icon = VscodeCodiconsLightbulb
            )

            SpacerHeight8()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentInterestInput,
                    onValueChange = { currentInterestInput = it },
                    label = { Text(text = stringResource(Res.string.edit_profile_add_interests_hint)) },
                    singleLine = true,
                    colors = HMMTextFieldColors()
                )

                IconButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        val validTags = validateAndAddInterests(currentInterestInput)
                        if (validTags.isNotEmpty()) {
                            onInterestChanged(interests + validTags)
                            currentInterestInput = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .2f)
                    ),
                    shape = RoundedCornerShape(CustomSize.Eight),
                    enabled = interests.size < 5 && currentInterestInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = MaterialSymbolsAdd_2,
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
        interests = listOf("Chat", "Lapin", "Chien").toImmutableList(),
        onInterestChanged = { list -> }
    )
}
