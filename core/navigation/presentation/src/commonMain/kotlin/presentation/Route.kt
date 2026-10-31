package com.msoula.hobbymatchmaker.core.navigation.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSendCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherInbox
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.icons.VscodeCodiconsLightbulb
import com.msoula.hobbymatchmaker.core.design.social_tab_received
import com.msoula.hobbymatchmaker.core.design.social_tab_sent
import org.jetbrains.compose.resources.StringResource
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
@SerialName("pick_pseudo")
object PickPseudo

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
    val label: StringResource,
    val icon: ImageVector
) {
    RECEIVED(Res.string.social_tab_received, FeatherInbox),
    SENT(Res.string.social_tab_sent, BootstrapSendCheck)
}

enum class MainTab(
    val route: Any,
    val label: String,
    val icon: ImageVector
) {
    MOVIES(Movies, "Movies", LucideFilm),
    HUB(Hub, "Hub", VscodeCodiconsLightbulb)
}
