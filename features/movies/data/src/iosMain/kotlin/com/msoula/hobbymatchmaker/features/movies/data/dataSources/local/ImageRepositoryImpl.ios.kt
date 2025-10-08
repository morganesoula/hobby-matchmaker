package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL

class ImageRepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher
) : ImageRepository {

    override suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ) {
        val localImagePath = downloadImage(coverFileName)
        localImagePath?.let { updateMovie(it) }
    }

    override suspend fun getRemoteImage(remotePosterPath: String): String? {
        return downloadImage(remotePosterPath)
    }

    override suspend fun downloadImage(remotePosterPath: String): String? {
        if (remotePosterPath.startsWith("file:", ignoreCase = true)) return remotePosterPath
        if (remotePosterPath.startsWith("/var/") || remotePosterPath.startsWith("/private/var/"))
            return "file://$remotePosterPath"

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
            Logger.e("Failed to download image: ${e.message}")
            ""
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveImageToLocal(
        imageData: NSData,
        imageName: String
    ): String? {
        return try {
            val cleanImageName = imageName.removePrefix("/")
            val fm = NSFileManager.defaultManager
            val baseDirUrl = fm.URLsForDirectory(NSCachesDirectory, NSUserDomainMask).first() as NSURL
            val imagesDirUrl = baseDirUrl.URLByAppendingPathComponent("Images", isDirectory = true)

            if (!fm.fileExistsAtPath(imagesDirUrl?.path!!)) {
                fm.createDirectoryAtPath(
                    imagesDirUrl.path!!,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            val fileUrl = imagesDirUrl.URLByAppendingPathComponent(cleanImageName, isDirectory = false)

            if (fm.fileExistsAtPath(fileUrl?.path!!)) {
                val attrs = fm.attributesOfItemAtPath(fileUrl.path!!, null)
                val size = (attrs?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
                if (size > 0L) {
                    val absolute = fileUrl.absoluteString
                    Logger.d("Image already cached: $absolute (size=$size)")
                    return absolute
                }
            }

            val ok = imageData.writeToURL(fileUrl, atomically = true)
            val exists = fm.fileExistsAtPath(fileUrl.path!!)
            val attrs = fm.attributesOfItemAtPath(fileUrl.path!!, null)
            val size = (attrs?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
            val absolute = fileUrl.absoluteString

            Logger.d("Saved ok=$ok exists=$exists size=$size at $absolute")

            if (!ok || !exists || size <= 0L) "" else absolute
        } catch (e: Exception) {
            Logger.e("Exception while writing image: ${e.message}")
            ""
        }
    }
}
