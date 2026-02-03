package com.msoula.hobbymatchmaker.core.database.models

data class UserCacheDataEntity(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = null,
    val avatarUrl: String? = null,
    val moviesLikedJson: String = "",
    val updatedAt: Long = 0L
) {
    companion object {
        val Initial = UserCacheDataEntity()
    }
}
