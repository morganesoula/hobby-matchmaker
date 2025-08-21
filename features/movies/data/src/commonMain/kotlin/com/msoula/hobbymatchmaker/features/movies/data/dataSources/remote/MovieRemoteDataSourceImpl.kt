package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class MovieRemoteDataSourceImpl(
    private val imageRepository: ImageRepository,
    private val firestore: FirebaseFirestore,
    private val tmdbKtorService: TMDBKtorService
) : MovieRemoteDataSource {

    override suspend fun fetchMovies(language: String): R<List<MovieRemoteModel>, AppError> {
        val pages = listOf(1, 2, 3)
        val movies = mutableListOf<MovieRemoteModel>()

        for (page in pages) {
            when (val result = fetchMoviesByPage(language, page)) {
                is R.Success -> movies.addAll(result.data)
                is R.Failure -> return result
            }
        }

        val updatedList = updateLocalPosterPath(movies)
        return R.Success(updatedList)
    }

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ): R<Unit, AppError> = safeFirebaseCall {
        if (isFavorite) {
            firestore.collection("users").document(uuidUser)
                .set(mapOf("movies" to FieldValue.arrayUnion(movieId)), merge = true)
        } else {
            firestore.collection("users").document(uuidUser)
                .set(mapOf("movies" to FieldValue.arrayRemove(movieId)), merge = true)
        }
    }

    override suspend fun setUserFavoriteMovies(uid: String, ids: List<Long>): R<Unit, AppError> =
        safeFirebaseCall {
            firestore.collection("users").document(uid)
                .set(mapOf("movies" to ids), merge = true)
        }

    private suspend fun fetchMoviesByPage(
        language: String,
        page: Int
    ): R<List<MovieRemoteModel>, AppError> =
        tmdbKtorService.getMoviesByPopularityDesc(language, page)
            .mapSuccess { response -> response.results ?: emptyList() }

    private suspend fun updateLocalPosterPath(list: List<MovieRemoteModel>): List<MovieRemoteModel> {
        return supervisorScope {
            list.map { movie ->
                async {
                    val cover = movie.poster
                    if (cover?.isBlank() == true) {
                        Logger.w("Skipping movie ${movie.title} (${movie.id}: poster")
                        return@async movie
                    }

                    try {
                        val localPath = imageRepository.getRemoteImage(movie.poster.orEmpty())
                        movie.copy(poster = localPath)
                    } catch (e: Exception) {
                        Logger.e("Error downloading image for ${movie.title}: ${e.message}")
                        movie
                    }
                }
            }.awaitAll()
        }
    }
}
