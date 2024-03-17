package com.msoula.hobbymatchmaker.features.movies.domain.models

data class MovieDomainModel(
    val id: Long,
    val title: String,
    val coverFileName: String,
    val localCoverFilePath: String,
    val isFavorite: Boolean,
    val isSeen: Boolean,
) {
    companion object {
        const val DEFAULT_ID: Long = -1L
        const val DEFAULT_TITLE: String = ""
        const val DEFAULT_COVER_FILE_NAME: String = ""
        const val DEFAULT_LOCAL_COVER_FILE_PATH: String = ""
        const val DEFAULT_IS_FAVORITE: Boolean = false
        const val DEFAULT_IS_SEEN: Boolean = false
    }
}
