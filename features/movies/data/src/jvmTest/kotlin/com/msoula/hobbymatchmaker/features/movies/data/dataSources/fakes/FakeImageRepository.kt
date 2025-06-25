package com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository

class FakeImageRepository : ImageRepository {

    override suspend fun getRemoteImage(localPosterPath: String): String {
        val normalizedPath = localPosterPath.removePrefix("/")
        return "local/path/$normalizedPath"
    }

    override suspend fun downloadImage(remotePosterPath: String): String =
        "downloaded/$remotePosterPath"

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) = updateMovie("saved/$coverFileName")
}
