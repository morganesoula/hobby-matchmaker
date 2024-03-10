package com.msoula.movies.presentation

import com.msoula.movies.data.model.MovieUi
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MovieStateUI(
    val movies: PersistentList<MovieUi> = persistentListOf(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
