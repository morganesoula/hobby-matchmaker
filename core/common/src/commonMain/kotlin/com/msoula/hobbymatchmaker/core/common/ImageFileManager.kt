package com.msoula.hobbymatchmaker.core.common

import dev.gitlive.firebase.storage.Data

expect class ImageFileManager {
    suspend fun copyImageToInternalStorage(sourceUri: String, fileName: String): String?
    fun deleteImageFromInternalStorage(filePath: String): Boolean
    fun readFileData(filePath: String): Data?
}
