package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        val movieId = savedStateHandle.toString()
        Log.d("HMM", "Movie id is: $movieId")
    }

    /* private val movieId: String = savedStateHandle.get<String>("value")
        ?: throw IllegalArgumentException("Missing movieID") */

    private val _movie = MutableStateFlow("")
    val movie = _movie.asStateFlow()

    init {
        /* viewModelScope.launch(ioDispatcher) {
            when (val data = fetchMovieDetailUseCase(movieId.toLong())) {
                is Result.Success -> {
                    Log.i("HMM", "Title is: ${data.data?.title}")
                    _movie.update { data.data?.title ?: "" }
                }

                is Result.Failure -> {
                    Log.e("HMM", "Error occurred: ${data.error.message}")
                }
            }
        } */
    }

}
