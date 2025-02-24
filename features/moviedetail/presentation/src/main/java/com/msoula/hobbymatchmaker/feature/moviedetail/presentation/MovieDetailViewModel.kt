package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.toMovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _oneTimeEventChannel = Channel<MovieDetailUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private val movieId = requireNotNull(savedStateHandle.get<Long>("movieId"))
    private val movieIdFlow = MutableStateFlow<Long?>(null)

    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()
    private val language = getDeviceLocale()

    init {
        setMovieId(movieId)
    }

    @SuppressLint("NewApi")
    val viewState: StateFlow<MovieDetailViewStateModel> =
        movieIdFlow.filterNotNull().flatMapLatest { movieId ->
            observeMovieDetailUseCase(Parameters.LongStringParam(movieId, language))
                .map { result ->
                    when (result) {
                        is Result.Loading -> MovieDetailViewStateModel.Loading
                        is Result.Success -> {
                            when (val data = result.data) {
                                is ObserveMovieSuccess.Success -> {
                                    currentMovie = data.data.toMovieDetailUiModel()
                                    MovieDetailViewStateModel.Success(data.data.toMovieDetailUiModel())
                                }

                                else -> MovieDetailViewStateModel.Empty
                            }
                        }

                        is Result.Failure -> {
                            MovieDetailViewStateModel.Error(result.error.message)
                        }
                    }
                }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieDetailViewStateModel.Loading
            )

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked -> {
                viewModelScope.launch(ioDispatcher) {
                    onPlayTrailerClicked(
                        event.movieId,
                        event.isVideoURIknown
                    )
                }
            }

            else -> Unit
        }
    }

    @SuppressLint("NewApi")
    private suspend fun onPlayTrailerClicked(movieId: Long, isVideoURIknown: Boolean) {
        if (isVideoURIknown) {
            sendOnce(
                MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                    currentMovie?.videoKey ?: ""
                )
            )
        } else {
            manageMovieTrailerUseCase(
                Parameters.LongStringParam(
                    movieId,
                    language
                )
            ).collect { result ->
                when (result) {
                    is Result.Success -> sendOnce(
                        MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                            result.data.videoURI
                        )
                    )

                    is Result.Loading -> sendOnce(MovieDetailUiEventModel.LoadingTrailer)
                    is Result.Failure -> sendOnce(
                        MovieDetailUiEventModel.ErrorFetchingTrailer(
                            result.error.message
                        )
                    )
                }
            }
        }
    }

    private fun setMovieId(movieId: Long) {
        if (movieIdFlow.value != movieId) {
            movieIdFlow.value = movieId
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieDetailUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
