package com.msoula.database.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.msoula.database.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {
    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies")
    fun getMoviesByPopularityDesc(): Flow<List<MovieEntity>>

    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
