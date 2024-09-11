package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.snapshots
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers.toMovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MovieRemoteDataSourceImpl(
    private val tmdbService: TMDBService,
    private val imageHelper: ImageHelper,
    private val firestore: FirebaseFirestore
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

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return firestore.collection("movies")
            .snapshots()
            .map { query ->
                query.documents.mapNotNull {
                    it.toObject(MovieEntityModel::class.java)?.toMovieDomainModel()
                }
            }
    }

    override suspend fun upsertMovies(movies: List<MovieDomainModel>) {
        val movieChunks = if (movies.size < 500) listOf(movies) else movies.chunked(500)

        movieChunks.forEach { movieChunk ->
            val batch: WriteBatch = firestore.batch()
            movieChunk.forEach { movie ->
                val movieEntity = movie.toMovieEntityModel()

                val movieRef = firestore.collection("movies").document(movieEntity.id.toString())
                batch.set(
                    movieRef, hashMapOf(
                        "id" to movieEntity.id,
                        "title" to movieEntity.title,
                        "coverFileName" to movieEntity.posterFileName,
                        "localCoverFilePath" to movieEntity.localCoverFilePath,
                        "isFavorite" to movieEntity.isFavorite,
                        "isSeen" to movieEntity.isSeen,
                        "synopsis" to movieEntity.synopsis
                    )
                )
            }
            batch.commit()
                .addOnSuccessListener {
                    Log.i("HMM", "Successfully saved movies online")
                }
                .addOnFailureListener { exception ->
                    Log.e("HMM", "Error saving movies online", exception)
                }
        }
    }

    override suspend fun setMovieFavoriteValue(movieId: Long, isFavorite: Boolean) {
        try {
            val movieSnapshot = firestore.collection("movies")
                .whereEqualTo("id", movieId)
                .get()
                .await()

            if (movieSnapshot.documents.isNotEmpty()) {
                val documentRef = movieSnapshot.documents.first().reference
                documentRef.update(mapOf("isFavorite" to isFavorite)).await()
            } else {
                Log.e("HMM", "Movie not found")
            }
        } catch (e: Exception) {
            Log.e("HMM", "Error setting movie favorite", e)
        }
    }

    override suspend fun setMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        try {
            val movieSnapshot = firestore.collection("movies")
                .whereEqualTo("coverFileName", coverFileName)
                .get()
                .await()

            if (movieSnapshot.documents.isNotEmpty()) {
                val documentRef = movieSnapshot.documents.first().reference
                documentRef.update(mapOf("localCoverFilePath" to localCoverFilePath)).await()
            } else {
                Log.e("HMM", "Movie not found")
            }
        } catch (e: Exception) {
            Log.e("HMM", "Error updating movie", e)
        }
    }
}
