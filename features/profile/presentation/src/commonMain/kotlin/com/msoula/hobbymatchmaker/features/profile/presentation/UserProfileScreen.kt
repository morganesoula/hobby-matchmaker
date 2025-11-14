package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ProfileLoadingScreen
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.templates.CompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.GuestProfileLayout
import com.msoula.hobbymatchmaker.core.design.templates.IncompleteProfileLayout
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.features.profile.presentation.components.EditBottomBar
import com.msoula.hobbymatchmaker.features.profile.presentation.components.ProfileHeaderSection
import com.msoula.hobbymatchmaker.features.profile.presentation.components.ProfileInterestsSection
import com.msoula.hobbymatchmaker.features.profile.presentation.components.ProfileSocialSection
import com.msoula.hobbymatchmaker.features.profile.presentation.components.ProfileStatsSection
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel,
    onNavigate: (String) -> Unit
) {
    val profileState by viewModel.screenState.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val editableProfile by viewModel.editableProfile.collectAsState()

    Scaffold(
        topBar = {
            if (isIosPlatform()) {
                BackNavigationTopBar(onBack = { onNavigate("movies") })
            }
        },
        bottomBar = {
            if (isEditMode && profileState is UserProfileUiStateModel.Success) {
                EditBottomBar(
                    canSave = true,
                    onSave = {
                        viewModel.onEvent(UserProfileUiEventModel.OnSaveClicked)
                    },
                    onSkip = {
                        viewModel.onEvent(UserProfileUiEventModel.OnSkipClicked)
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isEditMode && profileState is UserProfileUiStateModel.Success) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(UserProfileUiEventModel.OnEditModeToggled)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile"
                    )
                }
            }
        }
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
                val displayProfile = if (isEditMode) {
                    editableProfile ?: (profileState as UserProfileUiStateModel.Success).userProfile
                } else {
                    (profileState as UserProfileUiStateModel.Success).userProfile
                }

                val mode = if (isEditMode) ProfileMode.Edit else ProfileMode.View

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = .85f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding() + CustomSize.TwentyFour
                        )
                ) {
                    CompleteProfileLayout(
                        headerSection = {
                            ProfileHeaderSection(
                                mode,
                                displayProfile,
                                onAvatarClicked = {
                                    viewModel.onEvent(
                                        UserProfileUiEventModel.OnPickAvatarClicked
                                    )
                                },
                                onBioChanged = { bio ->
                                    viewModel.onEvent(
                                        UserProfileUiEventModel.OnBioChanged(bio)
                                    )
                                }
                            )
                        },
                        statsSection = {
                            ProfileStatsSection(mode, displayProfile)
                        },
                        interestsSection = {
                            ProfileInterestsSection(
                                mode = mode,
                                user = displayProfile
                            )
                        },
                        socialSection = {
                            ProfileSocialSection(
                                mode = mode,
                                user = displayProfile
                            )
                        }
                    )
                }
            }

            is UserProfileUiStateModel.Guest ->
                GuestProfileLayout()

            is UserProfileUiStateModel.Incomplete ->
                IncompleteProfileLayout()
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
