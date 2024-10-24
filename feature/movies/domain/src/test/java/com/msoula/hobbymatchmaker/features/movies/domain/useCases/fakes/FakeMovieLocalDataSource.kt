package com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes

import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieLocalDataSource : MovieLocalDataSource {

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

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        fakeMovies.addAll(movies)
    }

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return flow {
            emit(fakeMovies)
        }
    }

    override suspend fun updateMovieWithFavoriteValue(id: Long, isFavorite: Boolean) {
        fakeMovies.find { it.id == id }?.let { movie ->
            val updatedMovie = movie.copy(isFavorite = isFavorite)
            fakeMovies[fakeMovies.indexOf(movie)] = updatedMovie
        }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        fakeMovies.find { it.coverFileName == coverFileName }?.let {
            val updatedMovie = it.copy(localCoverFilePath = localCoverFilePath)
            fakeMovies[fakeMovies.indexOf(it)] = updatedMovie
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) {
        fakeMovies.add(movie)
    }

    fun clearData() {
        fakeMovies.clear()
    }

    fun getFirstElement(): MovieDomainModel {
        return fakeMovies.first()
    }
}
