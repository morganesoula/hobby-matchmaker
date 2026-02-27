package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MainTitle
import com.msoula.hobbymatchmaker.core.design.atoms.MediumTitle
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
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
    likedMovies: ImmutableList<MovieCarouselItem>,
    navigateToMovieDetail: (movieId: Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = CustomSize.Sixteen),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                MainTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.hub_favorite_movies_title)
                )
                SpacerHeight4()
                MediumTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.hub_favorite_movies_subtitle)
                )
                SpacerHeight8()
            }
        }

        items(likedMovies) { movie ->
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
        modifier = modifier.padding(horizontal = CustomSize.Sixteen)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.hub_favorite_movies_title),
            style = MaterialTheme.typography.titleLarge
        )
        SpacerHeight8()

        NoDataCard(
            noDataTitle = stringResource(Res.string.hub_favorite_movies_no_data_title),
            noDataText = stringResource(Res.string.hub_favorite_movies_no_data_txt),
            noDataBtnText = stringResource(Res.string.hub_favorite_movies_no_data_btn_txt),
            icon = BootstrapSearchHeart,
            onNoDataButtonClicked = navigateToMoviesScreen
        )
    }
}
