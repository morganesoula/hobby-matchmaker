package com.msoula.hobbymatchmaker.core.navigation.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSendCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherInbox
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.icons.VscodeCodiconsLightbulb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("splash")
object Splash

@Serializable
@SerialName("auth")
object Auth

@Serializable
@SerialName("sign_in")
object SignIn

@Serializable
@SerialName("sign_up")
object SignUp

@Serializable
@SerialName("main")
object Main

@Serializable
@SerialName("hub")
object Hub

@Serializable
@SerialName("movies")
object Movies

@Serializable
@SerialName("movie_detail")
data class MovieDetail(val id: Long)

@Serializable
@SerialName("profile")
object Profile

@Serializable
@SerialName("social")
object Social

enum class Destination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    RECEIVED("Received", FeatherInbox, "Received"),
    SENT("Sent", BootstrapSendCheck, "Sent")
}

enum class MainTab(
    val route: Any,
    val label: String,
    val icon: ImageVector
) {
    MOVIES(Movies, "Movies", LucideFilm),
    HUB(Hub, "Hub", VscodeCodiconsLightbulb)
}
