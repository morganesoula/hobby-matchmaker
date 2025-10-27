package presentation

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
