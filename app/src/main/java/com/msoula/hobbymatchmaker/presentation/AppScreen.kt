package com.msoula.hobbymatchmaker.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msoula.hobbymatchmaker.core.design.component.CenteredTopBarComponent
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.features.movies.presentation.EmptyMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.ErrorMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import kotlinx.coroutines.flow.Flow
import com.msoula.hobbymatchmaker.R.string as StringRes

@Composable
fun AppScreen(
    redirectToMovieDetail: (movieId: Long) -> Unit,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel = hiltViewModel<MovieViewModel>()
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenteredTopBarComponent(
                title = stringResource(id = StringRes.home_route_name),
                canNavigateBack = false,
                logOut = appViewModel::logOut
            )
        },
    ) { paddingValues ->
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val viewState by movieViewModel.movieState.collectAsStateWithLifecycle()

            AppContent(
                viewState = viewState,
                onCardEvent = movieViewModel::onCardEvent,
                redirectToMovieDetail = redirectToMovieDetail,
                oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow
            )
        }
    }
}

@Composable
fun AppContent(
    viewState: MovieUiStateModel,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (movieId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onCardEvent: (CardEventModel) -> Unit
) {
    when (viewState) {
        is MovieUiStateModel.Loading -> LoadingCircularProgress()
        is MovieUiStateModel.Empty -> EmptyMovieScreen(modifier = modifier)
        is MovieUiStateModel.Success -> {
            MovieScreen(
                modifier = modifier,
                movies = viewState.list,
                onCardEvent = onCardEvent,
                redirectToMovieDetail = redirectToMovieDetail,
                oneTimeEventChannelFlow = oneTimeEventChannelFlow
            )
        }

        is MovieUiStateModel.Error ->
            ErrorMovieScreen(
                modifier = modifier,
                error = viewState.errorMessage
            )
    }
}
