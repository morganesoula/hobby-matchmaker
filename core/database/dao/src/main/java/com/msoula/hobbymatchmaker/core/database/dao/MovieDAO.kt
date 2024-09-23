package com.msoula.hobbymatchmaker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.Genre
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieActorCrossRef
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieWithActors
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {
    @Upsert
    suspend fun upsertMovie(movies: MovieEntityModel)

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntityModel>)

    @Upsert
    suspend fun upsertActors(actors: List<Actor>)

    @Transaction
    suspend fun upsertMovieWithActors(
        movieWithActors: MovieWithActors
    ) {
        updateMovie(
            movieId = movieWithActors.movie.movieId,
            releaseDate = movieWithActors.movie.releaseDate,
            overview = movieWithActors.movie.synopsis,
            genres = movieWithActors.movie.genres,
            status = movieWithActors.movie.status,
            popularity = movieWithActors.movie.popularity
        )

        upsertActors(movieWithActors.actors)
        deleteMovieActorCrossRefs(movieId = movieWithActors.movie.movieId)

        val crossRefs = movieWithActors.actors.map { actor ->
            MovieActorCrossRef(
                movieId = movieWithActors.movie.movieId,
                actorId = actor.actorId
            )
        }

        upsertMovieActorCrossRefs(crossRefs)
    }

    @Query("DELETE FROM movie_actor_cross_ref WHERE movieId = :movieId")
    suspend fun deleteMovieActorCrossRefs(movieId: Long)

    @Upsert
    suspend fun upsertMovieActorCrossRefs(crossRefs: List<MovieActorCrossRef>)

    @Query("UPDATE movie SET localCoverFilePath = :localCoverFilePath WHERE posterFileName = :remotePosterFileName")
    fun updateMovieCover(remotePosterFileName: String, localCoverFilePath: String)

    @Query("UPDATE movie SET isFavorite = :isFavorite WHERE movieId = :movieId")
    fun updateMovieFavorite(movieId: Long, isFavorite: Boolean)

    @Query("UPDATE movie SET videoKey = :videoKey WHERE movieId = :movieId")
    fun updateMovieVideoKey(movieId: Long, videoKey: String)

    @Query("UPDATE movie SET releaseDate = :releaseDate, synopsis = :overview, status = :status, popularity = :popularity, genres = :genres WHERE movieId = :movieId")
    suspend fun updateMovie(
        movieId: Long,
        releaseDate: String?,
        overview: String?,
        genres: List<Genre>?,
        status: String?,
        popularity: Double?
    )

    @Query("SELECT * from movie")
    fun observeMovies(): Flow<List<MovieEntityModel>>

    @Transaction
    @Query("SELECT * from movie WHERE movieId = :movieId")
    fun observeMovieWithActor(movieId: Long): Flow<MovieWithActors>
}
