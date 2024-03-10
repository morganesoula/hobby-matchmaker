package com.msoula.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.msoula.database.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {
    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies")
    fun observeMovies(): Flow<List<MovieEntity>>

    @Query("UPDATE movies SET local_poster_path = :localPath WHERE poster_path = :remotePosterPath")
    fun updateMovieWithPosterLocalPath(
        remotePosterPath: String,
        localPath: String,
    )

    @Query("DELETE FROM movies")
    suspend fun clearAll()

    @Query("UPDATE movies SET favourite = :isFavourite WHERE id = :movieId")
    suspend fun updateMovieFavorite(
        movieId: Int,
        isFavourite: Boolean,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieEntity: MovieEntity)
}
