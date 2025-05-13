package com.msoula.hobbymatchmaker.core.navigation.domain

interface MovieDetailComponent {
    val movieId: Long
    fun onMovieDetailBackPressed(): () -> Unit
}
