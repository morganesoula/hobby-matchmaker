package presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieContent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    onFinishApp: () -> Unit,
    isConnected: Boolean,
    facebookUIClient: FacebookUIClient,
    socialClients: Map<ProviderType, SocialUIClient>
) {
    val nav = rememberNavController()

    LaunchedEffect(Unit) {
        if (isConnected) {
            nav.navigateAndReplaceAll(Route.Movies)
        } else {
            nav.navigateAndReplaceAll(Route.Auth)
        }
    }

    NavHost(
        modifier = modifier,
        navController = nav,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreenContent()
        }

        navigation(
            route = Route.Auth.path(),
            startDestination = Route.SignIn.path()
        ) {
            composable<Route.SignIn> {
                val signInViewModel = koinViewModel<SignInViewModel> {
                    parametersOf(socialClients)
                }

                val shouldShowGuestWarning by signInViewModel
                    .shouldShowGuestDialog
                    .collectAsState(initial = false)

                SignInScreenContent(
                    redirectToSignUpScreen = { nav.navigate(Route.SignUp) },
                    redirectToMovieScreen = {
                        nav.navigate(Route.Movies) {
                            popUpTo<Route.Auth> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    signInViewModel = signInViewModel,
                    resetSignInState = { signInViewModel.resetSignInState() },
                    oneTimeEventChannelFlow = signInViewModel.oneTimeEventChannelFlow,
                    facebookUIClient = facebookUIClient,
                    shouldShowGuestWarning = shouldShowGuestWarning
                )
            }

            composable<Route.SignUp> {
                val signUpViewModel = koinViewModel<SignUpViewModel>()

                SignUpScreenContent(
                    oneTimeEventChannelFlow = signUpViewModel.oneTimeEventChannelFlow,
                    redirectToSignInScreen = { nav.popBackStack() },
                    redirectToMovieScreen = {
                        nav.navigate(Route.Movies) {
                            popUpTo<Route.Auth> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    signUpViewModel = signUpViewModel
                )
            }
        }

        composable<Route.Movies> {
            BackHandler { onFinishApp() }

            val movieViewModel = koinViewModel<MovieViewModel>()
            val moviesState by movieViewModel.movieState.collectAsState()

            MovieContent(
                modifier = Modifier,
                movieViewModel = movieViewModel,
                movieState = moviesState,
                oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow,
                redirectToMovieDetail = { movieId -> nav.navigate(Route.MovieDetail(movieId)) },
                redirectToAuth = {
                    movieViewModel.logOut()
                    nav.navigate(Route.Auth) {
                        popUpTo<Route.Movies> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Route.MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<Route.MovieDetail>().id
            val movieDetailViewModel = koinViewModel<MovieDetailViewModel>(
                key = "movieDetail-$movieId",
                parameters = { parametersOf(movieId) }
            )
            val viewState by movieDetailViewModel.viewState.collectAsState()

            MovieDetailContent(
                oneTimeEventFlow = movieDetailViewModel.oneTimeEventChannelFlow,
                viewState = viewState,
                onPlayTrailerClicked = movieDetailViewModel::onEvent,
                onMovieDetailBackPressed = { nav.popBackStack() },
            )
        }
    }
}
