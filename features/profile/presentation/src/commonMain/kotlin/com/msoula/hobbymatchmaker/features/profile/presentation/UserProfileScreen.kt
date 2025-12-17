package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ProfileLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.SecondaryButton
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_requirements_no_number
import com.msoula.hobbymatchmaker.core.design.guest_build_circle_feature_description
import com.msoula.hobbymatchmaker.core.design.guest_build_circle_feature_title
import com.msoula.hobbymatchmaker.core.design.guest_discover_feature_description
import com.msoula.hobbymatchmaker.core.design.guest_discover_feature_title
import com.msoula.hobbymatchmaker.core.design.guest_header_description
import com.msoula.hobbymatchmaker.core.design.guest_header_title
import com.msoula.hobbymatchmaker.core.design.guest_redirect_description
import com.msoula.hobbymatchmaker.core.design.guest_redirect_sign_up_button_text
import com.msoula.hobbymatchmaker.core.design.guest_redirect_title
import com.msoula.hobbymatchmaker.core.design.guest_share_interests_feature_description
import com.msoula.hobbymatchmaker.core.design.guest_share_interests_feature_title
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.icons.HeroiconsSparkles
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.log_out
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMembers
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.molecules.EditProfileTopBar
import com.msoula.hobbymatchmaker.core.design.molecules.FeatureProfileCard
import com.msoula.hobbymatchmaker.core.design.molecules.FeatureProfileCardWithButton
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.organisms.AuthentifiedProfileHeader
import com.msoula.hobbymatchmaker.core.design.organisms.GenericProfileBackground
import com.msoula.hobbymatchmaker.core.design.organisms.GuestProfileHeader
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditInformationForm
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditInterestsForm
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditProfilePicture
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileInterestsSection
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileSocialSection
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileStatsSection
import com.msoula.hobbymatchmaker.core.design.templates.CompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.EditableProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.GuestProfileLayout
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toProfileSocialMembers
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileActions
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileState
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserProfileContent(
    profile: UserProfileUiModel,
    snackBarHostState: SnackbarHostState,
    searchedPseudos: ImmutableList<SocialUserSummaryUiModel>,
    userProfileState: UserProfileState,
    userProfileActions: UserProfileActions
) {
    val noNumberRequirement =
        stringResource(Res.string.edit_profile_basic_information_requirements_no_number)

    val requirements = remember(userProfileState.editableProfile) {
        listOf(
            ValidationRequirement(
                text = noNumberRequirement,
                isValid = userProfileState.editableProfile?.name?.all { !it.isDigit() } == true
            )
        )
    }

    val enableSaveTopBarButton by remember(userProfileState.editableProfile) {
        derivedStateOf {
            userProfileState.editableProfile?.name?.all { !it.isDigit() } == true
        }
    }

    val avatarLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single
    ) { image ->
        image?.let {
            userProfileActions.onEvent(UserProfileUiEventModel.OnAvatarSelected(it.path))
        } ?: Logger.d("No image found in the gallery")
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        },
        topBar = {
            if (userProfileState.isEditMode) {
                EditProfileTopBar(
                    isIOS = isIosPlatform(),
                    onBack = { userProfileActions.closeEdition() },
                    onSave = { userProfileActions.onEvent(UserProfileUiEventModel.OnSaveClicked) },
                    enableSave = enableSaveTopBarButton
                )
            } else {
                if (isIosPlatform()) {
                    BackNavigationTopBar(onBack = { userProfileActions.onNavigate("movies") })
                }
            }
        },
    ) { padding ->
        when (userProfileState.profileState) {
            is UserProfileUiStateModel.Loading -> ProfileLoadingScreen()

            is UserProfileUiStateModel.Error -> {
                ErrorStateScreen(
                    error = userProfileState.profileState.errorMessage,
                    hint = UIErrorHint(retry = RetryPolicy.Manual),
                    onRetry = {}
                )
            }

            is UserProfileUiStateModel.Success -> {
                if (userProfileState.isEditMode) {
                    userProfileState.editableProfile?.let { currentEditableProfile ->
                        GenericProfileBackground(
                            padding = padding
                        ) {
                            EditableProfileLayout(
                                editPhotoProfileSection = {
                                    ProfileEditProfilePicture(
                                        customAvatarPath = currentEditableProfile.avatarUrl,
                                        onAvatarClicked = { avatarLauncher.launch() }
                                    )
                                },
                                editBasicInformationSection = {
                                    ProfileEditInformationForm(
                                        name = currentEditableProfile.name,
                                        onNameChanged = { name ->
                                            userProfileActions.onEvent(
                                                UserProfileUiEventModel.OnNameChanged(
                                                    name
                                                )
                                            )
                                        },
                                        pseudo = currentEditableProfile.pseudo,
                                        onPseudoChanged = { pseudo ->
                                            userProfileActions.onEvent(
                                                UserProfileUiEventModel.OnPseudoChanged(
                                                    pseudo
                                                )
                                            )
                                        },
                                        onPseudoFocusLost = {
                                            userProfileActions.onEvent(UserProfileUiEventModel.OnPseudoDefined)
                                        },
                                        isPseudoAvailable = userProfileState.isPseudoAvailable,
                                        bio = currentEditableProfile.bio,
                                        onBioChanged = { bio ->
                                            userProfileActions.onEvent(
                                                UserProfileUiEventModel.OnBioChanged(
                                                    bio
                                                )
                                            )
                                        },
                                        nameRequirements = requirements.toImmutableList()
                                    )
                                },
                                editInterestsSection = {
                                    ProfileEditInterestsForm(
                                        interests = currentEditableProfile.interests?.toImmutableList()
                                            ?: persistentListOf(),
                                        onInterestChanged = {
                                            userProfileActions.onEvent(
                                                UserProfileUiEventModel.OnInterestsChanged(it)
                                            )
                                        }
                                    )
                                },
                                logOut = {
                                    SecondaryButton(
                                        text = stringResource(Res.string.log_out),
                                        onClick = {
                                            userProfileActions.logOut("sign_in")
                                        }
                                    )
                                }
                            )
                        }
                    }
                } else {
                    GenericProfileBackground(
                        padding = padding
                    ) {
                        CompleteProfileLayout(
                            headerSection = {
                                AuthentifiedProfileHeader(
                                    fullName = profile.name,
                                    biography = profile.bio ?: "",
                                    avatarPath = profile.avatarUrl,
                                    onEditProfileClicked = {
                                        userProfileActions.onEvent(UserProfileUiEventModel.OnEditModeClicked)
                                    }
                                )
                            },
                            statsSection = {
                                ProfileStatsSection(
                                    moviesLikedCount = profile.moviesLikedCount,
                                    socialMembersCount = profile.socialMembersCount
                                )
                            },
                            interestsSection = {
                                ProfileInterestsSection(
                                    interests = profile.interests?.toImmutableList()
                                )
                            },
                            socialSection = {
                                ProfileSocialSection(
                                    socialMembers = profile.socialMembers.map { it.toProfileSocialMembers() }
                                        .toImmutableList(),
                                    onSearchPeople = {
                                        userProfileActions.onSocialEvent(
                                            SocialUiEventModel.OnSearchPeople(
                                                it
                                            )
                                        )
                                    },
                                    searchResult = searchedPseudos.map { member ->
                                        ProfileSocialMembers(
                                            uid = member.uid,
                                            name = member.name,
                                            pseudo = member.pseudo,
                                            avatarUrl = member.avatarUrl
                                        )
                                    }.toImmutableList(),
                                    onInviteToSocialCircle = { pseudo, name ->
                                        userProfileActions.onSocialEvent(
                                            SocialUiEventModel.OnInviteToSocialCircle(pseudo, name)
                                        )
                                    }
                                )
                            },
                            logOut = {
                                SecondaryButton(
                                    text = stringResource(Res.string.log_out),
                                    onClick = { userProfileActions.logOut("sign_in") }
                                )
                            }
                        )
                    }
                }
            }

            is UserProfileUiStateModel.Guest -> {
                GenericProfileBackground(
                    padding = padding
                ) {
                    GuestProfileLayout(
                        guestHeader = {
                            GuestProfileHeader(
                                icon = LucideFilm,
                                titleHeader = stringResource(Res.string.guest_header_title),
                                descriptionHeader = stringResource(Res.string.guest_header_description),
                            )
                        },
                        guestDiscoverFeature = {
                            FeatureProfileCard(
                                icon = LucideFilm,
                                titleFeature = stringResource(Res.string.guest_discover_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_discover_feature_description)
                            )
                        },
                        guestBuildCircleFeature = {
                            FeatureProfileCard(
                                icon = BootstrapPerson,
                                titleFeature = stringResource(Res.string.guest_build_circle_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_build_circle_feature_description)
                            )
                        },
                        guestSharedInterestsFeature = {
                            FeatureProfileCard(
                                icon = HeroiconsSparkles,
                                titleFeature = stringResource(Res.string.guest_share_interests_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_share_interests_feature_description)
                            )
                        },
                        guestRedirectFeature = {
                            FeatureProfileCardWithButton(
                                titleFeature = stringResource(Res.string.guest_redirect_title),
                                descriptionFeature = stringResource(Res.string.guest_redirect_description),
                                buttonText = stringResource(Res.string.guest_redirect_sign_up_button_text),
                                onClick = { userProfileActions.onNavigate("sign_up") }
                            )
                        }
                    )
                }
            }
        }
    }
}
