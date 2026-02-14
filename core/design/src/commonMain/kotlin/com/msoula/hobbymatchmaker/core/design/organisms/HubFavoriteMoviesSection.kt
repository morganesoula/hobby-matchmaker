package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MediumTitle
import com.msoula.hobbymatchmaker.core.design.atoms.SmallBodyText
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_btn_txt
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_title
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_subtitle
import com.msoula.hobbymatchmaker.core.design.hub_favorite_movies_title
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.molecules.HubFavoriteMovie
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun HubFavoriteMoviesSection(
    likedMovies: ImmutableList<MovieCarouselItem>,
    navigateToMoviesScreen: () -> Unit,
    navigateToMovieDetail: (movieId: Long) -> Unit
) {
    Column {
        MediumTitle(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.hub_favorite_movies_title)
        )
        SpacerHeight4()
        SmallBodyText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.hub_favorite_movies_subtitle)
        )
        SpacerHeight8()

        if (likedMovies.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(likedMovies) { movie ->
                    HubFavoriteMovie(movie = movie, onMovieCardTapped = navigateToMovieDetail)
                }
            }
        } else {
            Card {
                NoDataCard(
                    noDataTitle = stringResource(Res.string.hub_favorite_movies_no_data_title),
                    noDataText = stringResource(Res.string.hub_favorite_movies_no_data_txt),
                    noDataBtnText = stringResource(Res.string.hub_favorite_movies_no_data_btn_txt),
                    onNoDataButtonClicked = navigateToMoviesScreen
                )
            }
        }
    }
}
