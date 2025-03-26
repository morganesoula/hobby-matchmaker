package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.core.database.Movie_actor_cross_ref
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import kotlinx.coroutines.flow.Flow

interface MovieDAO {
    suspend fun insertMovie(movie: Movie)
    suspend fun upsertMovies(movies: List<Movie>)
    suspend fun updateExistingMovie(
        movie: Movie
    )
    suspend fun updateExistingMovieWithDetail(
        movieUpdated: MovieUpdatedDataEntity
    )

    suspend fun insertActor(actor: Actor)
    suspend fun insertActors(actors: List<Actor>)
    suspend fun updateExistingActor(actorId: Long, name: String?, role: String?)
    suspend fun insertMovieWithActors(movieWithActors: Movie_actor_cross_ref)

    suspend fun updateMovieCover(
        remotePosterFileName: String,
        localCoverFilePath: String,
        movieId: Long
    )

    suspend fun updateMovieFavorite(movieId: Long, isFavorite: Long)
    suspend fun updateMovieVideoKey(movieId: Long, videoKey: String)

    fun observeMovies(): Flow<List<Movie>>
    fun observeMovieWithActor(movieId: Long): Flow<MovieDetailDataEntity>
    fun getMovieById(movieId: Long): Movie?
}
