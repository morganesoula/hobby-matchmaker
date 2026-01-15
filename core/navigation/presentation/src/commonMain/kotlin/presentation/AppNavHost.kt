package com.msoula.hobbymatchmaker.core.navigation.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
import com.msoula.hobbymatchmaker.core.design.models.TabItem
import com.msoula.hobbymatchmaker.core.design.reset_password
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SocialClientsVM
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.navigation.presentation.models.SocialClients
import com.msoula.hobbymatchmaker.core.navigation.presentation.utils.NavigationCallbacks
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenContent
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashViewModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.VideoPlayerState
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieContent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileContent
import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileActions
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileState
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.social.presentation.SocialContent
import com.msoula.hobbymatchmaker.features.social.presentation.SocialViewModel
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val VideoPlayerStateSaver = Saver<VideoPlayerState, List<Any>>(
    save = { state ->
        listOf(state.videoId, state.isVisible, state.isLoading)
    },
    restore = { list ->
        VideoPlayerState(
            videoId = list[0] as String,
            isVisible = list[1] as Boolean,
            isLoading = list[2] as Boolean
        )
    }
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    socialClients: SocialClients
) {
    val nav = rememberNavController()
    val navCallbacks = remember(nav) { NavigationCallbacks(nav) }
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
                redirectToAuth = navCallbacks.navigateToAuthFromSplash,
                redirectToMovies = navCallbacks.navigateToMoviesFromSplash
            )
        }

        navigation<Auth>(startDestination = SignIn) {
            composable<SignIn> {
                val signInViewModel = koinViewModel<SignInViewModel> {
                    parametersOf(SocialClientsVM(socialClients.clients))
                }

                val signInState by signInViewModel.signInState.collectAsState()
                val formState by signInViewModel.formDataFlow.collectAsState()
                val dontAskCheckbox by signInViewModel.dontAskCheckboxValue.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    signInViewModel.events.collect { event ->
                        when (event) {
                            is UiEvent.Navigate -> {
                                when (event.destination) {
                                    NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth()
                                    NavigationDestination.SignUp -> nav.navigate(SignUp)
                                    else -> Unit
                                }
                            }

                            is UiEvent.ShowSnackBar -> {
                                snackbarHostState.showSnackbar(
                                    event.message.asStringSuspend()
                                )
                            }

                            is UiEvent.CloseDialog -> {
                                when (event.dialogName) {
                                    "reset_password" ->
                                        snackbarHostState.showSnackbar(
                                            getString(Res.string.reset_password)
                                        )
                                }
                            }

                            else -> Unit
                        }
                    }
                }

                SignInScreenContent(
                    onNavigate = { destination ->
                        when (destination) {
                            NavigationDestination.SignUp -> nav.navigate(SignUp)
                            else -> Unit
                        }
                    },
                    signInState = signInState,
                    formState = formState,
                    dontAskCheckbox = dontAskCheckbox,
                    onEvent = signInViewModel::onEvent,
                    snackBarHostState = snackbarHostState,
                    facebookUIClient = (socialClients.clients[ProviderType.FACEBOOK] as FacebookUIClientImpl).facebookUIClient
                )
            }

            composable<SignUp> {
                val signUpViewModel = koinViewModel<SignUpViewModel>()

                val registrationState by signUpViewModel.formDataFlow.collectAsState()
                val signUpState by signUpViewModel.signUpState.collectAsState()

                val snackBarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    signUpViewModel.events.collect { event ->
                        when (event) {
                            is UiEvent.ShowSnackBar ->
                                snackBarHostState.showSnackbar(event.message.asStringSuspend())

                            is UiEvent.Navigate -> {
                                when (event.destination) {
                                    NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromAuth()
                                    NavigationDestination.SignIn -> nav.popBackStack()
                                    else -> Unit
                                }
                            }

                            else -> Unit
                        }
                    }
                }

                SignUpScreenContent(
                    registrationState = registrationState,
                    signUpState = signUpState,
                    snackBarHostState = snackBarHostState,
                    onEvent = signUpViewModel::onEvent,
                    onNavigateToSignIn = { nav.popBackStack() }
                )
            }
        }

        composable<Movies> {
            val movieViewModel: MovieViewModel = koinViewModel()

            val movieState by movieViewModel.screenState.collectAsState()
            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                movieViewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.Navigate ->
                            when (val dest = event.destination) {
                                is NavigationDestination.MovieDetail -> nav.navigate(
                                    MovieDetail(
                                        dest.movieId
                                    )
                                )

                                NavigationDestination.SignIn -> navCallbacks.navigateAndClearToAuth()
                                else -> Unit
                            }

                        is UiEvent.ShowSnackBar ->
                            snackBarHostState.showSnackbar(event.message.asStringSuspend())

                        else -> {}
                    }
                }
            }

            MovieContent(
                modifier = Modifier,
                movieState = movieState,
                onNavigate = { destination ->
                    when (destination) {
                        NavigationDestination.Profile -> nav.navigate(Profile)
                        NavigationDestination.Social -> nav.navigate(Social)
                        else -> Unit
                    }
                },
                snackBarHostState = snackBarHostState,
                observeMovies = movieViewModel::observeMovies,
                onEvent = movieViewModel::onCardEvent
            )
        }

        composable<MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetail>().id
            val movieDetailViewModel = koinViewModel<MovieDetailViewModel>(
                key = "movieDetail-$movieId",
                parameters = { parametersOf(movieId) }
            )

            val movieDetailState by movieDetailViewModel.screenState.collectAsState()
            val snackBarHostState = remember { SnackbarHostState() }
            var videoPlayerState by rememberSaveable(stateSaver = VideoPlayerStateSaver) {
                mutableStateOf(VideoPlayerState())
            }

            LaunchedEffect(Unit) {
                movieDetailViewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar ->
                            snackBarHostState.showSnackbar(event.message.asStringSuspend())

                        is UiEvent.OnDataReady -> {
                            videoPlayerState = videoPlayerState.copy(
                                videoId = event.data,
                                isVisible = true
                            )
                        }

                        else -> {}
                    }
                }
            }

            LaunchedEffect(Unit) {
                if (movieDetailState is UiState.Success) {
                    videoPlayerState = VideoPlayerState(
                        videoId = (movieDetailState as UiState.Success<MovieDetailUiModel>).data.videoKey,
                        isVisible = false,
                        isLoading = false
                    )
                }
            }

            MovieDetailContent(
                onNavigate = { destination ->
                    when (destination) {
                        NavigationDestination.Movies -> nav.popBackStack()
                        else -> return@MovieDetailContent
                    }
                },
                movieDetailState = movieDetailState,
                snackBarHostState = snackBarHostState,
                videoPlayerState = videoPlayerState,
                onVideoPlayerDismissed = {
                    videoPlayerState = videoPlayerState.copy(isVisible = false)
                },
                observeMovieDetail = { movieDetailViewModel.observeMovieDetail() },
                onEvent = movieDetailViewModel::onEvent
            )
        }

        composable<Profile> {
            val userProfileViewModel = koinViewModel<UserProfileViewModel>()
            val snackBarHostState = remember { SnackbarHostState() }

            val profileState by userProfileViewModel.screenState.collectAsState()
            val isEditMode by userProfileViewModel.isEditMode.collectAsState()
            val editableProfile by userProfileViewModel.editableProfile.collectAsState()
            val isPseudoAvailable by userProfileViewModel.isPseudoAvailable.collectAsState()

            val searchedPseudos by socialViewModel.searchResults.collectAsState()
            val profile = (profileState as? UserProfileUiStateModel.Success)?.userProfile

            LaunchedEffect(Unit) {
                userProfileViewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar ->
                            snackBarHostState.showSnackbar(event.message.asStringSuspend())

                        is UiEvent.Navigate -> {
                            when (event.destination) {
                                NavigationDestination.SignIn -> navCallbacks.navigateAndClearToAuth()
                                else -> Unit
                            }
                        }

                        is UiEvent.OnDataReady -> {
                            when (event.data) {
                                "profile_updated" -> userProfileViewModel.closeEdition()
                            }
                        }

                        else -> Unit
                    }
                }
            }

            if (profile != null) {
                val userProfileActions = remember(userProfileViewModel, socialViewModel) {
                    UserProfileActions(
                        closeEdition = { userProfileViewModel.closeEdition() },
                        logOut = { userProfileViewModel.logOut(it) },
                        onNavigate = { destination ->
                            when (destination) {
                                NavigationDestination.SignUp -> navCallbacks.navigateAndClearToAuth()
                                NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromProfile()
                                else -> Unit
                            }
                        },
                        onEvent = userProfileViewModel::onEvent,
                        onSocialEvent = socialViewModel::onEvent
                    )
                }

                val userProfileState = remember(
                    profileState, editableProfile, isEditMode, isPseudoAvailable
                ) {
                    UserProfileState(
                        profileState = profileState,
                        editableProfile = editableProfile,
                        isEditMode = isEditMode,
                        isPseudoAvailable = isPseudoAvailable
                    )
                }

                UserProfileContent(
                    snackBarHostState = snackBarHostState,
                    searchedPseudos = searchedPseudos,
                    profile = profile,
                    userProfileState = userProfileState,
                    userProfileActions = userProfileActions
                )
            }
        }

        composable<Social> {
            val socialTabs = remember {
                listOf(
                    Destination.RECEIVED,
                    Destination.SENT
                ).map { destination ->
                    TabItem(
                        label = destination.label,
                        icon = destination.icon,
                        contentDescription = destination.contentDescription
                    )
                }.toImmutableList()
            }

            val sentInvites by socialViewModel.sentInvites.collectAsState()
            val incomingInvites by socialViewModel.incomingInvites.collectAsState()

            SocialContent(
                sentInvites = sentInvites,
                incomingInvites = incomingInvites,
                tabs = socialTabs,
                observeSessionAndInvites = {
                    socialViewModel.observeSessionAndInvites()
                },
                onEvent = socialViewModel::onEvent
            )
        }
    }
}
