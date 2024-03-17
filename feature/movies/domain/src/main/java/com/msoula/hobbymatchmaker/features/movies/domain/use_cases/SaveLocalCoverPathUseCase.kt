package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import android.util.Log
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import javax.inject.Inject

class SaveLocalCoverPathUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val imageHelper: ImageHelper
) {
    suspend operator fun invoke(coverFileName: String) {
        imageHelper.saveRemoteImageAndUpdateMovie(
            coverFileName = coverFileName,
        ) { localCoverFilePath ->
            try {
                movieRepository.updateMovieWithLocalCoverFilePath(
                    coverFileName = coverFileName,
                    localCoverFilePath = localCoverFilePath
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HMM", "Error updating data: $e")
            }
        }
    }
}
