package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.essenty.backhandler.BackHandlerOwner

interface MovieDetailComponent : BackHandlerOwner {
    val movieId: Long
}
