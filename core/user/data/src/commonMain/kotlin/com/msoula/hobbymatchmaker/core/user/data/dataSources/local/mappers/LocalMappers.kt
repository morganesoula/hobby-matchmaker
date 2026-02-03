package com.msoula.hobbymatchmaker.core.user.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.models.UserCacheDataEntity
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.serialization.json.Json
import kotlin.time.Clock

fun UserCacheDataEntity.toUserSummaryDomainModel(): UserSummaryDomainModel =
    UserSummaryDomainModel(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = Json.decodeFromString(this.moviesLikedJson)
    )

fun UserSummaryDomainModel.toUserCacheDataEntity(): UserCacheDataEntity =
    UserCacheDataEntity(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLikedJson = Json.encodeToString(this.moviesLiked),
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
