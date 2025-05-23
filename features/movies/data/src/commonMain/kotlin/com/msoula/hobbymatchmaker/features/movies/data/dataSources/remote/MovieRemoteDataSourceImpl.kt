package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException

class MovieRemoteDataSourceImpl(
    private val imageRepository: ImageRepository,
    private val firestore: FirebaseFirestore,
    private val tmdbKtorService: TMDBKtorService
) : MovieRemoteDataSource {

    override suspend fun fetchMovies(language: String):
        Result<List<MovieDomainModel>, MovieErrors> {
        val pages = listOf(1, 2, 3)
        val movies = mutableListOf<MovieDomainModel>()

        for (page in pages) {
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
                .set(mapOf("movies" to FieldValue.arrayUnion(movieId)), merge = true)
        } else {
            firestore.collection("users").document(uuidUser)
                .set(mapOf("movies" to FieldValue.arrayRemove(movieId)), merge = true)
        }
    }

    private suspend fun fetchMoviesByPage(
        language: String,
        page: Int
    ): Result<List<MovieDomainModel>, MovieErrors.FetchMovieByPageError> {
        return try {
            when (val response = tmdbKtorService.getMoviesByPopularityDesc(language, page)) {
                is Result.Success -> {
                    val data = response.data.results?.map { it.toMovieDomainModel() }
                        ?: emptyList()
                    Result.Success(data)
                }

                is Result.Failure -> {
                    Logger.e("Returning failure from fetchMoviesByPage: ${response.error}")
                    Result.Failure(MovieErrors.FetchMovieByPageError(response.error.message))
                }

                else -> Result.Success(emptyList())
            }
        } catch (e: IOException) {
            Logger.e("Error fetching movies by page: $e")
            Result.Failure(MovieErrors.NetworkError(e.message ?: ""))
        } catch (e: Exception) {
            Logger.e("Error fetching movies by page: $e")
            Result.Failure(MovieErrors.UnknownError(e.message ?: ""))
        }
    }

    private suspend fun updateLocalPosterPath(list: List<MovieDomainModel>): List<MovieDomainModel> {
        return coroutineScope {
            list.map { movie ->
                async {
                    if (movie.coverFileName.isBlank()) {
                        Logger.w("Skipping movie ${movie.title} (${movie.id}: poster")
                        return@async movie
                    }

                    try {
                        val localPath = imageRepository.getRemoteImage(movie.coverFileName)
                        movie.copy(localCoverFilePath = localPath)
                    } catch (e: Exception) {
                        Logger.e("Error downloading image for ${movie.title}: ${e.message}")
                        movie
                    }
                }
            }.awaitAll()
        }
    }
}
