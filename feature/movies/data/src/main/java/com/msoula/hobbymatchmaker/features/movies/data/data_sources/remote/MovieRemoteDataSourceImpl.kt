package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote

import android.util.Log
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRemoteDataSourceImpl @Inject constructor(
    private val tmdbService: TMDBService,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRemoteDataSource {

    override suspend fun fetchMovies() {
        Log.d("HMM", "Into MovieRemoteDataSourceImpl and fetchMovies method")
        val resultPage1 =
            flow {
                emit(
                    tmdbService
                        .getMoviesByPopularityDesc(
                            language = "fr-FR",
                            page = 1,
                        ),
                )
            }

        val resultPage2 =
            flow {
                emit(
                    tmdbService
                        .getMoviesByPopularityDesc(
                            language = "fr-FR",
                            page = 2,
                        ),
                )
            }

        val resultPage3 =
            flow {
                emit(
                    tmdbService
                        .getMoviesByPopularityDesc(
                            language = "fr-FR",
                            page = 3,
                        ),
                )
            }

        combine(resultPage1, resultPage2, resultPage3) { page1, page2, page3 ->
            val combinedList = mutableListOf<MovieRemoteModel>()
            combinedList.addAll(page1.results ?: emptyList())
            combinedList.addAll(page2.results ?: emptyList())
            combinedList.addAll(page3.results ?: emptyList())
            combinedList.toList()
        }.map {
            Log.d("HMM", "Mapping list $it in MovieRemoteDataSourceImpl")
            it.map { movieModel -> movieModel.toMovieDomainModel() }
        }.collectLatest { remoteList ->
            remoteList.toList().forEach { movieDomainModel ->
                Log.d("HMM", "Inserting movie from remoteList in MovieRemoteDataSourceImpl")
                movieLocalDataSource.insertMovie(movieDomainModel)
            }
        }
    }
}
