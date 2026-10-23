package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_btn_txt
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_title
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_subtitle
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_title
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSearchHeart
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.molecules.HubFavoriteMovie
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun HubFavoriteMoviesSection(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    HubSection(
        modifier = modifier,
        title = stringResource(Res.string.hub_favorite_movies_title),
        subtitle = stringResource(Res.string.hub_favorite_movies_subtitle),
        content = content
    )
}

@Composable
fun HubFavoriteMovies(
    modifier: Modifier = Modifier,
    likedMovies: ImmutableList<MovieCarouselItem>,
    navigateToMovieDetail: (movieId: Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = CustomSize.Sixteen,
            end = CustomSize.Sixteen,
            bottom = CustomSize.Sixteen
        ),
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        items(likedMovies, key = { it.id }) { movie ->
            HubFavoriteMovie(movie = movie, onMovieCardTapped = navigateToMovieDetail)
        }
    }
}

@Composable
fun HubNoFavoriteMoviesSection(
    modifier: Modifier = Modifier,
    navigateToMoviesScreen: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NoDataCard(
            modifier = Modifier.fillMaxWidth(),
            noDataTitle = stringResource(Res.string.hub_favorite_movies_no_data_title),
            noDataText = stringResource(Res.string.hub_favorite_movies_no_data_txt),
            noDataBtnText = stringResource(Res.string.hub_favorite_movies_no_data_btn_txt),
            icon = BootstrapSearchHeart,
            onNoDataButtonClicked = navigateToMoviesScreen
        )
    }
}
