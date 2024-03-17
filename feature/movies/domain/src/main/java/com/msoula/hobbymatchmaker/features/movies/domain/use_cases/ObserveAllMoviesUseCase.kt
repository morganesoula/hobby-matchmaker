package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import android.util.Log
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllMoviesUseCase(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<List<MovieDomainModel>> {
        Log.d("HMM", "Into ObserveAllMoviesUseCase")
        return movieRepository.observeMovies()
    }
}
