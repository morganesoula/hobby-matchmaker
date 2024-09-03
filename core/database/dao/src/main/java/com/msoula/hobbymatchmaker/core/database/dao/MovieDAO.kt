package com.msoula.hobbymatchmaker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.msoula.hobbymatchmaker.core.database.dao.models.ActorEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieActorCrossEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieWithActorsEntityModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {
    @Upsert
    suspend fun upsertAll(movies: List<MovieEntityModel>)

    @Query("SELECT * FROM movies")
    fun observeMovies(): Flow<List<MovieEntityModel>>

    @Query("UPDATE movies SET local_poster_path = :localPath WHERE poster_path = :remotePosterPath")
    fun updateMovieWithLocalCoverFilePath(
        remotePosterPath: String,
        localPath: String,
    )

    @Query("DELETE FROM movies")
    suspend fun clearAll()

    @Query("UPDATE movies SET favourite = :isFavourite WHERE movieId = :id")
    suspend fun updateMovieFavorite(
        id: Long,
        isFavourite: Boolean,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieEntity: MovieEntityModel)

    @Upsert
    suspend fun upsertActors(actors: List<ActorEntityModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieActorCross(cross: List<MovieActorCrossEntityModel>)

    @Query("DELETE FROM MovieActorCrossEntityModel WHERE movieId = :movieId")
    suspend fun deleteMovieActorsCross(movieId: Long)

    @Transaction
    suspend fun updateMovieWithActors(
        movieId: Long,
        genre: List<String>?,
        releaseDate: String?,
        overview: String?,
        status: String?,
        popularity: Double?,
        actors: List<ActorEntityModel>
    ) {
        updateMovie(movieId, genre, releaseDate, overview, status, popularity)
        upsertActors(actors)
        deleteMovieActorsCross(movieId)

        val crossRef = actors.map { actor ->
            MovieActorCrossEntityModel(movieId, actor.actorId)
        }
        insertMovieActorCross(crossRef)
    }

    @Query("UPDATE movies SET video_key = :videoKey WHERE movieId = :movieId")
    suspend fun updateMovieVideoURI(
        movieId: Long,
        videoKey: String
    )

    @Query("UPDATE movies SET release_date = :releaseDate, overview = :overview, status = :status, popularity = :popularity, genre = :genre WHERE movieId = :movieId")
    suspend fun updateMovie(
        movieId: Long,
        genre: List<String>?,
        releaseDate: String?,
        overview: String?,
        status: String?,
        popularity: Double?
    )

    @Transaction
    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    fun getMovieWithActors(movieId: Long): Flow<MovieWithActorsEntityModel>
}
