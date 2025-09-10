package com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository

class FakeImageRepository(
    private val behavior: (String) -> String
) : ImageRepository {

    override suspend fun getRemoteImage(localPosterPath: String): String =
        behavior(localPosterPath)

    override suspend fun downloadImage(remotePosterPath: String): String =
        behavior(remotePosterPath)

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) = updateMovie(behavior(coverFileName))
}

fun movie(id: Long, title: String, poster: String?) =
    MovieRemoteModel(id = id.toInt(), title = title, poster = poster)

fun page(vararg items: MovieRemoteModel) =
    MovieResponseRemoteModel(results = items.toList())
