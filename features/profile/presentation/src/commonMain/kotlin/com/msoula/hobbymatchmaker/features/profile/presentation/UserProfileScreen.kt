package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.asString
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.movies_liked_count_title
import com.msoula.hobbymatchmaker.core.design.social_members_count_title
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    state: UserProfileUiStateModel
) {
    when (state) {
        is UserProfileUiStateModel.Success -> {
            UserProfileScreenContent(
                userProfile = state.userProfile
            )
        }

        is UserProfileUiStateModel.Error -> {
            UserProfileErrorScreen(error = state.errorMessage.asString())
        }

        is UserProfileUiStateModel.Loading -> {
            LoadingCircularProgress()
        }
    }

}

@Composable
fun UserProfileScreenContent(
    modifier: Modifier = Modifier,
    userProfile: UserProfileUiModel
) {
    Scaffold { padding ->
        Box(
            modifier = modifier.fillMaxSize()
                .padding(
                    top = if (isIosPlatform()) padding.calculateTopPadding() - 8.dp else padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                enabled = false,
                onClick = {}
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.background.copy(
                                alpha = 0.6f
                            )
                        )
                    ) {
                        Row {
                            Icon(
                                Icons.Default.Movie,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(Res.string.movies_liked_count_title),
                            )

                            Column {
                                Text(
                                    text = "${userProfile.moviesLikedCount}",
                                    color = Color.Black
                                )
                                Text(
                                    text = stringResource(Res.string.movies_liked_count_title),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                        }
                    }

                    Box(
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.background.copy(
                                alpha = 0.6f
                            )
                        )
                    ) {
                        Row {
                            Icon(
                                Icons.Default.People,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = stringResource(Res.string.social_members_count_title),
                            )

                            Column {
                                Text(
                                    text = "${userProfile.socialMembersCount}/5",
                                    color = Color.Black
                                )
                                Text(
                                    text = stringResource(Res.string.social_members_count_title),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileErrorScreen(
    modifier: Modifier = Modifier,
    error: String
) {
    Text(modifier = modifier, text = error)
}
