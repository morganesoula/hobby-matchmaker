package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.errors.FetchingMovieError
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.FetchStatusModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _oneTimeEventChannel = Channel<MovieUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private val fetchStatusFlow = MutableStateFlow<FetchStatusModel>(FetchStatusModel.NeverFetched)

    val movieState: StateFlow<MovieUiStateModel> =
        observeAllMoviesUseCase()
            .combine(fetchStatusFlow) { movies, fetchStatus ->
                Pair(movies, fetchStatus)
            }.mapLatest { (movies, fetchStatus) ->
                when {
                    movies.isEmpty() -> {
                        when (fetchStatus) {
                            is FetchStatusModel.Error -> MovieUiStateModel.Error(fetchStatus.error)
                            is FetchStatusModel.Loading -> MovieUiStateModel.Loading
                            is FetchStatusModel.Success -> MovieUiStateModel.Empty
                            FetchStatusModel.NeverFetched -> {
                                fetchMovies()
                                MovieUiStateModel.Loading
                            }
                        }
                    }

                    else -> {
                        if (fetchStatus is FetchStatusModel.Error) {
                            _oneTimeEventChannel.send(
                                MovieUiEventModel.OnMovieUiFetchedError(fetchStatus.error)
                            )
                        }

                        MovieUiStateModel.Success(
                            list = movies.map { it.toMovieUiModel() }.toPersistentList()
                        )
                    }
                }
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )

    /* val viewState: StateFlow<MovieUiStateModel> by lazy {
        observeAllMoviesUseCase()
            .mapLatest { movies ->
                if (movies.isEmpty()) {
                    when (val result = fetchMovies()) {
                        is Result.Success -> MovieUiStateModel.Loading
                        is Result.Failure -> MovieUiStateModel.Error(result.error.message)
                    }
                } else {
                    MovieUiStateModel.Success(list = movies.map { it.toMovieUiModel() }
                        .toPersistentList())
                }
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )
    } */


    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> {
                viewModelScope.launch(ioDispatcher) {
                    setMovieFavoriteUseCase(event.movie.id, !event.movie.isFavorite)
                }
            }

            is CardEventModel.OnSingleTap -> {
                viewModelScope.launch {
                    _oneTimeEventChannel.send(MovieUiEventModel.OnMovieDetailClicked(event.movieId))
                }
            }
        }
    }

    private suspend fun fetchMovies() {
        val language = getDeviceLocale()

        viewModelScope.launch(ioDispatcher) {
            fetchStatusFlow.emit(FetchStatusModel.Loading)

            fetchMoviesUseCase(language)
                .mapSuccess {
                    fetchStatusFlow.emit(FetchStatusModel.Success)
                }
                .mapError { error ->
                    viewModelScope.launch(ioDispatcher) {
                        fetchStatusFlow.emit(FetchStatusModel.Error(error.message))
                    }

                    FetchingMovieError(error.message)
                }
        }
    }
}
