package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.ExpandableTextComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMDetailTopBar
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.theme.successContainerColor
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import kotlinx.coroutines.flow.Flow
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
    val platformContext = LocalPlatformContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val errorFetchingMovieMessage = stringResource(Res.string.no_trailer_available)
    val noConnectionMessage = stringResource(Res.string.connection_issue)

    val posterModel = remember(movie.posterPath) {
        ImageRequest.Builder(platformContext)
            .data(movie.posterPath.toPath())
            .crossfade(true)
            .build()
    }

    val scrim = rememberLegibilityScrim()
    val titleShadow = Shadow(
        color = Color.Black.copy(alpha = 0.35f),
        offset = Offset(0f, 1.5f),
        blurRadius = 3f
    )

    var videoPlayerVisible by rememberSaveable { mutableStateOf(false) }
    var videoId by rememberSaveable { mutableStateOf(movie.videoKey) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val filteredCast = remember(movie.cast) { movie.cast.filterNot { it.key == "NO_CAST" } }
    val scrollState = rememberScrollState()

    LaunchedEffect(movie.id) {
        videoId = movie.videoKey
        videoPlayerVisible = videoId.isNotEmpty()
        isLoading = false
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        }
    ) { padding ->
        ObserveAsEvents(flow = oneTimeEventFlow) { event ->
            when (event) {
                is MovieDetailUiEventModel.OnMovieDetailUiFetchedError ->
                    snackBarHostState.showSnackbar(event.error)

                is MovieDetailUiEventModel.OnPlayMovieTrailerReady -> {
                    if (isLoading) isLoading = false
                    videoId = event.movieUri
                    videoPlayerVisible = true
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

        if (isLoading) LoadingCircularProgress()

        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            // Background image
            AsyncImage(
                model = posterModel,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = scrollState.value * 0.2f },
                contentScale = ContentScale.Crop
            )

            // Content on top of the image and below the main content
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(brush = scrim)
            )

            // Main content
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(
                        top = 200.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = bottomInset + 24.dp
                    )
                    .fillMaxSize()
            ) {
                Text(
                    text = movie.status,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .background(
                            color = successContainerColor(),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                MetaPill(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            shadow = titleShadow
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                MetaPill {
                    Text(
                        text = movie.releaseDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = " · " + movie.genre.toString()
                            .removeSurrounding("[", "]"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (videoPlayerVisible) {
                    YoutubeComponent(videoId = videoId) {
                        videoPlayerVisible = false
                    }
                } else {
                    Button(
                        onClick = {
                            onPlayTrailerClicked(
                                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                                    movieId = movie.id,
                                    isVideoURIknown = videoId.isNotEmpty()
                                )
                            )
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
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

            HMMDetailTopBar {
                onMovieDetailBackPressed()
            }
        }
    }
}

@Composable
fun MovieCastSection(
    cast: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val casting = cast.entries.toList()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.cast),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(casting, key = { it.key }) { (actor, role) ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .semantics { contentDescription = "$actor, $role" },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            contentDescription = stringResource(
                                Res.string.actor_name_content_description
                            ),
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
private fun MetaPill(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun rememberLegibilityScrim(): Brush {
    val base = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.background
    else MaterialTheme.colorScheme.surface

    return remember(base) {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.35f to base.copy(alpha = 0.55f),
                0.6f to base.copy(alpha = 0.82f),
                1f to base.copy(alpha = 0.95f)
            )
        )
    }
}

@Composable
fun ErrorMovieDetailScreen(error: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = error, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EmptyMovieDetailScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.no_data),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
