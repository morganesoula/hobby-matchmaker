package com.msoula.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.data.model.toListMovieUI
import com.msoula.movies.domain.use_case.DeleteAllMovies
import com.msoula.movies.domain.use_case.InsertMovieUseCase
import com.msoula.movies.domain.use_case.ObserveMoviesUseCase
import com.msoula.movies.domain.use_case.SetMovieFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val deleteAllMovies: DeleteAllMovies,
    private val insertMovieUseCase: InsertMovieUseCase,
) : ViewModel() {
    private val _displayingData = MutableStateFlow(false)
    val displayingData = _displayingData.asStateFlow()

    val viewState: StateFlow<MovieUiStateResult> by lazy {
        observeMoviesUseCase()
            .mapLatest { movies ->
                when {
                    movies.isNullOrEmpty() -> {
                        MovieUiStateResult.Empty
                    }

                    else -> {
                        Log.d("HMM", "List fetched in ViewModel is: $movies")
                        MovieUiStateResult.Fetched(list = movies.toListMovieUI())
                    }
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
                Log.d("HMM", "Into VM on Update with ${event.movie.isFavorite}")
                viewModelScope.launch {
                    setMovieFavoriteUseCase(event.movie.id, !event.movie.isFavorite)
                }
            }
        }
    }

    private fun clear() {
        viewModelScope.launch {
            deleteAllMovies()
        }
    }
}
