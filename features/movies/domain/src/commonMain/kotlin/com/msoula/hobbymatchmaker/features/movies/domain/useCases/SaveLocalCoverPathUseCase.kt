package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SaveLocalCoverPathUseCase(
    private val movieRepository: MovieRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(coverFileName: String) {
        imageRepository.saveRemoteImageAndUpdateMovie(
            coverFileName = coverFileName,
        ) { localCoverFilePath ->
            try {
                movieRepository.updateMovieWithLocalCoverFilePath(
                    coverFileName = coverFileName,
                    localCoverFilePath = localCoverFilePath
                )
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e("HMM", "Error updating data: $e")
                println("Error updating data: $e")
            }
        }
    }
}
