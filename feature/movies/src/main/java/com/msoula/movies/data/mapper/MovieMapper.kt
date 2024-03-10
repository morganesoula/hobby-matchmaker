package com.msoula.movies.data.mapper

import com.msoula.database.data.local.MovieEntity
import com.msoula.movies.data.model.Movie
import com.msoula.movies.data.network.MoviePogo
import com.msoula.movies.domain.util.Mapper

class MapMovieEntityToMovie : Mapper<MovieEntity, Movie> {
    override fun map(entity: MovieEntity): Movie {
        return Movie(
            id = entity.id,
            title = entity.title,
            posterJPG = entity.remotePosterPath,
            localPosterPath = entity.localPosterPath,
            seen = entity.seen,
            isFavorite = entity.favourite,
        )
    }
}

class MapMovieToMovieEntity : Mapper<Movie, MovieEntity> {
    override fun map(entity: Movie): MovieEntity {
        return MovieEntity(
            id = entity.id,
            title = entity.title,
            remotePosterPath = entity.posterJPG,
            localPosterPath = entity.localPosterPath,
            favourite = entity.isFavorite,
            seen = entity.seen,
        )
    }
}

class MapMoviePogoToMovieEntity : Mapper<MoviePogo, MovieEntity> {
    override fun map(entity: MoviePogo): MovieEntity {
        return MovieEntity(
            id = entity.id,
            title = entity.title,
            remotePosterPath = entity.poster,
        )
    }
}
