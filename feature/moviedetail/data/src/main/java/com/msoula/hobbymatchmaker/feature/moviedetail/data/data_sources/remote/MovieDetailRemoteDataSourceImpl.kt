package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieEntityModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieVideosService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieEntityModel
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
        Log.d("HMM", "Fetching movie trailer online")
        return execute({
            movieVideosService.fetchMovieVideos(movieId, language)
        })
            .mapSuccess {
                Log.i("HMM", "Successfully fetched movie trailer: $it")
                it.toMovieVideoDomainModel()
            }
            .mapError { error ->
                Log.e("HMM", "Error fetching movie trailer: $error")
                error
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
                val movieEntityToSave = movieDetail.toMovieEntityModel()

                movieReference.update(
                    hashMapOf(
                        "title" to movieEntityToSave.title,
                        "genre" to movieEntityToSave.genres,
                        "popularity" to movieEntityToSave.popularity,
                        "releaseDate" to movieEntityToSave.releaseDate,
                        "synopsis" to movieEntityToSave.synopsis,
                        "status" to movieEntityToSave.status,
                        "cast" to movieEntityToSave.cast
                    )
                ).await()
            } else {
                Log.e("HMM", "Movie not found")
            }
        } catch (e: Exception) {
            Log.e("HMM", "Error saving movie detail", e)
        }
    }

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        val movieReference = firestore.collection("movies").document(movieId.toString())

        return movieReference.snapshots().map { snapshot ->
            if (snapshot.exists()) {
                snapshot.toObject<MovieEntityModel>()?.toMovieDetailDomainModel()
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

                Log.d("HMM", "Updating movie video path for $movieReference")

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
