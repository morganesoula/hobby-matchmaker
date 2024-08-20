package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local

import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers.toActorEntityModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers.toMovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers.toMovieInfoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailLocalDataSourceImpl(
    private val movieDAO: MovieDAO
) : MovieDetailLocalDataSource {

    override suspend fun updateMovieInfo(movie: MovieDetailDomainModel) {
        movieDAO.updateMovie(
            movieId = movie.info.id ?: -1,
            genre = movie.info.genre?.map { genre -> genre.name ?: "" },
            releaseDate = movie.info.releaseDate,
            overview = movie.info.synopsis,
            status = movie.info.status,
            popularity = movie.info.popularity
        )
    }

    override suspend fun updateMovieWithActors(movie: MovieDetailDomainModel) {
        movieDAO.updateMovieWithActors(
            movieId = movie.info.id ?: -1,
            genre = movie.info.genre?.map { genre -> genre.name ?: "" },
            releaseDate = movie.info.releaseDate,
            overview = movie.info.synopsis,
            status = movie.info.status,
            popularity = movie.info.popularity,
            actors = movie.cast?.cast?.map { actor -> actor.toActorEntityModel() } ?: emptyList()
        )
    }

    override fun getMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDAO.getMovieWithActors(movieId).map {
            MovieDetailDomainModel(
                it.movie.toMovieInfoDomainModel(),
                it.actors.toMovieCastDomainModel()
            )
        }
    }

    override suspend fun updateMovieVideoURI(movieId: Long, videoURI: String) {
        movieDAO.updateMovieVideoURI(movieId, videoURI)
    }
}
