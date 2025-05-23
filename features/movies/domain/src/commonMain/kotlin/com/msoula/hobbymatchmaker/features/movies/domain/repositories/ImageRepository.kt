package com.msoula.hobbymatchmaker.features.movies.domain.repositories

interface ImageRepository {
    suspend fun getRemoteImage(localPosterPath: String): String
    suspend fun downloadImage(remotePosterPath: String): String
    suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    )
}
