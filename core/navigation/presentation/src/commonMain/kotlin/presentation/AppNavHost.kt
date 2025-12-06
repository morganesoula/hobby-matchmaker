package com.msoula.hobbymatchmaker.core.navigation.presentation

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
import com.msoula.hobbymatchmaker.core.design.models.TabItem
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
import com.msoula.hobbymatchmaker.features.social.presentation.SocialContent
import com.msoula.hobbymatchmaker.features.social.presentation.SocialViewModel
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
    val socialViewModel = koinViewModel<SocialViewModel>()

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

                SignInScreenContent(
                    onNavigate = { route ->
                        when (route) {
                            "movies" -> nav.navigate(Movies) {
                                popUpTo<Auth> { inclusive = true }
                                launchSingleTop = true
                            }

                            "sign_up" -> nav.navigate(SignUp)
                        }
                    },
                    signInViewModel = signInViewModel,
                    facebookUIClient = facebookUIClient
                )
            }

            composable<SignUp> {
                val signUpViewModel = koinViewModel<SignUpViewModel>()

                SignUpScreenContent(
                    signUpViewModel = signUpViewModel,
                    onNavigate = { route ->
                        when (route) {
                            "movies" -> {
                                nav.navigate(Movies) {
                                    popUpTo<Auth> { inclusive = true }
                                    launchSingleTop = true
                                }
                            }

                            "sign_in" -> {
                                nav.popBackStack()
                            }
                        }
                    }
                )
            }
        }

        composable<Movies> {
            val movieViewModel: MovieViewModel = koinViewModel()

            MovieContent(
                modifier = Modifier,
                movieViewModel = movieViewModel,
                onNavigate = { route ->
                    when (route) {
                        "sign_in" -> nav.navigate(SignIn) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }

                        "profile" -> nav.navigate(Profile)
                        "social" -> nav.navigate(Social)
                        else -> return@MovieContent
                    }
                },
                onNavigateToDetail = { movieId -> nav.navigate(MovieDetail(movieId)) }
            )
        }

        composable<MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetail>().id
            val movieDetailViewModel = koinViewModel<MovieDetailViewModel>(
                key = "movieDetail-$movieId",
                parameters = { parametersOf(movieId) }
            )

            MovieDetailContent(
                movieDetailViewModel = movieDetailViewModel,
                onNavigate = { route ->
                    when (route) {
                        "movies" -> nav.popBackStack()
                        else -> return@MovieDetailContent
                    }
                }
            )
        }

        composable<Profile> {
            val userProfileViewModel = koinViewModel<UserProfileViewModel>()

            UserProfileContent(
                profileViewModel = userProfileViewModel,
                socialViewModel = socialViewModel,
                onNavigate = { route ->
                    when (route) {
                        "sign_in" -> {
                            nav.navigate(Auth) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        "sign_up" -> {
                            nav.navigate(Auth) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        "movies" -> {
                            nav.navigate(Movies) {
                                popUpTo<Profile> { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable<Social> {
            val socialTabs = listOf(
                Destination.RECEIVED,
                Destination.SENT
            ).map { destination ->
                TabItem(
                    label = destination.label,
                    icon = destination.icon,
                    contentDescription = destination.contentDescription
                )
            }

            SocialContent(
                socialViewModel = socialViewModel,
                tabs = socialTabs,
                onNavigate = { route ->
                    when (route) {
                        "movies" -> {
                            nav.navigate(Movies) {
                                popUpTo<Social> { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}
