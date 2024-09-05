package com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.errors.LanguageNotSupportedError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieRemoteDataSource : MovieRemoteDataSource {

    private val fakeMovies = mutableListOf<MovieDomainModel>().apply {
        addAll(
            listOf(
                MovieDomainModel(
                    id = 1,
                    title = "Movie 1",
                    coverFileName = "cover1.jpg",
                    localCoverFilePath = "",
                    isFavorite = false,
                    isSeen = false,
                    overview = "Overview 1"
                ),
                MovieDomainModel(
                    id = 2,
                    title = "Movie 2",
                    coverFileName = "cover2.jpg",
                    localCoverFilePath = "",
                    isFavorite = false,
                    isSeen = false,
                    overview = "Overview 2"
                )
            )
        )
    }

    override suspend fun fetchMovies(language: String): Result<List<MovieDomainModel>> {
        return if (language == "fr-FR") {
            Result.Success(fakeMovies)
        } else {
            Result.Failure(LanguageNotSupportedError("Language not supported"))
        }
    }

    override suspend fun upsertMovies(movies: List<MovieDomainModel>) {
        fakeMovies.addAll(movies)
    }

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return flow {
            emit(fakeMovies)
        }
    }

    override suspend fun setMovieFavoriteValue(movieId: Long, isFavorite: Boolean) {
        fakeMovies.find { it.id == movieId }?.let { movie ->
            val updatedMovie = movie.copy(isFavorite = isFavorite)
            fakeMovies[fakeMovies.indexOf(movie)] = updatedMovie
        }
    }

    override suspend fun setMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        fakeMovies.find { it.coverFileName == coverFileName }?.let {
            val updatedMovie = it.copy(localCoverFilePath = localCoverFilePath)
            fakeMovies[fakeMovies.indexOf(it)] = updatedMovie
        }
    }

    fun getFirstElement(): MovieDomainModel {
        return fakeMovies.first()
    }

    fun clearData() {
        fakeMovies.clear()
    }
}
