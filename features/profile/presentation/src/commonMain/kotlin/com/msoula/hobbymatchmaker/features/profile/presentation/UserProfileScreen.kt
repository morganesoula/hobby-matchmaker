package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.msoula.hobbymatchmaker.core.design.settings_open_cd
import com.msoula.hobbymatchmaker.core.design.icons.LucideSettings
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ProfileLoadingScreen
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
import com.msoula.hobbymatchmaker.core.design.models.ProfileHeaderUiModel
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.molecules.EditProfileTopBar
import com.msoula.hobbymatchmaker.core.design.molecules.FeatureProfileCard
import com.msoula.hobbymatchmaker.core.design.molecules.FeatureProfileCardWithButton
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_requirements_not_empty
import com.msoula.hobbymatchmaker.core.design.organisms.AuthentifiedProfileHeader
import com.msoula.hobbymatchmaker.core.design.organisms.DiscardChangesDialog
import com.msoula.hobbymatchmaker.core.design.organisms.GenericProfileBackground
import com.msoula.hobbymatchmaker.core.design.organisms.GuestProfileHeader
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditInformationForm
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditInterestsForm
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileEditProfilePicture
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileFavoritesSection
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileInterestsEmptySection
import com.msoula.hobbymatchmaker.core.design.organisms.ProfileInterestsSection
import com.msoula.hobbymatchmaker.core.design.profile_pseudo_copied
import com.msoula.hobbymatchmaker.core.design.profile_share_message
import com.msoula.hobbymatchmaker.core.design.util.rememberShareText
import org.jetbrains.compose.resources.getString
import com.msoula.hobbymatchmaker.core.design.templates.CompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.EditableProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.GuestProfileLayout
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.PlatformBackHandler
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileActions
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileState
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserProfileContent(
    snackBarHostState: SnackbarHostState,
    userProfileState: UserProfileState,
    userProfileActions: UserProfileActions
) {
    val noNumberRequirement =
        stringResource(Res.string.edit_profile_basic_information_requirements_no_number)

    val notEmptyRequirement =
        stringResource(Res.string.edit_profile_basic_information_requirements_not_empty)

    val requirements = remember(userProfileState.editableProfile?.name) {
        val name = userProfileState.editableProfile?.name.orEmpty()
        listOf(
            ValidationRequirement(text = notEmptyRequirement, isValid = name.isNotBlank()),
            ValidationRequirement(text = noNumberRequirement, isValid = name.none { it.isDigit() })
        )
    }

    // Save only when something changed, the name is valid and no save is already running
    val enableSaveTopBarButton = userProfileState.isDirty &&
        userProfileState.editableProfile?.isNameValid == true &&
        !userProfileState.isSaving

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val requestLeaveEdition = {
        when {
            userProfileState.isSaving -> Unit
            userProfileState.isDirty -> showDiscardDialog = true
            else -> userProfileActions.closeEdition()
        }
    }

    // Android system back (and the iOS back gesture) must not silently drop unsaved edits
    PlatformBackHandler(enabled = userProfileState.isEditMode) { requestLeaveEdition() }

    if (showDiscardDialog && userProfileState.isEditMode) {
        DiscardChangesDialog(
            onKeepEditing = { showDiscardDialog = false },
            onDiscard = {
                showDiscardDialog = false
                userProfileActions.closeEdition()
            }
        )
    }

    val shareText = rememberShareText()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val pseudoCopiedMessage = stringResource(Res.string.profile_pseudo_copied)

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
                    onBack = requestLeaveEdition,
                    onSave = { userProfileActions.onEvent(UserProfileUiEventModel.OnSaveClicked) },
                    isSaving = userProfileState.isSaving,
                    enableSave = enableSaveTopBarButton
                )
            } else {
                val showSettings = userProfileState.profileState is UserProfileUiStateModel.Success
                if (isIosPlatform() || showSettings) {
                    BackNavigationTopBar(
                        showBackArrow = isIosPlatform(),
                        actions = {
                            if (showSettings) {
                                IconButton(onClick = { userProfileActions.onNavigate(NavigationDestination.Settings) }) {
                                    Icon(
                                        imageVector = LucideSettings,
                                        contentDescription = stringResource(Res.string.settings_open_cd),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        onBack = { userProfileActions.onNavigate(NavigationDestination.Movies) }
                    )
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
                    onRetry = { userProfileActions.onEvent(UserProfileUiEventModel.OnRetryClicked) }
                )
            }

            is UserProfileUiStateModel.Success -> {
                val profile = userProfileState.profileState.userProfile

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
                                }
                            )
                        }
                    }
                } else {
                    GenericProfileBackground(
                        padding = padding
                    ) {
                        val openEditMode = { userProfileActions.onEvent(UserProfileUiEventModel.OnEditModeClicked) }
                        val isNewProfile = profile.bio.isNullOrBlank() &&
                            profile.favorites.isEmpty() &&
                            profile.interests.isNullOrEmpty()

                        CompleteProfileLayout(
                            headerSection = {
                                AuthentifiedProfileHeader(
                                    user = ProfileHeaderUiModel(
                                        name = profile.name,
                                        pseudo = profile.pseudo,
                                        biography = profile.bio ?: "",
                                        avatarUrl = profile.avatarUrl
                                    ),
                                    showShareHint = isNewProfile,
                                    onChangePhotoClicked = openEditMode,
                                    onCopyPseudoClicked = {
                                        clipboardManager.setText(AnnotatedString("@${profile.pseudo}"))
                                        coroutineScope.launch { snackBarHostState.showSnackbar(pseudoCopiedMessage) }
                                    },
                                    onAddBioClicked = openEditMode,
                                    onEditProfileClicked = openEditMode,
                                    onShareProfileClicked = {
                                        coroutineScope.launch {
                                            shareText(getString(Res.string.profile_share_message, profile.pseudo))
                                        }
                                    }
                                )
                            },
                            favoritesSection = {
                                ProfileFavoritesSection(
                                    favorites = profile.favorites.toImmutableList(),
                                    favoritesCount = profile.moviesLikedCount,
                                    onSeeAllClicked = { userProfileActions.onNavigate(NavigationDestination.Movies) },
                                    onMovieClicked = { movieId ->
                                        userProfileActions.onNavigate(NavigationDestination.MovieDetail(movieId))
                                    },
                                    onDiscoverClicked = { userProfileActions.onNavigate(NavigationDestination.Movies) }
                                )
                            },
                            interestsSection = {
                                val interests = profile.interests.orEmpty()
                                if (interests.isEmpty()) {
                                    ProfileInterestsEmptySection(onAddClicked = openEditMode)
                                } else {
                                    ProfileInterestsSection(
                                        interests = interests.toImmutableList(),
                                        onAddClicked = openEditMode
                                    )
                                }
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
                                onClick = { userProfileActions.onNavigate(NavigationDestination.SignUp) }
                            )
                        }
                    )
                }
            }
        }

    }
}
