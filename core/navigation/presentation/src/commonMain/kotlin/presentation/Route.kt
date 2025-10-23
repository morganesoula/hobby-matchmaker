package presentation

interface Route {
    data object Splash : Route
    data object Auth : Route
    data object SignIn : Route
    data object SignUp : Route
    data object Movies : Route
    data class MovieDetail(val id: Long) : Route
}

fun Route.path(): String = when (this) {
    Route.Splash -> "splash"
    Route.Auth -> "auth"
    Route.SignIn -> "auth/signin"
    Route.SignUp -> "auth/signup"
    Route.Movies -> "movies"
    is Route.MovieDetail -> "movies/${id}"
    else -> ""
}

const val ARG_MOVIE_ID = "movieId"
const val MOVIE_DETAIL = "movies/{$ARG_MOVIE_ID}"

sealed class SplashResult {
    data object SkipAuth : SplashResult()
    data object ShowAuth : SplashResult()
}
