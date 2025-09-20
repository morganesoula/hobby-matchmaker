package com.msoula.hobbymatchmaker.tests.helpers

import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.ActorResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.GenreResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideoResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel

object MovieDetailTestFixtures {
    fun detail(id: Int = 603692) = MovieDetailResponseRemoteModel(
        id = id,
        genres = listOf(GenreResponseRemoteModel(28, "Action")),
        originalTitle = "Original FR Title",
        overview = "Un résumé de tests.",
        popularity = 123.4,
        releaseDate = "2021-05-12",
        status = "Released",
        title = "Titre FR",
        duration = 118
    )

    fun cast(vararg pairs: Pair<Int, String>) = CastResponseRemoteModel(
        cast = pairs.mapIndexed { index, p ->
            ActorResponseRemoteModel(
                id = p.first,
                name = p.second,
                character = "Role #$index"
            )
        }
    )

    fun videosFR(key: String = "abc123") = MovieVideosResponseRemoteModel(
        id = 603692,
        results = listOf(
            MovieVideoResponseRemoteModel(
                type = "Trailer",
                key = key,
                site = "YouTube"
            )
        )
    )

    fun videosEN(key: String = "enKey") = MovieVideosResponseRemoteModel(
        id = 603692,
        results = listOf(
            MovieVideoResponseRemoteModel(
                type = "Trailer",
                key = key,
                site = "YouTube"
            )
        )
    )
}
