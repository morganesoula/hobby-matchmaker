package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.ExpandableTextComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMDetailTopBar
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
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
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit,
    onMovieDetailBackPressed: () -> Unit
) {
    when (viewState) {
        is MovieDetailViewStateModel.Error -> ErrorMovieDetailScreen(error = viewState.error)
        is MovieDetailViewStateModel.Loading -> LoadingCircularProgress()
        is MovieDetailViewStateModel.Empty -> EmptyMovieDetailScreen()
        is MovieDetailViewStateModel.Success ->
            MovieDetailScreen(
                oneTimeEventFlow = oneTimeEventFlow,
                movie = viewState.movie,
                onPlayTrailerClicked = onPlayTrailerClicked,
                onMovieDetailBackPressed = onMovieDetailBackPressed
            )
    }
}

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    movie: MovieDetailUiModel,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit,
    onMovieDetailBackPressed: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        MovieDetailContentScreen(
            movie = movie,
            modifier = modifier.padding(it),
            onPlayTrailerClicked = onPlayTrailerClicked,
            oneTimeEventFlow = oneTimeEventFlow,
            snackBarHostState = snackBarHostState,
            onMovieDetailBackPressed = onMovieDetailBackPressed
        )
    }
}

@Composable
fun MovieDetailContentScreen(
    modifier: Modifier = Modifier,
    movie: MovieDetailUiModel,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit,
    snackBarHostState: SnackbarHostState,
    onMovieDetailBackPressed: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val errorFetchingMovieMessage = stringResource(Res.string.no_trailer_available)
    val noConnectionMessage = stringResource(Res.string.connection_issue)

    val movieVideoUri = remember { mutableStateOf(movie.videoKey) }
    var isLoading by remember { mutableStateOf(false) }
    var videoPlayerVisibility by remember { mutableStateOf(false) }

    val filteredCast = movie.cast.filterNot { it.key == "NO_CAST" }
    val scrollState = rememberScrollState()

    ObserveAsEvents(flow = oneTimeEventFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is MovieDetailUiEventModel.OnMovieDetailUiFetchedError ->
                    snackBarHostState.showSnackbar(event.error)

                is MovieDetailUiEventModel.OnPlayMovieTrailerReady -> {
                    if (isLoading) isLoading = false
                    movieVideoUri.value = event.movieUri
                    videoPlayerVisibility = true
                }

                is MovieDetailUiEventModel.ErrorFetchingTrailer -> {
                    if (isLoading) isLoading = false
                    snackBarHostState.showSnackbar(errorFetchingMovieMessage)
                }

                is MovieDetailUiEventModel.NoConnection ->
                    snackBarHostState.showSnackbar(noConnectionMessage)

                is MovieDetailUiEventModel.LoadingTrailer ->
                    isLoading = true

                else -> Unit
            }
        }
    }

    if (isLoading) LoadingCircularProgress()

    Box(modifier = modifier.fillMaxSize().zIndex(0f)) {
        //Background image
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(movie.posterPath.toPath())
                .build(),
            contentDescription = "poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .graphicsLayer {
                    translationY = scrollState.value * 0.5f
                    scaleX = 1f + (scrollState.value / 2000f)
                    scaleY = 1f + (scrollState.value / 2000f)
                },
            contentScale = ContentScale.Crop
        )
        // Filter to darken background
        BackgroundGradient(0.9f)

        //Content on top of the image
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        startY = 300f,
                        endY = 700f
                    )
                )
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .padding(top = 400.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = movie.status,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .background(
                            color = Color.Green.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = movie.releaseDate,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = " · " + movie.genre.toString()
                            .removeSurrounding("[", "]"),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (videoPlayerVisibility) {
                    YoutubeComponent(modifier, movieVideoUri.value) {
                        videoPlayerVisibility = false
                    }
                } else {
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
                            .height(55.dp)
                            .shadow(8.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(
                                        Res.string.play_icon_accessibility
                                    ),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.play_trailer),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExpandableTextComponent(
                    text = movie.synopsis,
                    showLess = stringResource(Res.string.show_less),
                    showMore = stringResource(Res.string.show_more),
                    shouldBeExpandable = filteredCast.isNotEmpty()
                )

                if (filteredCast.isNotEmpty()) MovieCastSection(filteredCast)
            }
        }

        HMMDetailTopBar {
            onMovieDetailBackPressed()
        }
    }
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
fun MovieCastSection(
    cast: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.cast),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cast.entries.toList()) { (actor, role) ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .width(120.dp)
                        .height(120.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = actor,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            softWrap = true,
                            overflow = TextOverflow.Clip
                        )

                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMovieDetailScreen(modifier: Modifier = Modifier, error: String) {
    Text(modifier = modifier, text = error)
}

@Composable
fun EmptyMovieDetailScreen(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(Res.string.no_data))
}
