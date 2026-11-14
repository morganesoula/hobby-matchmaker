package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapArrowRight
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.molecules.HubFavoriteMovie
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import com.msoula.hobbymatchmaker.core.design.profile_favorites_empty_button
import com.msoula.hobbymatchmaker.core.design.profile_favorites_empty_text
import com.msoula.hobbymatchmaker.core.design.profile_favorites_empty_title
import com.msoula.hobbymatchmaker.core.design.profile_favorites_title
import com.msoula.hobbymatchmaker.core.design.profile_see_all
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileFavoritesSection(
    favorites: ImmutableList<MovieCarouselItem>,
    favoritesCount: Int,
    onSeeAllClicked: () -> Unit,
    onMovieClicked: (movieId: Long) -> Unit,
    onDiscoverClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CustomSize.TwentyFour),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.profile_favorites_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                SpacerWidth8()
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = .15f)
                ) {
                    Text(
                        text = favoritesCount.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = CustomSize.Eight, vertical = CustomSize.Four)
                    )
                }
            }

            if (favorites.isNotEmpty()) {
                Row(
                    modifier = Modifier.clickable(onClick = onSeeAllClicked),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.profile_see_all),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = BootstrapArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = CustomSize.Four)
                            .size(IconSize.Sixteen)
                    )
                }
            }
        }

        SpacerHeight16()

        if (favorites.isEmpty()) {
            // NoDataCard already has a 16 dp side padding: 8 more lines it up with the section title
            NoDataCard(
                modifier = Modifier.padding(horizontal = CustomSize.Eight),
                noDataTitle = stringResource(Res.string.profile_favorites_empty_title),
                noDataText = stringResource(Res.string.profile_favorites_empty_text),
                noDataBtnText = stringResource(Res.string.profile_favorites_empty_button),
                icon = LucideFilm,
                onNoDataButtonClicked = onDiscoverClicked
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = CustomSize.TwentyFour),
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
            ) {
                items(favorites, key = { it.id }) { movie ->
                    HubFavoriteMovie(
                        modifier = Modifier.width(CustomSize.NinetySix + CustomSize.TwentyFour),
                        movie = movie,
                        onMovieCardTapped = onMovieClicked
                    )
                }
            }
        }
    }
}
