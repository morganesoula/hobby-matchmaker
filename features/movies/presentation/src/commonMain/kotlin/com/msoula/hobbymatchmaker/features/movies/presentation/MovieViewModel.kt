package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.connection_issue
import com.msoula.hobbymatchmaker.core.design.models.MatchAnimationData
import com.msoula.hobbymatchmaker.core.design.models.MatchingMemberInfo
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.PaginationStateModel
import com.msoula.hobbymatchmaker.features.movies.presentation.orchestrators.MovieCatalogOrchestrator
import com.msoula.hobbymatchmaker.features.movies.presentation.orchestrators.MovieUserActionOrchestrator
import com.msoula.hobbymatchmaker.features.social.domain.models.MovieMatchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel(
    private val userActionOrchestrator: MovieUserActionOrchestrator,
    private val catalogOrchestrator: MovieCatalogOrchestrator,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private var moviesJob: Job? = null

    private val language = getDeviceLocale()

    val movieScreenState: StateFlow<UiState<ImmutableList<MovieUiModel>>>
        field = MutableStateFlow<UiState<ImmutableList<MovieUiModel>>>(UiState.Loading)

    val paginationState: StateFlow<PaginationStateModel>
        field = MutableStateFlow<PaginationStateModel>(PaginationStateModel.Initial)

    init {
        observeMovies()
        scope.launch {
            checkAndRefreshIfStale()
        }
    }

    private fun observeMovies() {
        moviesJob?.cancel()
        moviesJob = scope.launch {
            catalogOrchestrator.observeMovies().collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        val movies = result.data

                        if (movies.movies.isEmpty()) {
                            movieScreenState.update { UiState.Loading }
                        } else {
                            movieScreenState.update { mapSuccess(movies) }
                        }
                    }

                    is AppResult.Failure -> {
                        movieScreenState.update { mapError(result.error) }
                    }
                }
            }
        }
    }

    private suspend fun checkAndRefreshIfStale() {
        if (catalogOrchestrator.shouldRefreshMovies()) {
            catalogOrchestrator.fetchMovies(language)
                .onFailure { error ->
                    movieScreenState.update { mapError(error) }
                }
        }
    }

    fun loadMore() {
        val current = paginationState.value
        if (current.isLoadingMore || !current.hasMorePages) return

        scope.launch {
            paginationState.update { it.copy(isLoadingMore = true) }

            when (val result =
                catalogOrchestrator.loadMoreMovies(language, current.currentPage + 1)) {
                is AppResult.Success -> {
                    paginationState.update {
                        it.copy(
                            isLoadingMore = false,
                            currentPage = result.data.currentPage,
                            hasMorePages = result.data.hasMore
                        )
                    }
                }

                is AppResult.Failure -> {
                    paginationState.update {
                        it.copy(isLoadingMore = false)
                    }
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(result.error))
                    )
                }
            }
        }
    }

    fun onEvent(event: MovieEventModel) {
        when (event) {
            is MovieEventModel.OnCardDoubleTap -> scope.launch { toggleFavorite(event.movie.id) }
            is MovieEventModel.OnCardSingleTap -> scope.launch { handleSingleTap(event.movieId) }
            is MovieEventModel.RetryMovies -> observeMovies()
        }
    }

    private suspend fun handleSingleTap(movieId: Long) {
        val canAccess = userActionOrchestrator.canAccessMovieDetail(movieId)

        if (canAccess) {
            eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.MovieDetail(movieId)))
        } else {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(UIText.Resource(Res.string.connection_issue))
            )
        }
    }

    private suspend fun toggleFavorite(movieId: Long) {
        val currentState = movieScreenState.value
        if (currentState !is UiState.Success) return

        val movie = currentState.data.firstOrNull { it.id == movieId } ?: return
        val newFavoriteState = !movie.isFavorite

        userActionOrchestrator.toggleFavoriteAndGetUid(movieId, newFavoriteState)
            .onSuccess { uid ->
                scope.launch {
                    userActionOrchestrator.syncFavoriteToCircle(
                        uid,
                        movieId,
                        newFavoriteState
                    )
                }

                if (newFavoriteState) {
                    checkAndNotifyMatch(uid, movieId)
                }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun checkAndNotifyMatch(uid: String, movieId: Long) {
        userActionOrchestrator.checkForMovieMatch(uid, movieId)
            .onSuccess { result ->
                if (result is MovieMatchResult.Match) {
                    val ownerAvatarUrl = userActionOrchestrator.getOwnerAvatarUrl(uid)
                    val matchingMembers = result.matchingMemberDomainModels.map { member ->
                        MatchingMemberInfo(member.displayName, member.avatarUrl)
                    }.toImmutableList()

                    eventHandler.sendEvent(
                        UiEvent.ShowAnimation(
                            MatchAnimationData(ownerAvatarUrl, matchingMembers)
                        )
                    )
                }
            }
            .onFailure { error ->
                Logger.e("Failed to check movie match: $error")
            }
    }

    private fun mapSuccess(success: ObserveAllMoviesSuccess): UiState<ImmutableList<MovieUiModel>> {
        val movies = success.movies.map { it.toMovieUiModel() }.toImmutableList()
        return if (movies.isEmpty()) UiState.Empty else UiState.Success(movies)
    }

    private fun mapError(error: AppError): UiState.Error =
        UiState.Error(
            defaultMessageMapper.toUIText(error),
            hint = UIErrorHint(retry = RetryPolicy.Manual)
        )

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
