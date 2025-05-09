package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToFile

class ImageRepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher
) : ImageRepository {

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)
        updateMovie(localImagePath)
    }

    override suspend fun getRemoteImage(localPosterPath: String): String {
        return downloadImage(localPosterPath)
    }

    override suspend fun downloadImage(remotePosterPath: String): String {
        val imgPrefix = "https://image.tmdb.org/t/p/w500"
        val fullURL = "$imgPrefix$remotePosterPath"

        return try {
            val imageData = withContext(coroutineDispatcher) {
                NSData.dataWithContentsOfURL(NSURL.URLWithString(fullURL)!!)
            }

            if (imageData == null) {
                Logger.d("Failed to download image")
                ""
            } else {
                saveImageToLocal(imageData, remotePosterPath)
            }
        } catch (e: Exception) {
            Logger.e("Failed to download image")
            ""
        }
    }

    private fun saveImageToLocal(
        imageData: NSData,
        imageName: String
    ): String {
        return try {
            val cleanImageName = imageName.removePrefix("/")
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                directory = NSDocumentDirectory,
                domainMask = NSUserDomainMask,
                expandTilde = true
            ).first() as String

            val filePath = "$documentsPath/$cleanImageName"
            val fileManager = NSFileManager.defaultManager

            if (!fileManager.fileExistsAtPath(filePath)) {
                val success = imageData.writeToFile(filePath, atomically = true)
                if (success) {
                    Logger.d("Successfully saved image: $filePath")
                } else {
                    Logger.e("Writing failure: $filePath")
                    return ""
                }
            } else {
                Logger.d("File already exists: $filePath")
            }

            filePath
        } catch (e: Exception) {
            Logger.e("Exception while writing image: ${e.message}")
            ""
        }
    }
}
