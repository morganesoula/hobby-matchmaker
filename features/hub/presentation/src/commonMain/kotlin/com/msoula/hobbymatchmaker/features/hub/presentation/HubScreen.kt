package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.models.RecentMatchMember
import com.msoula.hobbymatchmaker.core.design.organisms.HubFavoriteMovies
import com.msoula.hobbymatchmaker.core.design.organisms.HubFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoRecentMatchesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubRecentMatches
import com.msoula.hobbymatchmaker.core.design.organisms.HubRecentMatchesSection
import com.msoula.hobbymatchmaker.core.design.organisms.MemberDetailModalBottomSheet
import com.msoula.hobbymatchmaker.core.design.organisms.MemberDetailNoMoviesModalBottomSheet
import com.msoula.hobbymatchmaker.core.design.templates.HubLayout
import com.msoula.hobbymatchmaker.core.design.util.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HubContent(
    hubFavoriteMovies: UiState<ImmutableList<MovieCarouselItem>>,
    hubRecentMatches: UiState<ImmutableList<ProfileSocialMember>>,
    showRecentMatchDetail: Boolean,
    observeFavoriteMovies: () -> Unit,
    getRecentMatches: () -> Unit,
    onModalDismissed: () -> Unit,
    onMemberClicked: (member: ProfileSocialMember) -> Unit,
    navigateToMoviesScreen: () -> Unit,
    navigateToMovieDetail: (movieId: Long) -> Unit,
    navigateToProfileScreen: () -> Unit,
    selectedMatch: ProfileSocialMember?,
    selectedMatchMoviesState: UiState<ImmutableList<MovieCarouselItem>>
) {
    HubLayout(
        isRecentMatchesEmpty = hubRecentMatches is UiState.Empty,
        hubRecentMatches = {
            HubRecentMatchesSection {
                StateContainer(
                    state = hubRecentMatches,
                    onEmpty = {
                        HubNoRecentMatchesSection(navigateToProfileScreen = navigateToProfileScreen)
                    },
                    onError = { error, hint ->
                        HubSectionError {
                            ErrorStateScreen(
                                error = error,
                                hint = hint,
                                onRetry = { getRecentMatches() }
                            )
                        }
                    },
                    onSuccess = { members ->
                        HubRecentMatches(
                            members = members,
                            onMemberClicked = onMemberClicked
                        )
                    }
                )
            }
        },
        hubFavoriteMovies = {
            HubFavoriteMoviesSection {
                StateContainer(
                    state = hubFavoriteMovies,
                    onEmpty = {
                        HubNoFavoriteMoviesSection(navigateToMoviesScreen = navigateToMoviesScreen)
                    },
                    onError = { error, hint ->
                        HubSectionError {
                            ErrorStateScreen(
                                error = error,
                                hint = hint,
                                onRetry = { observeFavoriteMovies() }
                            )
                        }
                    },
                    onSuccess = { movies ->
                        HubFavoriteMovies(
                            likedMovies = movies,
                            navigateToMovieDetail = navigateToMovieDetail
                        )
                    }
                )
            }
        }
    )

    if (showRecentMatchDetail && (selectedMatchMoviesState is UiState.Success || selectedMatchMoviesState is UiState.Empty)) {
        selectedMatch?.let { match ->
            when (selectedMatchMoviesState) {
                is UiState.Success -> MemberDetailModalBottomSheet(
                    member = RecentMatchMember(
                        uid = match.uid,
                        name = match.name,
                        pseudo = match.pseudo,
                        avatarUrl = match.avatarUrl ?: "",
                        commonMovies = selectedMatchMoviesState.data
                    ),
                    onDismiss = onModalDismissed
                )

                is UiState.Empty -> MemberDetailNoMoviesModalBottomSheet(
                    member = RecentMatchMember(
                        uid = match.uid,
                        name = match.name,
                        pseudo = match.pseudo,
                        avatarUrl = match.avatarUrl ?: "",
                        commonMovies = persistentListOf()
                    ),
                    onDismiss = onModalDismissed
                )
            }
        }
    }
}

// Sections have a bounded height: keep the error message and retry button reachable
@Composable
private fun HubSectionError(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
