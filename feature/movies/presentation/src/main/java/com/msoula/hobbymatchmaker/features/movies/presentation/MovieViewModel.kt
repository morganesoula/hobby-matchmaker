package com.msoula.hobbymatchmaker.features.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_LOCAL_COVER_FILE_PATH
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.DeleteAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SaveLocalCoverPathUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val saveLocalCoverPathUseCase: SaveLocalCoverPathUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val deleteAllMoviesUseCase: DeleteAllMoviesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _displayingData = MutableStateFlow(false)
    val displayingData = _displayingData.asStateFlow()

    val viewState: StateFlow<MovieUiStateResult> by lazy {
        observeAllMoviesUseCase()
            .onCompletion {
                _displayingData.update { true }
            }
            .onEach { movies ->
                movies.map { movie ->
                    if (movie.localCoverFilePath == DEFAULT_LOCAL_COVER_FILE_PATH) {
                        viewModelScope.launch(ioDispatcher) {
                            saveLocalCoverPathUseCase(movie.coverFileName)
                        }
                    }
                }
            }
            .mapLatest { movies ->
                if (movies.isEmpty()) {
                    MovieUiStateResult.Empty
                } else {
                    Log.d("HMM", "Into ViewModel with data: $movies")
                    MovieUiStateResult.Fetched(list = movies.map { it.toMovieUiModel() })
                }
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateResult.Loading
            )
    }

    fun onCardEvent(event: CardEvent) {
        when (event) {
            is CardEvent.OnDoubleTap -> {
                viewModelScope.launch {
                    setMovieFavoriteUseCase(event.movie.id, !event.movie.isFavorite)
                }
            }
        }
    }

    private fun clear() {
        viewModelScope.launch {
            deleteAllMoviesUseCase()
        }
    }
}
