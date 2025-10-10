package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ImageRepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
) : ImageRepository {

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)
        localImagePath?.let { updateMovie(it) }
    }

    override suspend fun getRemoteImage(remotePosterPath: String) = downloadImage(remotePosterPath)

    override suspend fun downloadImage(remotePosterPath: String): String? {
        var imageName = ""

        val raw = remotePosterPath.trim()
        if (raw.isBlank()) return null

        if (raw.startsWith("/data/") || raw.startsWith("/storage/")) return raw

        val imgPrefix = "https://image.tmdb.org/t/p/w500"
        val fullURL = "$imgPrefix$raw"

        return try {
            val bitmap = withContext(coroutineDispatcher) {
                val connexion = (URL(fullURL).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    instanceFollowRedirects = true
                }

                connexion.inputStream.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }

            if (bitmap == null) {
                Logger.e("Bitmap is null for URL: $fullURL")
                null
            } else {
                imageName = if (raw.isBlank()) {
                    "poster_${System.currentTimeMillis()}.jpg"
                } else {
                    if (raw.startsWith("/")) raw.removePrefix("/") else raw
                }
            }

            saveImageToLocal(bitmap, imageName)
        } catch (e: Exception) {
            Logger.e("Exception while downloading image: ${e.message}")
            null
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
