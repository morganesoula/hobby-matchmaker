package com.msoula.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.component.event.SwipeCardEvent
import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.domain.use_case.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase
) : ViewModel() {

    init {
        initMovies()
    }

    private val _movieStateUi = MutableStateFlow(MovieStateUI())
    val movieStateUi = _movieStateUi.asStateFlow()

    private fun initMovies() {
        viewModelScope.launch {
            getMovieUseCase()
                .collect { result ->
                    when (result) {
                        is MovieUiStateResult.Loading -> _movieStateUi.update { it.copy(isLoading = true) }
                        is MovieUiStateResult.Error -> _movieStateUi.update { it.copy(error = result.throwable?.message) }
                        is MovieUiStateResult.Fetched -> {
                            _movieStateUi.update { it.copy(movies = result.list) }
                        }
                    }
                }
        }
    }

    fun onCardSwipeEvent(cardSwipeEvent: SwipeCardEvent) {
        when (cardSwipeEvent) {
            is SwipeCardEvent.OnSwipeLeft -> {
                _movieStateUi.update {
                    it.copy(
                        movies = movieStateUi.value.movies.filterNot { movie -> movie.id == cardSwipeEvent.movieId }
                    )
                }
            }

            is SwipeCardEvent.OnSwipeRight -> {}
        }
    }
}
