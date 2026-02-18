package com.msoula.hobbymatchmaker.core.navigation.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MinimalDialog
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
import com.msoula.hobbymatchmaker.core.design.models.MatchAnimationData
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
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
import com.msoula.hobbymatchmaker.features.hub.presentation.HubContent
import com.msoula.hobbymatchmaker.features.hub.presentation.HubViewModel
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
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.social.presentation.SocialContent
import com.msoula.hobbymatchmaker.features.social.presentation.SocialViewModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val VideoPlayerStateSaver = Saver<VideoPlayerState, List<Any>>(
    save = { state ->
        listOf(state.videoId, state.isVisible, state.isLoading)
    },
    restore = { list ->
        if (list.size >= 3) {
            VideoPlayerState(
                videoId = list[0] as? String ?: "",
                isVisible = list[1] as? Boolean ?: false,
                isLoading = list[2] as? Boolean ?: false
            )
        } else {
            VideoPlayerState.Initial
        }
    }
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(
    socialClients: SocialClients
) {
    val nav = rememberNavController()
    val navCallbacks = remember(nav) { NavigationCallbacks(nav) }
    val socialViewModel = koinViewModel<SocialViewModel>()

    NavHost(
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
                    facebookUIClient = requireNotNull(
                        (socialClients
                            .clients[ProviderType.FACEBOOK] as? FacebookUIClientImpl)
                            ?.facebookUIClient
                    ) {
                        "Facebook UI client is not configured in SocialClients"
                    }
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

        composable<Main> {
            MainScaffold(
                navCallbacks = navCallbacks
            )
        }

        composable<MovieDetail> { backStackEntry ->
            val movieId = backStackEntry.toRoute<MovieDetail>().id
            val movieDetailViewModel = koinViewModel<MovieDetailViewModel>(
                key = "movieDetail-$movieId",
                parameters = { parametersOf(movieId) }
            )

            val movieDetailState by movieDetailViewModel.movieDetailState.collectAsState()
            val snackBarHostState = remember { SnackbarHostState() }
            var videoPlayerState by rememberSaveable(stateSaver = VideoPlayerStateSaver) {
                mutableStateOf(VideoPlayerState.Initial)
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
                val currentState = movieDetailState
                if (currentState is UiState.Success<MovieDetailUiModel>) {
                    videoPlayerState = VideoPlayerState(
                        videoId = currentState.data.videoKey,
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
                retryObservation = { movieDetailViewModel.retryObservation() },
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
                        logOut = { userProfileViewModel.onEvent(UserProfileUiEventModel.OnSignUpButtonClicked) },
                        onNavigate = { destination ->
                            when (destination) {
                                NavigationDestination.SignUp -> navCallbacks.navigateAndClearToAuth()
                                NavigationDestination.Movies -> navCallbacks.navigateToMoviesFromProfile()
                                else -> Unit
                            }
                        },
                        onEvent = userProfileViewModel::onEvent,
                        onSearchPeople = { query ->
                            socialViewModel.onEvent(SocialUiEventModel.OnSearchPeople(query))
                        },
                        onInviteToSocialCircle = { pseudo, name ->
                            socialViewModel.onEvent(
                                SocialUiEventModel.OnInviteToSocialCircle(
                                    pseudo,
                                    name
                                )
                            )
                        }
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

                val mappedSearchResults = searchedPseudos.map { member ->
                    ProfileSocialMember(
                        uid = member.uid,
                        name = member.name,
                        pseudo = member.pseudo,
                        avatarUrl = member.avatarUrl,
                        commonMoviesCount = member.commonMoviesCount
                    )
                }.toImmutableList()

                UserProfileContent(
                    snackBarHostState = snackBarHostState,
                    searchedPseudos = mappedSearchResults,
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
            var showCapacityDialog by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                socialViewModel.events.collect { event ->
                    when (event) {
                        UiEvent.CapacityReached -> showCapacityDialog = true
                        else -> Unit
                    }
                }
            }

            if (showCapacityDialog) {
                MinimalDialog(
                    noDataText = ""
                ) {
                    showCapacityDialog = false
                }
            }

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

@Composable
fun MainScaffold(
    navCallbacks: NavigationCallbacks
) {
    val tabNavController = rememberNavController()
    val currentBackStack by tabNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    val selected = currentDestination
                        ?.route
                        ?.contains(tab.route::class.simpleName ?: "") == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            tabNavController.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = tabNavController,
            startDestination = Movies,
            modifier = Modifier.padding(padding)
        ) {
            composable<Movies> {
                val movieViewModel: MovieViewModel = koinViewModel()

                val movieState by movieViewModel.movieScreenState.collectAsState()
                val paginationState by movieViewModel.paginationState.collectAsState()

                val snackBarHostState = remember { SnackbarHostState() }

                var matchingAnimationData by remember { mutableStateOf(MatchAnimationData.Empty) }

                LaunchedEffect(Unit) {
                }

                var matchAnimVisibility by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(Unit) {
                    movieViewModel.events.collect { event ->
                        when (event) {
                            is UiEvent.Navigate ->
                                when (val dest = event.destination) {
                                    is NavigationDestination.MovieDetail -> navCallbacks.navigateToMovieDetailFromMoviesOrHub(
                                        dest.movieId
                                    )

                                    NavigationDestination.SignIn -> navCallbacks.navigateAndClearToAuth()
                                    else -> Unit
                                }

                            is UiEvent.ShowSnackBar ->
                                snackBarHostState.showSnackbar(event.message.asStringSuspend())

                            is UiEvent.ShowAnimation -> {
                                matchAnimVisibility = true
                                matchingAnimationData = event.data
                            }

                            else -> {}
                        }
                    }
                }

                MovieContent(
                    modifier = Modifier,
                    movieState = movieState,
                    paginationState = paginationState,
                    matchingAnimationData = matchingAnimationData,
                    onNavigate = { destination ->
                        when (destination) {
                            NavigationDestination.Profile -> navCallbacks.navigateToProfileFromMovies()
                            NavigationDestination.Social -> navCallbacks.navigateToSocialFromMovies()
                            else -> Unit
                        }
                    },
                    snackBarHostState = snackBarHostState,
                    showMatchAnimation = matchAnimVisibility,
                    observeMovies = movieViewModel::observeMovies,
                    onEvent = movieViewModel::onCardEvent,
                    onLoadMore = movieViewModel::loadMore,
                    resetAnimation = { matchAnimVisibility = false }
                )
            }

            composable<Hub> {
                val hubViewModel: HubViewModel = koinViewModel()
                val hubFavoriteMoviesState by hubViewModel.hubFavoriteMoviesState.collectAsState()
                val hubRecentMatchesState by hubViewModel.hubRecentMatchesState.collectAsState()

                HubContent(
                    hubFavoriteMovies = hubFavoriteMoviesState,
                    hubRecentMatches = hubRecentMatchesState,
                    observeFavoriteMovies = { hubViewModel.observeFavoriteMovies() },
                    observeRecentMatches = { hubViewModel.observeRecentMatches() },
                    navigateToMoviesScreen = { navCallbacks.navigateToMoviesFromHub() },
                    navigateToMovieDetail = { navCallbacks.navigateToMovieDetailFromMoviesOrHub(it) },
                    navigateToProfileScreen = { navCallbacks.navigateToProfileFromHub() }
                )
            }
        }
    }
}

