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
            posterJPG = entity.posterPath
        )
    }

}

class MapMoviePogoToMovieEntity : Mapper<MoviePogo, MovieEntity> {
    override fun map(entity: MoviePogo): MovieEntity {
        return MovieEntity(
            id = entity.id,
            title = entity.title,
            posterPath = entity.poster
        )
    }
}
