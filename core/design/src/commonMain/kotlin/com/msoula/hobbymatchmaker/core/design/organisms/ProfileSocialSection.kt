package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.authentified_no_social_members_description
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.icons.LucideHeart
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMembers
import com.msoula.hobbymatchmaker.core.design.molecules.PseudoSearchBar
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_add_people_form_title
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_add_people_text_button
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileSocialSection(
    socialMembers: List<ProfileSocialMembers>?,
    onSearchPeople: (pseudo: String) -> Unit,
    searchResult: List<ProfileSocialMembers> = emptyList(),
    onInviteToSocialCircle: (pseudo: String, name: String?) -> Unit
) {
    val textFieldState = rememberTextFieldState()
    var displayAddPeopleForm by rememberSaveable { mutableStateOf(false) }
    var pseudoSelectedTmp by remember { mutableStateOf("") }
    var associatedNameTmp by remember { mutableStateOf("") }

    GenericCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(Modifier.padding(CustomSize.Sixteen)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = LucideHeart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSize.TwentyFour)
                    )
                    SpacerWidth4()
                    Text(
                        text = stringResource(Res.string.user_profile_social_circle_main_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                if (!displayAddPeopleForm) {
                    Button(
                        onClick = { displayAddPeopleForm = true },
                        modifier = Modifier.wrapContentWidth(),
                        shape = RoundedCornerShape(CustomSize.Sixteen)
                    ) {
                        Icon(
                            imageVector = MaterialSymbolsPerson_add,
                            contentDescription = stringResource(
                                Res.string.user_profile_social_circle_main_add_people_text_button
                            ),
                            modifier = Modifier.size(IconSize.Sixteen)
                        )

                        SpacerWidth4()

                        Text(
                            text = stringResource(
                                Res.string.user_profile_social_circle_main_add_people_text_button
                            )
                        )
                    }
                }
            }

            if (displayAddPeopleForm) {
                SpacerHeight16()
                GenericCard(
                    Modifier
                        .border(
                            1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(
                                CustomSize.Sixteen
                            )
                        ),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = MaterialSymbolsPerson_add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(IconSize.TwentyFour)
                            )
                            SpacerWidth8()
                            Text(
                                text = stringResource(Res.string.user_profile_social_circle_main_add_people_form_title),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        PseudoSearchBar(
                            textFieldState = textFieldState,
                            onSearch = onSearchPeople,
                            searchResults = searchResult,
                            onPseudoSelected = { pseudo, name ->
                                pseudoSelectedTmp = pseudo
                                associatedNameTmp = name ?: ""
                            }
                        )

                        if (pseudoSelectedTmp.isNotEmpty()) {
                            SpacerHeight8()

                            Button(
                                onClick = {
                                    displayAddPeopleForm = false
                                    onInviteToSocialCircle(pseudoSelectedTmp, associatedNameTmp)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(CustomSize.Sixteen)
                            ) {
                                Text(text = stringResource(Res.string.user_profile_social_circle_main_add_people_text_button))
                            }
                        }

                        SpacerHeight8()

                        Button(
                            onClick = { displayAddPeopleForm = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(CustomSize.Sixteen)
                        ) {
                            Text(text = stringResource(Res.string.cancel))
                        }
                    }
                }
                SpacerHeight16()
            }

            SpacerHeight16()

            if (!socialMembers.isNullOrEmpty()) {
                socialMembers.forEachIndexed { index, member ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = CustomSize.Eight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubcomposeAsyncImage(
                            model = member.avatarUrl,
                            contentDescription = member.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(IconSize.FortyEight)
                                .clip(CircleShape)
                                .border(
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground
                                    ),
                                    shape = CircleShape
                                )
                        )

                        SpacerWidth8()
                        Text(
                            text = member.name ?: member.pseudo,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (index < socialMembers.lastIndex) {
                        HorizontalDivider(
                            Modifier.padding(vertical = CustomSize.Four),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .2f)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.authentified_no_social_members_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileSocialSectionPreview() {
    ProfileSocialSection(
        socialMembers = emptyList(),
        onSearchPeople = {},
        onInviteToSocialCircle = { _, _ -> }
    )
}
