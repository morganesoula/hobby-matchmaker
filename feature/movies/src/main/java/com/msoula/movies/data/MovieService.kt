package com.msoula.movies.data

import android.util.Log
import com.msoula.database.data.dao.MovieDAO
import com.msoula.database.data.local.MovieEntity
import com.msoula.movies.data.mapper.MapMoviePogoToMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MovieService
    @Inject
    constructor(
        private val movieDAO: MovieDAO,
        private val tmdbService: TMDBService,
        private val mapperPogoToEntity: MapMoviePogoToMovieEntity,
        private val imageHelper: ImageHelper,
    ) {
        fun observeMovies(): Flow<List<MovieEntity>> {
            Log.d("HMM", "Fetching movies in Service")
            return movieDAO.observeMovies()
                .map {
                    Log.d("HMM", "List in Service is: $it")
                    it
                }
                .onEach { list ->
                    if (list.isEmpty()) {
                        refreshMovies()
                    }
                }
        }

        suspend fun updateMovie(
            movieId: Int,
            isFavorite: Boolean,
        ) {
            Log.d("HMM", "Updating movie in Service")
            movieDAO.updateMovieFavorite(movieId, isFavorite)
        }

        private suspend fun refreshMovies() {
            Log.i("HMM", "Refreshing movies")
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
                val combinedList = page1.results + page2.results + page3.results
                combinedList
            }.collect { finalList ->
                val listOfMovieEntity = mapperPogoToEntity.mapList(finalList)

                try {
                    movieDAO.upsertAll(listOfMovieEntity)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    Log.e("HMM", "Error upserting list of movies")
                }

                saveLocalPoster(listOfMovieEntity.map { it.remotePosterPath })
            }
        }

        private suspend fun saveLocalPoster(remotePosterPaths: List<String>) {
            for (remotePath in remotePosterPaths) {
                imageHelper.saveRemoteImageAndUpdateMovie(
                    remotePosterPath = remotePath,
                ) { localPath ->
                    try {
                        movieDAO.updateMovieWithPosterLocalPath(
                            remotePosterPath = remotePath,
                            localPath = localPath,
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("HMM", "Error updating data: $e")
                    }
                }
            }
        }
    }
