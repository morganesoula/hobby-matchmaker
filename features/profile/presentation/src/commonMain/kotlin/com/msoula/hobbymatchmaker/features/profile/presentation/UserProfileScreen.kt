package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.asString
import com.msoula.hobbymatchmaker.core.design.component.ShimmerCard
import com.msoula.hobbymatchmaker.core.design.component.ShimmerCircle
import com.msoula.hobbymatchmaker.core.design.component.ShimmerRectangle
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
    state: UserProfileUiStateModel,
    onEvent: (UserProfileUiEventModel) -> Unit,
    navigateToSignUpScreen: () -> Unit
) {
    when (state) {
        is UserProfileUiStateModel.Success ->
            UserProfileScreenContent(
                mode = ProfileMode.View,
                //userProfile = fakeUserProfile(),
                userProfile = state.userProfile,
                onEvent = onEvent
            )

        is UserProfileUiStateModel.Error ->
            UserProfileErrorScreen(error = state.errorMessage.asString())

        is UserProfileUiStateModel.Loading ->
            UserProfileLoadingScreen()

        is UserProfileUiStateModel.Guest ->
            UserProfileGuestScreen(navigateToSignUpScreen = navigateToSignUpScreen)

        is UserProfileUiStateModel.Incomplete ->
            UserProfileScreenContent(
                mode = ProfileMode.View,
                userProfile = null,
                onEvent = onEvent
            )
    }
}

@Composable
fun UserProfileScreenContent(
    modifier: Modifier = Modifier,
    mode: ProfileMode,
    userProfile: UserProfileUiModel?,
    onEvent: (UserProfileUiEventModel) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = .85f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(bottom = 24.dp)
        ) {
            ProfileHeaderSection(mode, userProfile, onEvent)
            Spacer(Modifier.height(12.dp))
            ProfileStatsSection(mode = mode, user = userProfile)
            Spacer(Modifier.height(16.dp))
            ProfileInterestsSection(mode, userProfile)
            Spacer(Modifier.height(16.dp))
            ProfileSocialSection(mode, userProfile)
        }
    }
}

@Composable
fun UserProfileGuestScreen(
    modifier: Modifier = Modifier,
    navigateToSignUpScreen: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PersonOutline,
            contentDescription = null,
            modifier = modifier.size(96.dp),
            tint = Color.Gray
        )
        Spacer(modifier.height(16.dp))
        Text(
            "You're browsing as a guest!",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier.height(8.dp))
        Text(
            "Create an account to build your profile and connect with others.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier.height(24.dp))
        Button(onClick = { navigateToSignUpScreen() }) {
            Text("Create My Profile")
        }
    }
}

@Composable
fun UserProfileLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ShimmerCircle(modifier = modifier.size(96.dp).align(Alignment.CenterHorizontally))
        Spacer(modifier.height(12.dp))
        ShimmerRectangle(
            widthFraction = 0.5f,
            height = 20.dp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier.height(24.dp))
        ShimmerCard(height = 120.dp)
    }
}

@Composable
fun UserProfileErrorScreen(
    modifier: Modifier = Modifier,
    error: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFFF9800),
            modifier = modifier.size(72.dp)
        )
        Spacer(modifier.height(16.dp))
        Text(error, textAlign = TextAlign.Center)
        Spacer(modifier.height(24.dp))
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
