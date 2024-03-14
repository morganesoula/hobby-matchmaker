package com.msoula.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.data.model.toListMovieUI
import com.msoula.movies.domain.useCases.DeleteAllMovies
import com.msoula.movies.domain.useCases.InsertMovieUseCase
import com.msoula.movies.domain.useCases.ObserveMoviesUseCase
import com.msoula.movies.domain.useCases.SetMovieFavoriteUseCase
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
class MovieViewModel
    @Inject
    constructor(
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
                            MovieUiStateResult.Fetched(list = movies.toListMovieUI())
                        }
                    }
                }
                .flowOn(Dispatchers.Main)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    MovieUiStateResult.Loading,
                )
        }

        // create function

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
                deleteAllMovies()
            }
        }
    }
