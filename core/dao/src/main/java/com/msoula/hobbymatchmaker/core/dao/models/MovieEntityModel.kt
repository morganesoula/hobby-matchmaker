package com.msoula.hobbymatchmaker.core.dao.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntityModel(
    @PrimaryKey(autoGenerate = false) @ColumnInfo("id", defaultValue = "0") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val remotePosterPath: String,
    @ColumnInfo(name = "local_poster_path") val localPosterPath: String = "",
    @ColumnInfo(name = "favourite") val favourite: Boolean = false,
    @ColumnInfo(name = "already_seen") val seen: Boolean = false,
)
