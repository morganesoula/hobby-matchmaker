package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.core.database.Movie_actor_cross_ref
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDAOImpl(private val database: HMMDatabase) : MovieDAO {

    override suspend fun insertMovie(movie: Movie) {
        database.hmm_databaseQueries.insertMovie(
            movieId = movie.movieId,
            title = movie.title,
            posterFileName = movie.posterFileName,
            synopsis = movie.synopsis,
            releaseDate = movie.releaseDate,
            genres = movie.genres.toString(),
            localCoverFilePath = movie.localCoverFilePath,
            isFavorite = movie.isFavorite,
            isSeen = movie.isSeen,
            popularity = movie.popularity,
            status = movie.status,
            videoKey = movie.videoKey
        )
    }

    override suspend fun updateExistingMovie(movie: Movie) {
        database.hmm_databaseQueries.updateExistingMovie(
            movieId = movie.movieId,
            title = movie.title,
            posterFileName = movie.posterFileName,
            synopsis = movie.synopsis,
            releaseDate = movie.releaseDate,
            genres = movie.genres.toString(),
            localCoverFilePath = movie.localCoverFilePath,
            isFavorite = movie.isFavorite,
            isSeen = movie.isSeen,
            popularity = movie.popularity,
            status = movie.status,
            videoKey = movie.videoKey
        )
    }

    override suspend fun upsertMovies(movies: List<Movie>) {
        database.transaction {
            movies.forEach { movie ->
                val existingMovie =
                    database.hmm_databaseQueries.getMovieById(movie.movieId).executeAsOneOrNull()

                if (existingMovie == null) {
                    database.hmm_databaseQueries.insertMovie(
                        movieId = movie.movieId,
                        title = movie.title,
                        posterFileName = movie.posterFileName,
                        synopsis = movie.synopsis,
                        releaseDate = movie.releaseDate,
                        genres = movie.genres,
                        localCoverFilePath = movie.localCoverFilePath,
                        isFavorite = movie.isFavorite,
                        isSeen = movie.isSeen,
                        popularity = movie.popularity,
                        status = movie.status,
                        videoKey = movie.videoKey
                    )
                } else {
                    database.hmm_databaseQueries.updateExistingMovie(
                        movieId = movie.movieId,
                        title = movie.title,
                        posterFileName = movie.posterFileName,
                        synopsis = movie.synopsis,
                        releaseDate = movie.releaseDate,
                        genres = movie.genres,
                        localCoverFilePath = movie.localCoverFilePath,
                        isFavorite = movie.isFavorite,
                        isSeen = movie.isSeen,
                        popularity = movie.popularity,
                        status = movie.status,
                        videoKey = movie.videoKey
                    )
                }
            }
        }
    }

    override suspend fun updateExistingMovieWithDetail(movieUpdated: MovieUpdatedDataEntity) {
        database.transaction {
            database.hmm_databaseQueries.updateMovieWithDetail(
                movieUpdated.releaseDate,
                movieUpdated.overview,
                movieUpdated.status,
                movieUpdated.popularity,
                movieUpdated.genres,
                movieUpdated.movieId
            )

            movieUpdated.cast.forEach { actor ->
                val existingActor = getActorById(actor.actorId)

                if (existingActor == null) {
                    database.hmm_databaseQueries.insertActor(
                        actorId = actor.actorId,
                        name = actor.name,
                        role = actor.role
                    )
                } else {
                    database.hmm_databaseQueries.updateExistingActor(
                        actor.name, actor.role, actor.actorId
                    )
                }
            }

            movieUpdated.cast.forEach { actor ->
                database.hmm_databaseQueries.insertMovieActorCrossRef(
                    movieId = movieUpdated.movieId,
                    actorId = actor.actorId
                )
            }
        }
    }

    override suspend fun insertActor(actor: Actor) {
        database.hmm_databaseQueries.insertActor(
            actorId = actor.actorId,
            name = actor.name,
            role = actor.role
        )
    }

    override suspend fun insertActors(actors: List<Actor>) {
        database.transaction {
            actors.forEach { actor ->
                database.hmm_databaseQueries.insertActor(
                    actorId = actor.actorId,
                    name = actor.name,
                    role = actor.role
                )
            }
        }
    }

    override suspend fun updateExistingActor(actorId: Long, name: String?, role: String?) {
        database.hmm_databaseQueries.updateExistingActor(
            name, role, actorId
        )
    }

    override suspend fun insertMovieWithActors(movieWithActors: Movie_actor_cross_ref) {
        database.transaction {
            database.hmm_databaseQueries.insertMovieActorCrossRef(
                movieWithActors.movieId,
                movieWithActors.actorId
            )
        }
    }

    override suspend fun updateMovieCover(
        remotePosterFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) {
        database.hmm_databaseQueries.updateMovieWithCover(
            localCoverFilePath,
            remotePosterFileName,
            movieId
        )
    }

    override suspend fun updateMovieFavorite(movieId: Long, isFavorite: Long) {
        database.hmm_databaseQueries.updateMovieWithFavoriteValue(
            isFavorite,
            movieId
        )
    }

    override suspend fun updateMovieVideoKey(movieId: Long, videoKey: String) {
        database.hmm_databaseQueries.updateMovieWithVideoKey(videoKey, movieId)
    }

    override fun observeMovies(): Flow<List<Movie>> {
        return database.hmm_databaseQueries.observeMovies().asFlow().mapToList(Dispatchers.IO)
    }

    override fun observeMovieWithActor(movieId: Long): Flow<MovieDetailDataEntity> {
        return database.hmm_databaseQueries
            .observeDetailMovie(movieId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                if (rows.isEmpty()) throw IllegalStateException("Movie not found")

                val firstRow = rows.first()
                MovieDetailDataEntity(
                    movie = Movie(
                        movieId = movieId, title = firstRow.title,
                        posterFileName = null,
                        synopsis = firstRow.synopsis,
                        releaseDate = firstRow.releaseDate,
                        genres = firstRow.genres,
                        localCoverFilePath = firstRow.localCoverFilePath,
                        isFavorite = null,
                        isSeen = null,
                        popularity = firstRow.popularity,
                        status = firstRow.status,
                        videoKey = firstRow.videoKey,
                    ),
                    actors = rows
                        .filter { it.actorId != null }
                        .map {
                            Actor(
                                actorId = it.actorId ?: 0L,
                                name = it.actorName,
                                role = it.actorRole
                            )
                        }
                )
            }
    }

    override fun getMovieById(movieId: Long): Movie? {
        return database.hmm_databaseQueries.getMovieById(movieId).executeAsOneOrNull()
    }

    override fun getActorById(actorId: Long): Actor? {
        return database.hmm_databaseQueries.getActorById(actorId).executeAsOneOrNull()
    }
}
