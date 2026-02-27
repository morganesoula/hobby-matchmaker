package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_common_movies_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_common_movies_title
import com.msoula.hobbymatchmaker.core.design.models.RecentMatchMember
import com.msoula.hobbymatchmaker.core.design.molecules.HubFavoriteMovie
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailModalBottomSheet(
    member: RecentMatchMember,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircleWithCustomPhoto(customAvatarPath = member.avatarUrl)
                    SpacerHeight8()
                    Text(text = member.name ?: member.pseudo)
                    SpacerHeight8()
                    Text(text = stringResource(Res.string.hub_recent_matches_common_movies_title))
                    SpacerHeight4()
                }
            }

            items(member.commonMovies) { movie ->
                HubFavoriteMovie(movie = movie, onMovieCardTapped = {})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailNoMoviesModalBottomSheet(
    member: RecentMatchMember,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        Column {
            CircleWithCustomPhoto(customAvatarPath = member.avatarUrl)
            SpacerHeight8()
            Text(text = member.name ?: member.pseudo)
            SpacerHeight8()
            Text(text = stringResource(Res.string.hub_recent_matches_common_movies_title))
            SpacerHeight8()
            Text(text = stringResource(Res.string.hub_recent_matches_common_movies_no_data_txt))
        }
    }
}
