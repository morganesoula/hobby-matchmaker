package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.errors.FetchingMovieDetailError
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.FetchStatusModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.toMovieDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _oneTimeEventChannel = Channel<MovieDetailUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private val movieId = requireNotNull(savedStateHandle.get<Long>("movieId"))
    private val fetchStatusFlow = MutableStateFlow<FetchStatusModel>(FetchStatusModel.NeverFetched)
    private val movieIdFlow = MutableStateFlow<Long?>(null)

    init {
        setMovieId(movieId)
    }

    val viewState: StateFlow<MovieDetailViewStateModel> =
        movieIdFlow.filterNotNull().flatMapLatest {
            observeMovieDetailUseCase(movieId)
                .combine(fetchStatusFlow) { movie, fetchStatus ->
                    Pair(movie, fetchStatus)
                }.mapLatest { (movie, fetchStatus) ->
                    when {
                        movie?.info?.synopsis.isNullOrBlank() -> {
                            when (fetchStatus) {
                                is FetchStatusModel.Error -> MovieDetailViewStateModel.Error(
                                    fetchStatus.error
                                )

                                FetchStatusModel.Loading -> MovieDetailViewStateModel.Loading
                                FetchStatusModel.NeverFetched -> {
                                    fetchMovieDetail(movieId)
                                    MovieDetailViewStateModel.Loading
                                }

                                FetchStatusModel.Success -> MovieDetailViewStateModel.Empty
                            }
                        }

                        else -> {
                            if (fetchStatus is FetchStatusModel.Error) {
                                _oneTimeEventChannel.send(
                                    MovieDetailUiEventModel.OnMovieDetailUiFetchedError(
                                        fetchStatus.error
                                    )
                                )
                            }
                            MovieDetailViewStateModel.Success(
                                movie?.toMovieDetailUiModel() ?: MovieDetailUiModel()
                            )
                        }
                    }
                }
                .flowOn(Dispatchers.Main)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieDetailViewStateModel.Loading
            )

    private fun setMovieId(movieId: Long) {
        viewModelScope.launch(ioDispatcher) {
            movieIdFlow.emit(movieId)
        }
    }

    private suspend fun fetchMovieDetail(movieId: Long) {
        val language = getDeviceLocale()

        viewModelScope.launch(ioDispatcher) {
            fetchStatusFlow.emit(FetchStatusModel.Loading)

            fetchMovieDetailUseCase(movieId, language)
                .mapSuccess {
                    fetchStatusFlow.emit(FetchStatusModel.Success)
                }
                .mapError { error ->
                    viewModelScope.launch(ioDispatcher) {
                        fetchStatusFlow.emit(FetchStatusModel.Error(error.message))
                    }

                    FetchingMovieDetailError(error.message)
                }
        }
    }
}
