package com.msoula.hobbymatchmaker.features.movies.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel

fun MovieDomainModel.toMovieUiModel(): MovieUiModel {
    val local = this.localCoverFilePath
    val remote = this.coverFileName

    val resolved = when {
        local.startsWith("file://") -> local
        local.startsWith("/data/")
            || local.startsWith("/storage")
            || local.startsWith("/var")
            || local.startsWith("/private/var/") -> "file://$local"

        remote.startsWith("/") -> "https://image.tmdb.org/t/p/w500$remote"
        remote.startsWith("http") -> remote

        else -> ""
    }

    return MovieUiModel(
        id = this.id,
        coverFilePath = resolved,
        isFavorite = this.isFavorite,
        title = this.title,
        overview = this.overview ?: "",
        note = this.note
    )
}

fun MovieUiModel.toCarouselItem(): MovieCarouselItem {
    return MovieCarouselItem(
        id = id,
        title = title,
        overview = overview,
        coverFilePath = coverFilePath,
        note = note,
        isFavorite = isFavorite
    )
}

fun List<MovieUiModel>.toCarouselItems(): List<MovieCarouselItem> {
    return map { it.toCarouselItem() }
}
