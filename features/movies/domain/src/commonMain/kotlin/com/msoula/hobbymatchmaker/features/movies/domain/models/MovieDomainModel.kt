package com.msoula.hobbymatchmaker.features.movies.domain.models

data class MovieDomainModel(
    var id: Long = 0L,
    var title: String = "",
    var coverFileName: String = "",
    var localCoverFilePath: String = "",
    var isFavorite: Boolean = false,
    var isSeen: Boolean = false,
    var overview: String? = null
) {
    companion object {
        const val DEFAULT_ID: Long = -1L
        const val DEFAULT_TITLE: String = ""
        const val DEFAULT_COVER_FILE_NAME: String = ""
        const val DEFAULT_LOCAL_COVER_FILE_PATH: String = ""
        const val DEFAULT_IS_FAVORITE: Boolean = false
        const val DEFAULT_IS_SEEN: Boolean = false
        const val DEFAULT_OVERVIEW: String = ""
    }
}
