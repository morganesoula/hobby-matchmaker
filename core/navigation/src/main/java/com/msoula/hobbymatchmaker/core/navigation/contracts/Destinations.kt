package com.msoula.hobbymatchmaker.core.navigation.contracts

import cafe.adriel.voyager.core.screen.Screen
import kotlinx.serialization.Serializable

sealed class Destinations {
    @Serializable
    object Auth {
        @Serializable
        object SignIn

        @Serializable
        object SignUp
    }

    @Serializable
    object Main {
        @Serializable
        object App

        @Serializable
        data class MovieDetail(val movieId: Long = -1L)
    }

    @Serializable
    object Splash {
        @Serializable
        object SplashScreen
    }
}
