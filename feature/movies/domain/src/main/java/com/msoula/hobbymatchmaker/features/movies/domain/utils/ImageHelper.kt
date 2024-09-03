package com.msoula.hobbymatchmaker.features.movies.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.net.URL

private const val IMG_PREFIX = "https://image.tmdb.org/t/p/w500"

class ImageHelper constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context,
) {
    suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)

        updateMovie(localImagePath)
    }

    suspend fun getRemoteImage(coverFileName: String): String = downloadImage(coverFileName)

    private suspend fun downloadImage(remotePosterPath: String): String {
        val response =
            CoroutineScope(coroutineDispatcher).async {
                val bitmap =
                    BitmapFactory.decodeStream(URL(IMG_PREFIX + remotePosterPath).openStream())
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
            e.printStackTrace()
            Log.e("HMM", "Exception occurred while saving image: ${e.message}")
        }

        return absolutePath
    }
}
