package com.msoula.hobbymatchmaker.features.movies.presentation.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieRepository : MovieRepository {

    private var movies: List<MovieDomainModel> = emptyList()

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return flow {
            emit(movies)
        }
    }

    override suspend fun updateMovieWithFavoriteValue(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMovies(language: String): Result<Unit, MovieErrors> {
        return if (language == "en") {
            Result.Success(Unit)
        } else {
            Result.Failure(MovieErrors.NetworkError("Network error"))
        }
    }

    fun setMovies(movies: List<MovieDomainModel>) {
        this.movies = movies
    }
}
