package com.msoula.movies.presentation

import com.msoula.movies.data.model.Movie

data class MovieStateUI(
    val movies: List<Movie> = mutableListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
