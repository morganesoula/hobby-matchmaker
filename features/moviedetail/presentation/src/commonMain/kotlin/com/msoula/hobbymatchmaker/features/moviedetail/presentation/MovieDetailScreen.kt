package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.ExpandableTextComponent
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.component.LocalSnackBar
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieDetailContent(
    viewState: MovieDetailViewStateModel,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit
) {
    when (viewState) {
        is MovieDetailViewStateModel.Error -> ErrorMovieDetailScreen(error = viewState.error)
        is MovieDetailViewStateModel.Loading -> LoadingCircularProgress()
        is MovieDetailViewStateModel.Empty -> EmptyMovieDetailScreen()
        is MovieDetailViewStateModel.Success ->
            MovieDetailScreen(
                oneTimeEventFlow = oneTimeEventFlow,
                movie = viewState.movie,
                onPlayTrailerClicked = onPlayTrailerClicked
            )
    }
}

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    movie: MovieDetailUiModel,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit
) {
    Scaffold {
        MovieDetailContentScreen(
            movie = movie,
            modifier = modifier.padding(it),
            onPlayTrailerClicked = onPlayTrailerClicked,
            oneTimeEventFlow = oneTimeEventFlow
        )
    }
}

@Composable
fun MovieDetailContentScreen(
    modifier: Modifier = Modifier,
    movie: MovieDetailUiModel,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val errorFetchingMovieMessage = stringResource(Res.string.no_trailer_available)

    val videoPlayerVisibility = remember { mutableStateOf(false) }
    val movieVideoUri = remember { mutableStateOf(movie.videoKey) }
    val isLoading = remember { mutableStateOf(false) }
    val exitFullScreen = remember { mutableStateOf(false) }

    val castMaxIndex = 5
    val verticalGradientHeight = 0.8f

    val cast = buildAnnotatedString {
        movie.cast.asIterable().take(6).forEachIndexed { index, (name, role) ->
            append(name)
            append(" (")
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append(role)
            }
            append(")")

            if (index < castMaxIndex) {
                append(", ")
            }
        }
    }

    ObserveAsEvents(flow = oneTimeEventFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is MovieDetailUiEventModel.OnMovieDetailUiFetchedError -> {
                    snackBarHostState.showSnackbar(event.error)
                }

                is MovieDetailUiEventModel.OnPlayMovieTrailerReady -> {
                    if (isLoading.value) {
                        isLoading.value = false
                    }

                    movieVideoUri.value = event.movieUri
                    videoPlayerVisibility.value = true
                }

                is MovieDetailUiEventModel.ErrorFetchingTrailer -> {
                    if (isLoading.value) {
                        isLoading.value = false
                    }

                    snackBarHostState.showSnackbar(message = errorFetchingMovieMessage + { event.message })
                }

                is MovieDetailUiEventModel.LoadingTrailer -> {
                    isLoading.value = true
                }

                else -> Unit
            }
        }
    }

    if (isLoading.value) {
        LoadingCircularProgress()
    }

    if (exitFullScreen.value) {
        ExitFullScreen()
    }

    CompositionLocalProvider(value = LocalSnackBar provides snackBarHostState) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            //Background image
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(movie.posterPath.toPath())
                    .build(),
                contentDescription = "poster",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Filter to darken background
            BackgroundGradient(verticalGradientHeight)

            //Content on top of the image
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .verticalScroll(rememberScrollState())
                        .padding(top = 250.dp, start = 16.dp, end = 16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = movie.status,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .background(
                                color = Color.Green.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp, 2.dp, 8.dp, 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(text = movie.releaseDate)
                        Text(text = " · " + movie.genre.toString().removeSurrounding("[", "]"))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onPlayTrailerClicked(
                                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                                    movieId = movie.id,
                                    isVideoURIknown = movie.videoKey.isNotEmpty()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 4.dp, end = 4.dp)
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(Res.string.play_icon_accessibility)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(Res.string.play_trailer),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    ExpandableTextComponent(
                        text = movie.synopsis,
                        showLess = stringResource(Res.string.show_less),
                        showMore = stringResource(Res.string.show_more)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.cast),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = cast)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (videoPlayerVisibility.value) {
                exitFullScreen.value = false
                VideoComponent(movieUri = movieVideoUri.value)
            }
        }
    }

    /* BackHandler(enabled = true) {
        videoPlayerVisibility.value = false
        exitFullScreen.value = true
    } */
}

@Composable
fun BackgroundGradient(verticalGradientHeight: Float) {
    val screenHeight = GetScreenHeight()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    endY = screenHeight.div(verticalGradientHeight)
                )
            )
    )
}

@Composable
fun VideoComponent(movieUri: String) {
    EnterFullScreen()
    YoutubeComponent(videoId = movieUri)
}

@Composable
fun ErrorMovieDetailScreen(modifier: Modifier = Modifier, error: String) {
    Text(modifier = modifier, text = error)
}

@Composable
fun EmptyMovieDetailScreen(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(Res.string.no_data))
}
