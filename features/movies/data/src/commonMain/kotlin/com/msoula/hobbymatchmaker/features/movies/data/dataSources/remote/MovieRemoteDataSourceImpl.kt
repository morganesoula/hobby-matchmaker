package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteDataModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.PaginatedMovieResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore

class MovieRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val tmdbKtorService: TMDBKtorService
) : MovieRemoteDataSource {

    override suspend fun refreshMovies(language: String): AppResult<List<MovieRemoteDataModel>, AppError> {
        val pages = listOf(1, 2, 3)
        val movies = mutableListOf<MovieRemoteDataModel>()

        for (page in pages) {
            when (val result = fetchMoviesByPage(language, page)) {
                is AppResult.Success -> movies.addAll(result.data)
                is AppResult.Failure -> return result
            }
        }

        return AppResult.Success(movies)
    }

    override suspend fun fetchMoviesPage(
        language: String,
        page: Int
    ): AppResult<PaginatedMovieResult, AppError> =
        tmdbKtorService.getMoviesByPopularityDesc(language, page)
            .mapSuccess { response ->
                PaginatedMovieResult(
                    movies = response.results ?: emptyList(),
                    currentPage = response.page ?: page,
                    totalPages = response.totalPages ?: 1,
                    hasMore = (response.page ?: page) < (response.totalPages ?: 1)
                )
            }

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = safeFirebaseCall {
        if (isFavorite) {
            firestore.collection("users").document(uuidUser)
                .set(mapOf("movies" to FieldValue.arrayUnion(movieId)), merge = true)
        } else {
            firestore.collection("users").document(uuidUser)
                .set(mapOf("movies" to FieldValue.arrayRemove(movieId)), merge = true)
        }
    }

    override suspend fun setUserFavoriteMovies(
        uid: String,
        ids: List<Long>
    ): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore.collection("users").document(uid)
                .set(mapOf("movies" to ids), merge = true)
        }

    private suspend fun fetchMoviesByPage(
        language: String,
        page: Int
    ): AppResult<List<MovieRemoteDataModel>, AppError> =
        tmdbKtorService.getMoviesByPopularityDesc(language, page)
            .mapSuccess { response -> response.results ?: emptyList() }
}
