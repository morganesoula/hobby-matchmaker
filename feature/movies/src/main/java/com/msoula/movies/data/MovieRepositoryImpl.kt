package com.msoula.movies.data

import com.example.movies.BuildConfig
import com.msoula.database.data.dao.MovieDAO
import com.msoula.database.data.local.MovieEntity
import com.msoula.movies.data.mapper.MapMoviePogoToMovieEntity
import com.msoula.movies.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class MovieRepositoryImpl(
    private val tmdbService: TMDBService,
    private val movieDAO: MovieDAO,
    private val mapperPogoToEntity: MapMoviePogoToMovieEntity
) : MovieRepository {

    override fun getMoviesByPopularityDesc(): Flow<List<MovieEntity>> {
        return movieDAO
            .getMoviesByPopularityDesc()
            .onEach { movies ->
                if (movies.isEmpty()) {
                    refreshMovies()
                }
            }
    }

    override suspend fun refreshMovies() {
        tmdbService
            .getMoviesByPopularityDesc(
                BuildConfig.tmdb_key,
                language = "fr-FR"
            )
            .results
            .map {
                mapperPogoToEntity.map(it)
            }
            .also {
                movieDAO.upsertAll(it)
            }
    }
}
