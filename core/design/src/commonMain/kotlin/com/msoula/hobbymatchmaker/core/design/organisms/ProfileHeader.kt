package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.MAX_SOCIAL_CIRCLE_SIZE
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.ButtonWithStartIcon
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithDefaultIcon
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.authentified_edit_button_title
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.icons.LucidePencil
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.ProfileHeaderUiModel
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.molecules.PseudoSearchBar
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_add_people_form_title
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_add_people_text_button
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.icons.LucideCopy
import com.msoula.hobbymatchmaker.core.design.profile_share_pseudo

@Composable
fun GuestProfileHeader(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titleHeader: String,
    descriptionHeader: String
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleWithDefaultIcon(
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                icon = icon,
                iconTint = MaterialTheme.colorScheme.onSurface
            )

            SpacerHeight8()

            Text(
                text = titleHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            SpacerHeight4()

            Text(
                text = descriptionHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AuthentifiedProfileHeader(
    modifier: Modifier = Modifier,
    user: ProfileHeaderUiModel,
    onEditProfileClicked: () -> Unit,
    onSearchPeople: (pseudo: String) -> Unit,
    searchResult: ImmutableList<ProfileSocialMember> = persistentListOf(),
    onInviteToSocialCircle: (pseudo: String, name: String?) -> Unit,
    onSharePseudoClicked: () -> Unit = {}
) {

    val textFieldState = rememberTextFieldState()
    var displayAddPeopleForm by rememberSaveable { mutableStateOf(false) }
    var pseudoSelectedTmp by remember { mutableStateOf("") }
    var associatedNameTmp by remember { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleWithCustomPhoto(
                customAvatarPath = user.avatarUrl,
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f)
            )
            SpacerHeight8()

            if (user.name.isNotBlank()) {
                Text(
                    text = user.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            if (user.pseudo.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${user.pseudo}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { onSharePseudoClicked() }) {
                        Icon(
                            imageVector = LucideCopy,
                            contentDescription = stringResource(Res.string.profile_share_pseudo),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(IconSize.Sixteen)
                        )
                    }
                }
            }

            if (user.name.isNotBlank() || user.pseudo.isNotBlank()) SpacerHeight16()

            if (user.biography.isNotBlank()) {
                Text(
                    text = user.biography,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                SpacerHeight16()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ButtonWithStartIcon(
                    icon = LucidePencil,
                    textButton = stringResource(Res.string.authentified_edit_button_title),
                    onButtonClick = onEditProfileClicked,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )

                if (!displayAddPeopleForm && (user.circleSize) < MAX_SOCIAL_CIRCLE_SIZE) {
                    SpacerHeight8()

                    ButtonWithStartIcon(
                        icon = MaterialSymbolsPerson_add,
                        textButton = stringResource(
                            Res.string.user_profile_social_circle_main_add_people_text_button
                        ),
                        onButtonClick = { displayAddPeopleForm = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
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
            }
        }
    }
}
