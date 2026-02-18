package com.msoula.hobbymatchmaker.features.movies.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
        releaseDate = this.releaseDate,
        isFavorite = this.isFavorite,
        title = this.title,
        overview = this.overview ?: "",
        note = this.note
    )
}

fun MovieUiModel.toCarouselItem(): MovieCarouselItem {
    return MovieCarouselItem(
        id = this.id,
        title = this.title,
        overview = this.overview,
        releaseDate = this.releaseDate,
        coverFilePath = this.coverFilePath,
        note = this.note,
        isFavorite = this.isFavorite,
        isShared = this.isShared
    )
}

fun ImmutableList<MovieUiModel>.toCarouselItems(): ImmutableList<MovieCarouselItem> {
    return map { it.toCarouselItem() }.toImmutableList()
}
