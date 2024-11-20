package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import kotlinx.coroutines.tasks.await
import okio.IOException

class MovieRemoteDataSourceImpl(
    private val tmdbService: TMDBService,
    private val imageHelper: ImageHelper,
    private val firestore: FirebaseFirestore
) : MovieRemoteDataSource {

    override suspend fun fetchMovies(language: String):
        Result<List<MovieDomainModel>, MovieErrors> {
        val pages = listOf(1, 2, 3)
        val movies = mutableListOf<MovieDomainModel>()

        pages.forEach { page ->
            when (val result = fetchMoviesByPage(language, page)) {
                is Result.Success -> movies.addAll(result.data)
                is Result.Failure -> return result
                else -> Unit
            }
        }

        val updatedList = updateLocalPosterPath(movies)
        return Result.Success(updatedList)
    }

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ) {
        if (isFavorite) {
            firestore.collection("users").document(uuidUser)
                .update("movies", FieldValue.arrayUnion(movieId))
                .await()
        } else {
            firestore.collection("users").document(uuidUser)
                .update("movies", FieldValue.arrayRemove(movieId))
                .await()
        }
    }

    private suspend fun fetchMoviesByPage(
        language: String,
        page: Int
    ): Result<List<MovieDomainModel>, MovieErrors.FetchMovieByPageError> {
        return try {
            val response = tmdbService.getMoviesByPopularityDesc(language, page)
            val data = response.body()?.results?.map { it.toMovieDomainModel() } ?: emptyList()
            Result.Success(data)
        } catch (e: IOException) {
            Result.Failure(MovieErrors.NetworkError(e.message ?: ""))
        } catch (e: ApiException) {
            Result.Failure(MovieErrors.ApiError(e.message ?: ""))
        } catch (e: Exception) {
            Result.Failure(MovieErrors.UnknownError(e.message ?: ""))
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
