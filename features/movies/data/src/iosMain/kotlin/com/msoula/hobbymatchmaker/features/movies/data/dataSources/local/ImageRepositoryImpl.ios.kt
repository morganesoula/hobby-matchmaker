package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import io.ktor.util.date.getTimeMillis
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
    ): AppResult<Unit, AppError> {
        downloadImage(coverFileName)?.let { updateMovie(it) }
        return AppResult.Success(Unit)
    }

    override suspend fun getRemoteImage(remotePosterPath: String): String? {
        return downloadImage(remotePosterPath)
    }

    override suspend fun downloadImage(remotePosterPath: String): String? {
        val raw = remotePosterPath.trim()
        if (raw.isBlank()) return null

        if (raw.startsWith("file://", ignoreCase = true)) return raw
        if (raw.startsWith("/var/") || raw.startsWith("/private/var/"))
            return "file://$raw"

        val base = "https://image.tmdb.org/t/p/w500"
        val fullURL = if (raw.startsWith("http", ignoreCase = true)) raw else "$base$raw"

        return try {
            val imageData = withContext(coroutineDispatcher) {
                NSData.dataWithContentsOfURL(NSURL.URLWithString(fullURL)!!)
            }

            if (imageData == null) {
                Logger.d("Failed to download image")
                null
            } else {
                val imageName = when {
                    raw.startsWith("http", ignoreCase = true) -> raw.substringAfterLast('/')
                        .substringBefore('?')

                    raw.startsWith("/") -> raw.removePrefix("/")
                    else -> raw
                }.ifBlank { "poster_${getTimeMillis()}.jpg" }

                saveImageToLocal(imageData, imageName)
            }
        } catch (e: Exception) {
            Logger.e("Failed to download image: ${e.message}")
            null
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
            val baseDirUrl =
                fm.URLsForDirectory(NSCachesDirectory, NSUserDomainMask).first() as NSURL
            val imagesDirUrl = baseDirUrl.URLByAppendingPathComponent("Images", isDirectory = true)

            if (!fm.fileExistsAtPath(imagesDirUrl?.path!!)) {
                fm.createDirectoryAtPath(
                    imagesDirUrl.path!!,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            val fileUrl =
                imagesDirUrl.URLByAppendingPathComponent(cleanImageName, isDirectory = false)

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
            if (!ok || !exists || size <= 0L) null else absolute
        } catch (e: Exception) {
            Logger.e("Exception while writing image: ${e.message}")
            null
        }
    }
}
