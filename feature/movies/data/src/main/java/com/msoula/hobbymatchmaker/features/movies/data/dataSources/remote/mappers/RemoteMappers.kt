package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_ID
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_IS_FAVORITE
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_IS_SEEN
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_LOCAL_COVER_FILE_PATH
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_OVERVIEW
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_TITLE

fun MovieRemoteModel.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.id?.toLong() ?: DEFAULT_ID,
        title = this.title ?: DEFAULT_TITLE,
        coverFileName = this.poster ?: DEFAULT_LOCAL_COVER_FILE_PATH,
        localCoverFilePath = DEFAULT_LOCAL_COVER_FILE_PATH,
        isFavorite = DEFAULT_IS_FAVORITE,
        isSeen = DEFAULT_IS_SEEN,
        overview = DEFAULT_OVERVIEW
    )
}
