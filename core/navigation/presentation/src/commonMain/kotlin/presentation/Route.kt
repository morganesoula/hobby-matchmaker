package com.msoula.hobbymatchmaker.core.navigation.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSendCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherInbox
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
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    RECEIVED("invitation_received", "Received", FeatherInbox, "Received"),
    SENT("invitation_sent", "Sent", BootstrapSendCheck, "Sent")
}
