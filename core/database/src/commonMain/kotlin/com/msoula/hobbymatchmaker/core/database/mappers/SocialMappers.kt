package com.msoula.hobbymatchmaker.core.database.mappers

import com.msoula.hobbymatchmaker.core.database.User_cache
import com.msoula.hobbymatchmaker.core.database.models.UserCacheDataEntity

fun User_cache.toUserCacheDataEntity(): UserCacheDataEntity =
    UserCacheDataEntity(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatar_url,
        moviesLikedJson = this.movies_liked_json,
        updatedAt = this.updated_at
    )
