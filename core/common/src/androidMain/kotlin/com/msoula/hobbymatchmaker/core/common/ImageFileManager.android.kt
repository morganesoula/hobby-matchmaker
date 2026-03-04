package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import dev.gitlive.firebase.storage.Data
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.net.toUri

actual class ImageFileManager(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) {
    actual suspend fun copyImageToInternalStorage(sourceUri: String, fileName: String): String? =
        withContext(dispatcherProvider.io) {
            try {
                val avatarsDir = File(context.filesDir, "avatars")
                if (!avatarsDir.exists()) {
                    avatarsDir.mkdirs()
                }

                val destinationFile = File(avatarsDir, fileName)

                context.contentResolver.openInputStream(sourceUri.toUri())?.use { inputStream ->
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

    actual fun readFileData(filePath: String): Data? =
        runCatching { Data(File(filePath).readBytes()) }.getOrNull()
}
