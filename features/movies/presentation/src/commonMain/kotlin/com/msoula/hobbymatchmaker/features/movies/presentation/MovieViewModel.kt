package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.connection_issue
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModel(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val logOutUseCase: LogOutUseCase,
    private val checkMovieSynopsisValueUseCase: CheckMovieSynopsisValueUseCase,
    private val connectivityCheck: NetworkConnectivityChecker,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val language = getDeviceLocale()

    private val _screenState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        observeMovies()
    }

    fun observeMovies() {
        scope.launch {
            observeAllMoviesUseCase(language).collect { result ->
                _screenState.update {
                    when (result) {
                        is AppResult.Success -> {
                            when (val payload = result.data) {
                                is ObserveAllMoviesSuccess.Success -> {
                                    val movies = payload.movies.map { it.toMovieUiModel() }
                                    if (movies.isEmpty()) UiState.Empty else UiState.Success(movies)
                                }

                                is ObserveAllMoviesSuccess.DataLoadedInDB -> UiState.Loading
                            }
                        }

                        is AppResult.Failure -> {
                            UiState.Error(
                                error = defaultMessageMapper.toUIText(result.error),
                                hint = UIErrorHint(retry = RetryPolicy.Manual)
                            )
                        }
                    }
                }
            }
        }
    }

    fun logOut() {
        scope.launch {
            logOutUseCase()
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(
                            defaultMessageMapper.toUIText(error)
                        )
                    )
                }
                .onSuccess {
                    Logger.d("Successfully logged out")
                    eventHandler.sendEvent(UiEvent.NavigateToRoute("sign_in"))
                }
        }
    }

    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> {
                scope.launch {
                    toggleFavorite(event.movie.id, !event.movie.isFavorite)
                }
            }

            is CardEventModel.OnSingleTap -> {
                scope.launch {
                    handleSingleTap(event.movieId)
                }
            }
        }
    }

    internal suspend fun handleSingleTap(movieId: Long) {
        val localResult = checkMovieSynopsisValueUseCase(movieId)
        val hasConnectivity = connectivityCheck.hasActiveConnection()

        val hasLocal = when (localResult) {
            is AppResult.Success -> localResult.data
            is AppResult.Failure -> false
        }

        if (hasLocal || hasConnectivity) {
            eventHandler.sendEvent(UiEvent.NavigateToDetail(movieId))
        } else {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(
                    UIText.Resource(Res.string.connection_issue)
                )
            )
        }
    }

    private suspend fun toggleFavorite(movieId: Long, isFavorite: Boolean) {
        fetchFirebaseUserInfo()
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
            .onSuccess { state ->
                val uid = when (state) {
                    is AuthState.Authenticated -> state.user.uid
                    AuthState.SignedOut -> ""
                }

                setMovieFavoriteUseCase(uid, movieId, isFavorite)
                    .onFailure { error ->
                        eventHandler.sendEvent(
                            UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                        )
                    }
            }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
