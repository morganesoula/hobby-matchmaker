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
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModel(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val logOutUseCase: LogOutUseCase,
    private val checkMovieSynopsisValueUseCase: CheckMovieSynopsisValueUseCase,
    private val connectivityCheck: NetworkConnectivityChecker,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    private val _oneTimeEventChannel = Channel<MovieUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()
    private val language = getDeviceLocale()

    val movieState: StateFlow<MovieUiStateModel> =
        observeAllMoviesUseCase(language).mapLatest { result ->
            when (result) {
                is AppResult.Success -> {
                    when (val payload = result.data) {
                        is ObserveAllMoviesSuccess.Success -> {
                            MovieUiStateModel.Success(
                                payload.movies.map { it.toMovieUiModel() }
                            )
                        }

                        is ObserveAllMoviesSuccess.DataLoadedInDB -> MovieUiStateModel.Loading
                    }
                }

                is AppResult.Failure -> {
                    MovieUiStateModel.Error(
                        defaultMessageMapper.toUIText(result.error)
                    )
                }
            }
        }
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )

    fun logOut() {
        scope.launch {
            logOutUseCase()
                .onFailure {
                    _oneTimeEventChannel.send(
                        MovieUiEventModel.OnLogOutFailure(
                            defaultMessageMapper.toUIText(it)
                        )
                    )
                }
                .onSuccess {
                    _oneTimeEventChannel.send(MovieUiEventModel.OnLogOutSuccess)
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
                Logger.d("Detect single tap on card")
                scope.launch {
                    val eventToSend = handleSingleTap(event.movieId)
                    Logger.d("Event to send: $eventToSend")
                    sendOnce(eventToSend)
                }
            }
        }
    }

    internal suspend fun handleSingleTap(movieId: Long): MovieUiEventModel {
        val localResult = checkMovieSynopsisValueUseCase(movieId)
        val hasConnectivity = connectivityCheck.hasActiveConnection()

        val hasLocal = when (localResult) {
            is AppResult.Success -> localResult.data
            is AppResult.Failure -> false
        }

        return when {
            hasLocal || hasConnectivity -> MovieUiEventModel.OnMovieDetailClicked(movieId)
            else -> MovieUiEventModel.NoFetchingDetailPossible
        }
    }

    private suspend fun toggleFavorite(movieId: Long, isFavorite: Boolean) {
        fetchFirebaseUserInfo()
            .onFailure {
                val uiError = defaultMessageMapper.toUIText(it)
                _oneTimeEventChannel.trySend(MovieUiEventModel.ShowError(uiError))
            }
            .onSuccess { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        val uid = state.user.uid
                        setMovieFavoriteUseCase(uid ?: "", movieId, isFavorite)
                            .onFailure {
                                val uiError = defaultMessageMapper.toUIText(it)
                                _oneTimeEventChannel.trySend(MovieUiEventModel.ShowError(uiError))
                            }
                            .onSuccess {
                                Unit
                            }
                    }

                    AuthState.SignedOut -> {
                        setMovieFavoriteUseCase("", movieId, isFavorite)
                            .onFailure {
                                val uiError = defaultMessageMapper.toUIText(it)
                                _oneTimeEventChannel.trySend(MovieUiEventModel.ShowError(uiError))
                            }
                            .onSuccess {
                                Unit
                            }
                    }
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
