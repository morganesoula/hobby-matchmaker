package com.msoula.hobbymatchmaker.core.database.dao.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntityModel(
    @PrimaryKey(autoGenerate = false) @ColumnInfo("movieId", defaultValue = "0") val movieId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") val remotePosterPath: String,
    @ColumnInfo(name = "local_poster_path") val localPosterPath: String = "",
    @ColumnInfo(name = "favourite") val favourite: Boolean = false,
    @ColumnInfo(name = "already_seen") val seen: Boolean = false,
    // Filled when fetching movie detail
    @ColumnInfo("genre") val genre: List<String>? = emptyList(),
    @ColumnInfo("release_date") val releaseDate: String? = "",
    @ColumnInfo("overview") val overview: String? = "",
    @ColumnInfo("status") val status: String? = "",
    @ColumnInfo("popularity") val popularity: Double? = 0.0,
    @ColumnInfo("video_key") val videoUri: String? = ""
)
