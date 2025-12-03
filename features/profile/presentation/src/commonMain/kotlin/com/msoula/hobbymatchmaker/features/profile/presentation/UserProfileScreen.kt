package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ProfileLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
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
import com.msoula.hobbymatchmaker.core.design.icons.Film
import com.msoula.hobbymatchmaker.core.design.icons.Person
import com.msoula.hobbymatchmaker.core.design.icons.Sparkle
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
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileSocialMembers
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileSocialSection
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileStatsSection
import com.msoula.hobbymatchmaker.core.design.templates.CompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.EditableProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.GuestProfileLayout
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toProfileSocialMembers
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.social.presentation.SocialViewModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    profileViewModel: UserProfileViewModel,
    socialViewModel: SocialViewModel,
    onNavigate: (String) -> Unit
) {
    val profileState by profileViewModel.screenState.collectAsState()
    val isEditMode by profileViewModel.isEditMode.collectAsState()
    val editableProfile by profileViewModel.editableProfile.collectAsState()
    val isPseudoAvailable by profileViewModel.isPseudoAvailable.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val noNumberRequirement =
        stringResource(Res.string.edit_profile_basic_information_requirements_no_number)

    val requirements = remember(editableProfile) {
        derivedStateOf {
            listOf(
                ValidationRequirement(
                    text = noNumberRequirement,
                    isValid = editableProfile?.name?.all { !it.isDigit() } == true
                )
            )
        }
    }

    val enableSaveTopBarButton by remember(requirements.value) {
        derivedStateOf {
            requirements.value.all { it.isValid }
        }
    }

    val avatarLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single
    ) { image ->
        image?.let {
            profileViewModel.onEvent(UserProfileUiEventModel.OnAvatarSelected(it.path))
        } ?: Logger.d("No image found in the gallery")
    }

    LaunchedEffect(profileViewModel.events) {
        profileViewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                is UiEvent.NavigateToRoute -> onNavigate(event.route)
                is UiEvent.OnDataReady -> {
                    when (event.data) {
                        "profile_updated" -> profileViewModel.closeEdition()
                    }
                }

                else -> Unit
            }
        }
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
            if (isEditMode) {
                EditProfileTopBar(
                    isIOS = isIosPlatform(),
                    onBack = { profileViewModel.closeEdition() },
                    onSave = { profileViewModel.onEvent(UserProfileUiEventModel.OnSaveClicked) },
                    enableSave = enableSaveTopBarButton
                )
            } else {
                if (isIosPlatform()) {
                    BackNavigationTopBar(onBack = { onNavigate("movies") })
                }
            }
        },
    ) { padding ->
        when (profileState) {
            is UserProfileUiStateModel.Loading -> ProfileLoadingScreen()

            is UserProfileUiStateModel.Error -> {
                val error = (profileState as UserProfileUiStateModel.Error)
                ErrorStateScreen(
                    error = error.errorMessage,
                    hint = UIErrorHint(retry = RetryPolicy.Manual),
                    onRetry = {}
                )
            }

            is UserProfileUiStateModel.Success -> {
                if (isEditMode) {
                    editableProfile?.let { currentEditableProfile ->
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
                                            profileViewModel.onEvent(
                                                UserProfileUiEventModel.OnNameChanged(
                                                    name
                                                )
                                            )
                                        },
                                        pseudo = currentEditableProfile.pseudo,
                                        onPseudoChanged = { pseudo ->
                                            profileViewModel.onEvent(
                                                UserProfileUiEventModel.OnPseudoChanged(
                                                    pseudo
                                                )
                                            )
                                        },
                                        onPseudoFocusLost = {
                                            profileViewModel.onEvent(UserProfileUiEventModel.OnPseudoDefined)
                                        },
                                        isPseudoAvailable = isPseudoAvailable,
                                        bio = currentEditableProfile.bio,
                                        onBioChanged = { bio ->
                                            profileViewModel.onEvent(
                                                UserProfileUiEventModel.OnBioChanged(
                                                    bio
                                                )
                                            )
                                        },
                                        nameRequirements = requirements.value
                                    )
                                },
                                editInterestsSection = {
                                    ProfileEditInterestsForm(
                                        interests = currentEditableProfile.interests ?: emptyList(),
                                        onInterestChanged = {
                                            profileViewModel.onEvent(
                                                UserProfileUiEventModel.OnInterestsChanged(it)
                                            )
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
                        val profile = (profileState as UserProfileUiStateModel.Success).userProfile
                        val searchedPseudos by socialViewModel.searchResults.collectAsState()

                        CompleteProfileLayout(
                            headerSection = {
                                AuthentifiedProfileHeader(
                                    fullName = profile.name,
                                    biography = profile.bio ?: "",
                                    avatarPath = profile.avatarUrl,
                                    onEditProfileClicked = {
                                        profileViewModel.onEvent(UserProfileUiEventModel.OnEditModeClicked)
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
                                    interests = profile.interests
                                )
                            },
                            socialSection = {
                                ProfileSocialSection(
                                    socialMembers = profile.socialMembers.map { it.toProfileSocialMembers() },
                                    onSearchPeople = {
                                        socialViewModel.onEvent(SocialUiEventModel.OnSearchPeople(it))
                                    },
                                    searchResult = searchedPseudos.map { member ->
                                        ProfileSocialMembers(
                                            uid = member.uid,
                                            name = member.name,
                                            pseudo = member.pseudo,
                                            avatarUrl = member.avatarUrl
                                        )
                                    },
                                    onInviteToSocialCircle = {
                                        socialViewModel.onEvent(
                                            SocialUiEventModel.OnInviteToSocialCircle(it)
                                        )
                                    }
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
                                icon = Film,
                                titleHeader = stringResource(Res.string.guest_header_title),
                                descriptionHeader = stringResource(Res.string.guest_header_description),
                            )
                        },
                        guestDiscoverFeature = {
                            FeatureProfileCard(
                                icon = Film,
                                titleFeature = stringResource(Res.string.guest_discover_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_discover_feature_description)
                            )
                        },
                        guestBuildCircleFeature = {
                            FeatureProfileCard(
                                icon = Person,
                                titleFeature = stringResource(Res.string.guest_build_circle_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_build_circle_feature_description)
                            )
                        },
                        guestSharedInterestsFeature = {
                            FeatureProfileCard(
                                icon = Sparkle,
                                titleFeature = stringResource(Res.string.guest_share_interests_feature_title),
                                descriptionFeature = stringResource(Res.string.guest_share_interests_feature_description)
                            )
                        },
                        guestRedirectFeature = {
                            FeatureProfileCardWithButton(
                                titleFeature = stringResource(Res.string.guest_redirect_title),
                                descriptionFeature = stringResource(Res.string.guest_redirect_description),
                                buttonText = stringResource(Res.string.guest_redirect_sign_up_button_text),
                                onClick = { onNavigate("sign_up") }
                            )
                        }
                    )
                }
            }
        }
    }
}
