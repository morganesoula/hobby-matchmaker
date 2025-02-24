package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.net.URL

class ImageRepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
): ImageRepository {

    override suspend fun getRemoteImage(localPosterPath: String) = downloadImage(localPosterPath)

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)
        updateMovie(localImagePath)
    }

    override suspend fun downloadImage(remotePosterPath: String): String {
        val imgPrefix = "https://image.tmdb.org/t/p/w500"
        val response = CoroutineScope(coroutineDispatcher).async {
            val bitmap =
                BitmapFactory.decodeStream(URL(imgPrefix + remotePosterPath).openStream())
            if (bitmap == null) {
                //Log.e("HMM", "Bitmap is null into method downloadImage")
                println("Bitmap is null into method downloadImage")
                return@async ""
            }
            val savedImagePath = saveImageToLocal(bitmap, remotePosterPath)
            savedImagePath
        }.await()

        return response
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
