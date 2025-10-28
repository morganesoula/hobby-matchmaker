package presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashViewModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieContent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileContent
import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    facebookUIClient: FacebookUIClient,
    socialClients: Map<ProviderType, SocialUIClient>
) {
    val nav = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = nav,
        startDestination = Splash
    ) {
        composable<Splash> {
            val splashViewModel = koinViewModel<SplashViewModel>()
            val state by splashViewModel.state.collectAsState()

            SplashScreenContent(
                state = state,
                redirectToAuth = {
                    nav.navigate(Auth) {
                        popUpTo<Splash> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                redirectToMovies = {
                    nav.navigate(Movies) {
                        popUpTo<Splash> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        navigation<Auth>(startDestination = SignIn) {
            composable<SignIn> {
                val signInViewModel = koinViewModel<SignInViewModel> {
                    parametersOf(socialClients)
                }

                val shouldShowGuestWarning by signInViewModel
                    .shouldShowGuestDialog
                    .collectAsState(initial = false)

                SignInScreenContent(
                    redirectToSignUpScreen = { nav.navigate(SignUp) },
                    redirectToMovieScreen = {
                        nav.navigate(Movies) {
                            popUpTo<Auth> { inclusive = true }
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

            composable<SignUp> {
                val signUpViewModel = koinViewModel<SignUpViewModel>()

                SignUpScreenContent(
                    oneTimeEventChannelFlow = signUpViewModel.oneTimeEventChannelFlow,
                    redirectToSignInScreen = { nav.popBackStack() },
                    redirectToMovieScreen = {
                        nav.navigate(Movies) {
                            popUpTo<Auth> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    signUpViewModel = signUpViewModel
                )
            }
        }

        composable<Movies> {
            val movieViewModel = koinViewModel<MovieViewModel>()
            val moviesState by movieViewModel.movieState.collectAsState()

            MovieContent(
                modifier = Modifier,
                movieViewModel = movieViewModel,
                movieState = moviesState,
                oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow,
                redirectToMovieDetail = { movieId -> nav.navigate(MovieDetail(movieId)) },
                redirectToAuth = {
                    nav.navigate(Auth) {
                        popUpTo<Movies> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                redirectToProfile = {
                    nav.navigate(Profile) {
                        popUpTo<Movies> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetail>().id
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

        composable<Profile> {
            val profileViewModel = koinViewModel<UserProfileViewModel>()
            val userProfileState by profileViewModel.currentUserProfileState.collectAsState()

            UserProfileContent(
                state = userProfileState,
                onEvent = profileViewModel::onEvent,
                navigateToSignUpScreen = {
                    nav.navigate(Auth) {
                        popUpTo<Profile> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
