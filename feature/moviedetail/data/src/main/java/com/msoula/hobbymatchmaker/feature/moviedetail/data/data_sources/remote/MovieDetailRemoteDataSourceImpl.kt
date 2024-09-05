package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieVideosService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.Genre
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailService: MovieDetailService,
    private val movieVideosService: MovieVideosService,
    private val firestore: FirebaseFirestore
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel> {
        return execute({
            movieDetailService.fetchMovieDetail(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieDetailDomainModel()
            }
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?> {
        return execute({
            movieDetailService.fetchMovieCredits(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieActorDomainModel()
            }
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?> {
        return execute({
            movieVideosService.fetchMovieVideos(movieId, language)
        })
            .mapSuccess {
                it.toMovieVideoDomainModel()
            }
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {
        try {
            val movieQuery = firestore.collection("movies")
                .whereEqualTo("id", movieDetail.id)
                .get()
                .await()

            if (movieQuery.documents.isNotEmpty()) {
                val movieReference = movieQuery.documents.first().reference
                val existingMovie = movieReference.get().await().toObject<MovieDetailDomainModel>()

                Log.d("HMM", "Existing movie is: $existingMovie")

                val movieData = hashMapOf(
                    "title" to movieDetail.title,
                    "genre" to movieDetail.genre?.map { genre ->
                        mapOf(
                            "id" to (genre.id ?: Genre.DEFAULT_ID),
                            "name" to (genre.name ?: Genre.DEFAULT_NAME)
                        )
                    },
                    "popularity" to movieDetail.popularity,
                    "releaseDate" to movieDetail.releaseDate,
                    "synopsis" to movieDetail.synopsis,
                    "status" to movieDetail.status,
                    "cast" to movieDetail.cast?.map { actor ->
                        mapOf(
                            "id" to actor.id,
                            "name" to actor.name,
                            "role" to actor.role
                        )
                    }
                )

                movieReference.update(movieData).await()
            } else {
                Log.e("HMM", "Movie not found")
            }
        } catch (e: Exception) {
            Log.e("HMM", "Error saving movie detail", e)
        }
    }

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        val movieReference = firestore.collection("movies").document(movieId.toString())

        Log.d("HMM", "Into ObserveMovieDetail with movieReference: ${movieReference.get().await()}")

        return movieReference.snapshots().map { snapshot ->
            if (snapshot.exists()) {
                val movieDetail = snapshot.toObject<MovieDetailDomainModel>()
                Log.d("HMM", "Movie detail observed is: $movieDetail")
                movieDetail
            } else {
                Log.d("HMM", "No movie detail found for ID: $movieId")
                null
            }
        }
    }

    override suspend fun updateMovieVideoUri(movieId: Long, videoPath: String) {
        try {
            val movieQuery = firestore.collection("movies")
                .whereEqualTo("id", movieId)
                .get()
                .await()

            if (movieQuery.documents.isNotEmpty()) {
                val movieReference = movieQuery.documents.first().reference
                movieReference.update(
                    mapOf(
                        "videoKey" to videoPath
                    )
                ).await()
            } else {
                Log.e("HMM", "Movie not found")
            }
        } catch (e: Exception) {
            Log.e("HMM", "Error updating movie video path", e)
        }
    }
}

