package com.msoula.hobbymatchmaker.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.msoula.hobbymatchmaker.core.dao.models.MovieEntityModel
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

    @Query("UPDATE movies SET favourite = :isFavourite WHERE id = :id")
    suspend fun updateMovieFavorite(
        id: Long,
        isFavourite: Boolean,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieEntity: MovieEntityModel)
}
