package com.msoula.hobbymatchmaker.core.common

expect class ImageFileManager {
    suspend fun copyImageToInternalStorage(sourceUri: String, fileName: String): String?

    fun deleteImageFromInternalStorage(filePath: String): Boolean
}
