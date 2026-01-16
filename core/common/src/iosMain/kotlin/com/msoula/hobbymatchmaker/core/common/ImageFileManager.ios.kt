package com.msoula.hobbymatchmaker.core.common

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL

@OptIn(ExperimentalForeignApi::class)
actual class ImageFileManager {
    actual suspend fun copyImageToInternalStorage(sourceUri: String, fileName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val fileManager = NSFileManager.defaultManager
                val documentsDirectory = fileManager.URLsForDirectory(
                    directory = platform.Foundation.NSDocumentDirectory,
                    inDomains = platform.Foundation.NSUserDomainMask
                ).firstOrNull() as? NSURL ?: return@withContext null

                val avatarsDirectory = documentsDirectory.URLByAppendingPathComponent("avatars")
                if (avatarsDirectory != null) {
                    fileManager.createDirectoryAtURL(
                        url = avatarsDirectory,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null
                    )
                }

                val sourceURL = NSURL(string = sourceUri)
                val imageData = NSData.dataWithContentsOfURL(sourceURL) ?: return@withContext null
                val destinationURL = avatarsDirectory?.URLByAppendingPathComponent(fileName)
                    ?: return@withContext null
                val success = imageData.writeToURL(url = destinationURL, atomically = true)

                if (success) {
                    destinationURL.path
                } else {
                    null
                }
            } catch (e: Exception) {
                Logger.e("Failed to copy image to internal storage", e)
                null
            }
        }

    actual fun deleteImageFromInternalStorage(filePath: String): Boolean {
        return try {
            val fileManager = NSFileManager.defaultManager
            val fileURL = NSURL.fileURLWithPath(filePath)
            fileManager.removeItemAtURL(URL = fileURL, error = null)
            true
        } catch (e: Exception) {
            Logger.e("Failed to delete image from internal storage", e)
            false
        }
    }
}
