package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper

class MovieRemoteDataSourceImpl constructor(
    private val tmdbService: TMDBService,
    private val imageHelper: ImageHelper
) : MovieRemoteDataSource {

    override suspend fun fetchMovies(language: String): Result<List<MovieDomainModel>> {
        val pages = listOf(1, 2, 3)
        val movies = mutableListOf<MovieDomainModel>()

        pages.forEach { page ->
            when (val result = fetchMoviesByPage(language, page)) {
                is Result.Success -> movies.addAll(result.data)
                is Result.Failure -> return result
            }
        }

        val updatedList = updateLocalPosterPath(movies)
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
