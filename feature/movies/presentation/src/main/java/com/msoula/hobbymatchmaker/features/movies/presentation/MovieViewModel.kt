package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ObserveAuthState
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.DeleteAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SaveLocalCoverPathUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val deleteAllMoviesUseCase: DeleteAllMoviesUseCase,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    private val saveLocalCoverPathUseCase: SaveLocalCoverPathUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val observeAuthState: ObserveAuthState
) : ViewModel() {

    val authState: StateFlow<AuthUiStateModel> by lazy {
        observeAuthState()
            .map { user ->
                user?.let {
                    if (it.isConnected) {
                        AuthUiStateModel.IsConnected
                    } else {
                        AuthUiStateModel.NotConnected
                    }
                } ?: AuthUiStateModel.NotConnected
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthUiStateModel.CheckingState
            )
    }

    val viewState: StateFlow<MovieUiStateModel> by lazy {
        observeAllMoviesUseCase()
            /* .onEach { movies ->
                for (movie in movies) {
                    if (movie.localCoverFilePath == MovieDomainModel.DEFAULT_LOCAL_COVER_FILE_PATH) {
                        saveLocalCoverPathUseCase(movie.coverFileName)
                    }
                }
            } */
            .mapLatest { movies ->
                if (movies.isEmpty()) {
                    when (val result = fetchMovies()) {
                        is Result.Success -> MovieUiStateModel.Loading
                        is Result.Failure -> MovieUiStateModel.Error(result.error.message)
                    }
                } else {
                    MovieUiStateModel.Fetched(list = movies.map { it.toMovieUiModel() }
                        .toPersistentList())
                }
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )
    }

    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> {
                viewModelScope.launch(ioDispatcher) {
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

    private suspend fun fetchMovies(): Result<Unit> {
        val fetchResult = viewModelScope.async(ioDispatcher) {
            fetchMoviesUseCase()
        }

        return fetchResult.await()
    }
}
