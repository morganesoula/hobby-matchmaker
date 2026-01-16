package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

actual class ImageFileManager(private val context: Context) {
    actual suspend fun copyImageToInternalStorage(sourceUri: String, fileName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(sourceUri)

                val avatarsDir = File(context.filesDir, "avatars")
                if (!avatarsDir.exists()) {
                    avatarsDir.mkdirs()
                }

                val destinationFile = File(avatarsDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                destinationFile.absolutePath
            } catch (e: IOException) {
                Logger.e("Failed to copy image to internal storage", e)
                null
            } catch (e: SecurityException) {
                Logger.e("Security exception when copying image", e)
                null
            }
        }

    actual fun deleteImageFromInternalStorage(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("Failed to delete image from internal storage", e)
            false
        }
    }
}
