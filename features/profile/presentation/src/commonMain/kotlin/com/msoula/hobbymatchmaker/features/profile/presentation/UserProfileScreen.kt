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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ProfileLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
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
import com.msoula.hobbymatchmaker.core.design.templates.GuestProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.IncompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toProfileSocialMembers
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel,
    onNavigate: (String) -> Unit
) {
    val profileState by viewModel.screenState.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                is UiEvent.NavigateToRoute -> onNavigate(event.route)
                is UiEvent.OnDataReady -> {
                    when (event.data) {
                        "profile_updated" -> viewModel.closeEdition()
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
                    onBack = { viewModel.closeEdition() },
                    onSave = { viewModel.onEvent(UserProfileUiEventModel.OnSaveClicked) }
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
                    GenericProfileBackground(
                        padding = padding
                    ) {
                        val profile = (profileState as UserProfileUiStateModel.Success).userProfile

                        IncompleteProfileLayout(
                            editPhotoProfileSection = {
                                ProfileEditProfilePicture()
                            },
                            editBasicInformationSection = {
                                ProfileEditInformationForm(
                                    name = profile.name,
                                    onNameChanged = { name ->
                                        viewModel.onEvent(
                                            UserProfileUiEventModel.OnNameChanged(
                                                name
                                            )
                                        )
                                    },
                                    bio = profile.bio,
                                    onBioChanged = { bio ->
                                        viewModel.onEvent(
                                            UserProfileUiEventModel.OnBioChanged(
                                                bio
                                            )
                                        )
                                    }
                                )
                            },
                            editInterestsSection = {
                                ProfileEditInterestsForm(
                                    interests = profile.interests ?: emptyList(),
                                    onInterestChanged = {
                                        viewModel.onEvent(
                                            UserProfileUiEventModel.OnInterestsChanged(it)
                                        )
                                    }
                                )
                            }
                        )
                    }
                } else {
                    GenericProfileBackground(
                        padding = padding
                    ) {
                        val profile = (profileState as UserProfileUiStateModel.Success).userProfile

                        CompleteProfileLayout(
                            headerSection = {
                                AuthentifiedProfileHeader(
                                    fullName = "Test name",
                                    biography = profile.bio ?: "",
                                    onEditProfileClicked = {
                                        viewModel.onEvent(UserProfileUiEventModel.OnEditModeClicked)
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
                                    socialMembers = profile.socialMembers.map { it.toProfileSocialMembers() }
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
                                descriptionHeader = stringResource(Res.string.guest_header_description)
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

private fun fakeUserProfile() =
    UserProfileUiModel(
        name = "Morgane",
        avatarUrl = null,
        bio = "Test bio pour des activités :)",
        interests = listOf("Cats", "Gaming", "Book"),
        moviesLikedCount = 5,
        socialMembersCount = 3,
        socialMembers = listOf(
            SocialMemberUiModel(
                uid = "1234",
                name = "Maxime",
                avatarUrl = ""
            ),
            SocialMemberUiModel(
                uid = "12345",
                name = "Soizic",
                avatarUrl = ""
            ),
            SocialMemberUiModel(
                uid = "123456",
                name = "KitKat",
                avatarUrl = ""
            )
        ),
    )
