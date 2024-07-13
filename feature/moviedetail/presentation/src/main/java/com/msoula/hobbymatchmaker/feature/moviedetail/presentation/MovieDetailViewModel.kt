package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailEvent
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.toMovieDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _movie = MutableStateFlow(MovieDetailUiModel(""))
    val movie = _movie.asStateFlow()

    private val _oneTimeEventChannel = Channel<MovieDetailEvent>()
    val oneTimeEvent = _oneTimeEventChannel.receiveAsFlow()

    init {
        val movieId = requireNotNull(savedStateHandle.get<Long>("movieId"))
        Log.d("HMM", "Movie id is: $movieId")

        viewModelScope.launch(ioDispatcher) {
            when (val data = fetchMovieDetailUseCase(movieId = movieId)) {
                is Result.Success -> {
                    val movieUi = data.data?.toMovieDetailUiModel()
                    Log.d("HMM", "Updating movie with: $movieUi")
                    _movie.update { it.copy(title = movieUi?.title ?: "") }
                }

                is Result.Failure -> {
                    Log.e("HMM", "Error occurred: ${data.error.message}")
                    withContext(Dispatchers.Main) {
                        _oneTimeEventChannel.send(MovieDetailEvent.OnMovieDetailFetchedError(data.error.message))
                    }
                }
            }
        }
    }
}
