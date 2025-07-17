package com.github.korblu.astrud.ui.pages

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transitionFactory
import coil3.transition.CrossfadeTransition
import com.github.korblu.astrud.R
import com.github.korblu.astrud.data.media.ExoPlayerManager
import com.github.korblu.astrud.data.media.UserSongs
import com.github.korblu.astrud.ui.theme.AstrudTheme
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
import com.github.korblu.astrud.ui.viewmodels.SongViewModel

/*
    I can't tell if Jetpack Compose is really like this or I'm just unorganized.
    Feels like a mess. Maybe UI Toolkits are just like this?
    It is an intuitive to build mess, though. This is pretty fun. 05/23/2025
*/

@Composable
fun SongArtwork(
    modifier: Modifier = Modifier,
    scale: ContentScale = ContentScale.Crop,
    songAlbumUri: String? = null,
    songTitle: String? = null,
    songUri: String? = null,
    rounding: Dp = 12.dp
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clickable(
                onClick = {
                    val exoPlayer = ExoPlayerManager(context)
                    exoPlayer.initPlayer()
                    if (songUri != null)
                        exoPlayer.playAudio(songUri)
                }
            ),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(songAlbumUri)
                .placeholder(R.drawable.album_placeholder)
                .fallback(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .transitionFactory(CrossfadeTransition.Factory())
                .build(),
            contentDescription = "Album Cover",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 1.dp)
                .clip(RoundedCornerShape(size = rounding)),
            contentScale = scale,
        )

        if (songTitle != null) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .fillMaxWidth()
                    .heightIn(min = 30.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = songTitle,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }
}

@Composable
fun getRandomSongs(songViewModel: SongViewModel, listSize: Int = 10): List<Map<String?, String?>?>? {
    val activity = LocalActivity.current as Activity
    val userSongs = UserSongs(songViewModel)
    var songAttributes by remember { mutableStateOf<List<Map<String?, String?>?>?>( List (listSize) { mapOf ( null to null ) } ) }

    LaunchedEffect(true) {
        songAttributes = List (listSize) { userSongs.getRandomSong(activity, true) }
    }

    return songAttributes
}

@Composable
fun AstrudDial(songViewModel: SongViewModel) {
    val songAttributes = getRandomSongs(songViewModel, 9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .heightIn(max = 380.dp)
                .aspectRatio(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(9) { index ->
                    val songAlbumUri = songAttributes?.get(index)?.get("albumArtUri")
                    val songTitle = songAttributes?.get(index)?.get("title")
                    val songUri = songAttributes?.get(index)?.get("uri")

                    SongArtwork(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(
                                    size = 12.dp
                                )
                            ),
                        songAlbumUri = songAlbumUri,
                        songTitle = songTitle,
                        rounding = 12.dp,
                        songUri = songUri
                    )
                }
            }
        }
    }
}

@Composable
fun SongSuggestions(flavorText: String, songViewModel: SongViewModel) {
    val songAttributes = getRandomSongs(songViewModel, 10)

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = flavorText,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(top = 25.dp, start = 15.dp)
        )
    }
    LazyRow(
        modifier = Modifier.padding(start = 15.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(songAttributes?.size ?: 0) { song ->
            val songAlbumUri = songAttributes?.get(song)?.get("albumArtUri")
            val songTitle = songAttributes?.get(song)?.get("title")
            val songUri = songAttributes?.get(song)?.get("uri")

            @Composable
            fun SongBox() {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SongArtwork(
                        scale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(
                                    size = 18.dp
                                )
                            ),
                        songAlbumUri = songAlbumUri,
                        songTitle = songTitle,
                        songUri = songUri,
                        rounding = 18.dp
                    )
                }
            }
            SongBox()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudHome(
    navController: NavController,
    innerPadding: PaddingValues,
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    AstrudTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            state = barViewModel.homeBarState.collectAsState().value
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Speed Dial:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 5.dp),
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            item {
                AstrudDial(songViewModel)
            }
            // todo Actually make these different and interesting. 07/04/2025 -K
            item {
                SongSuggestions("Suggestions:", songViewModel)
            }

            item {
                SongSuggestions("Recently Listened:", songViewModel)
            }

            item {
                SongSuggestions("Recently Added:", songViewModel)
            }
        }
    }
}


