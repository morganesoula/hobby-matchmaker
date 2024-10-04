package com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.errors.LanguageNotSupportedError

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

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun clearData() {
        fakeMovies.clear()
    }
}
