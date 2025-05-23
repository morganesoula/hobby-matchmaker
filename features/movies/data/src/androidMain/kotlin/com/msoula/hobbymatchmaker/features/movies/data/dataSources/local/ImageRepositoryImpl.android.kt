package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.URL

class ImageRepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
): ImageRepository {

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)
        updateMovie(localImagePath)
    }

    override suspend fun getRemoteImage(localPosterPath: String) = downloadImage(localPosterPath)

    override suspend fun downloadImage(remotePosterPath: String): String {
        val imgPrefix = "https://image.tmdb.org/t/p/w500"
        val fullURL = "$imgPrefix$remotePosterPath"

        return try {
            val bitmap = withContext(coroutineDispatcher) {
                BitmapFactory.decodeStream(URL(fullURL).openStream())
            }

            if (bitmap == null) {
                Logger.e("Bitmap is null for URL: $fullURL")
                ""
            } else {
                saveImageToLocal(bitmap, remotePosterPath)
            }
        } catch (e: Exception) {
            Logger.e("Exception while downloading image: ${e.message}")
            ""
        }
    }

    private fun saveImageToLocal(
        bitmap: Bitmap,
        imageName: String,
    ): String {
        var absolutePath = ""
        try {
            val cleanImageName = imageName.removePrefix("/")
            absolutePath =
                if (context.getFileStreamPath(cleanImageName).exists()) {
                    context.getFileStreamPath(cleanImageName).absolutePath
                } else {
                    val outputStream = context.openFileOutput(cleanImageName, Context.MODE_PRIVATE)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    context.getFileStreamPath(cleanImageName).absolutePath
                }
        } catch (e: Exception) {
            Log.e("HMM", "Exception occurred while saving image: ${e.message}")
        }

        return absolutePath
    }
}
