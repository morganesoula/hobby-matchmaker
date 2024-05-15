package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import javax.inject.Inject

class MovieRemoteDataSourceImpl @Inject constructor(
    private val tmdbService: TMDBService,
    private val imageHelper: ImageHelper
) : MovieRemoteDataSource {

    override suspend fun fetchMovies(): Result<List<MovieDomainModel>> {
        val list = buildList {
            fetchMoviesByPage("fr-FR", 1).mapSuccess { movies1 ->
                addAll(movies1)
                fetchMoviesByPage("fr-FR", 2).mapSuccess { movies2 ->
                    addAll(movies2)
                    fetchMoviesByPage("fr-FR", 3).mapSuccess { movies3 ->
                        addAll(movies3)
                    }.mapError { error3 -> return@mapError error3 }
                }.mapError { error2 -> return@mapError error2 }
            }.mapError { error1 -> return@mapError error1 }
        }

        val updatedList = updateLocalPosterPath(list)
        return Result.Success(updatedList)
    }

    private suspend fun fetchMoviesByPage(
        language: String,
        page: Int
    ): Result<List<MovieDomainModel>> {
        return execute({
            tmdbService.getMoviesByPopularityDesc(
                language,
                page
            )
        }).mapSuccess { response ->
            response.results?.map {
                it.toMovieDomainModel()
            } ?: emptyList()
        }
    }

    private suspend fun updateLocalPosterPath(list: List<MovieDomainModel>): List<MovieDomainModel> {
        val mutableMovieList = list.toMutableList()

        for ((index, movie) in mutableMovieList.withIndex()) {
            mutableMovieList[index] =
                movie.copy(
                    localCoverFilePath = imageHelper.getRemoteImage(
                        movie.coverFileName
                    )
                )
        }

        return mutableMovieList.map { it }
    }
}
