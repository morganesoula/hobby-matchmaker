package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SaveLocalCoverPathUseCase(
    private val movieRepository: MovieRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(coverFileName: String, movieId: Long) {
        imageRepository.saveRemoteImageAndUpdateMovie(
            coverFileName = coverFileName,
        ) { localCoverFilePath ->
            try {
                movieRepository.updateMovieWithLocalCoverFilePath(
                    coverFileName = coverFileName,
                    localCoverFilePath = localCoverFilePath,
                    movieId = movieId
                )
            } catch (e: Exception) {
                Logger.e("Error updating data: $e")
                e.printStackTrace()
            }
        }
    }
}
