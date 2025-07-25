package com.github.korblu.astrud.ui.pages

import android.app.Activity
import android.net.Uri
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
import androidx.core.net.toUri
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
import com.github.korblu.astrud.data.media.UserSongs
import com.github.korblu.astrud.ui.theme.AstrudTheme
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
import com.github.korblu.astrud.ui.viewmodels.NowPlayingViewModel
import com.github.korblu.astrud.ui.viewmodels.SongViewModel

/*
    I can't tell if Jetpack Compose is really like this or I'm just unorganized.
    Feels like a mess. Maybe UI Toolkits are just like this?
    It is an intuitive to build mess, though. This is pretty fun. 05/23/2025
*/

data class SongMetadata(
    val uri: Uri?,
    val title: String?,
    val artist: String?,
    val albumArtUri: Uri?
) 

@Composable
fun SongArtwork(
    modifier: Modifier = Modifier,
    navController: NavController,
    scale: ContentScale = ContentScale.Crop,
    songMetadata: SongMetadata,    
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel<NowPlayingViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    rounding: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = {
                    if (songMetadata.uri == null) {
                        return@clickable
                    } else {
                        nowPlayingViewModel.playSong(
                            songMetadata.uri,
                            songMetadata.title!!,
                            songMetadata.artist!!,
                            songMetadata.albumArtUri
                        )

                        val encodedUri = Uri.encode(songMetadata.uri.toString())
                        val encodedTitle = Uri.encode(songMetadata.title)
                        val encodedAlbumArtUri = Uri.encode(songMetadata.albumArtUri.toString())
                        barViewModel.onHideBars()
                        navController.navigate("NowPlayingScreen/$encodedUri/$encodedTitle/$encodedAlbumArtUri")
                    }

                }
            ),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(songMetadata.albumArtUri)
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

        if (songMetadata.title != null) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .fillMaxWidth()
                    .heightIn(min = 30.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = songMetadata.title,
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
fun AstrudDial(
    navController: NavController,
    songViewModel: SongViewModel,
    barViewModel: AppBarViewModel,
    nowPlayingViewModel: NowPlayingViewModel
) {
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
                    val songArtist = songAttributes?.get(index)?.get("artist")

                    SongArtwork(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(
                                    size = 12.dp
                                )
                            ),
                        nowPlayingViewModel = nowPlayingViewModel,
                        songMetadata = SongMetadata(
                            songUri?.toUri(),
                            songTitle,
                            songArtist,
                            songAlbumUri?.toUri()
                        ),
                        navController = navController,
                        rounding = 12.dp,
                        barViewModel = barViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SongSuggestions(
    navController: NavController,
    flavorText: String,
    songViewModel: SongViewModel,
    nowPlayingViewModel: NowPlayingViewModel,
    barViewModel: AppBarViewModel
) {
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
            val songArtist = songAttributes?.get(song)?.get("artist")

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
                        nowPlayingViewModel = nowPlayingViewModel,
                        songMetadata = SongMetadata(
                            songUri?.toUri(),
                            songTitle,
                            songArtist,
                            songAlbumUri?.toUri()
                        ),
                        navController = navController,
                        rounding = 18.dp,
                        barViewModel = barViewModel
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
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel<NowPlayingViewModel>(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    AstrudTheme {
        LaunchedEffect(Unit) {
            barViewModel.onShowBars()
        }
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Speed Dial:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }

            item {
                AstrudDial(navController, songViewModel, barViewModel, nowPlayingViewModel)
            }
            // todo Actually make these different and interesting. 07/04/2025 -K
            item {
                SongSuggestions(navController, "Suggestions:", songViewModel, nowPlayingViewModel, barViewModel)
            }

            item {
                SongSuggestions(navController, "Recently Listened:", songViewModel, nowPlayingViewModel, barViewModel)
            }

            item {
                SongSuggestions(navController, "Recently Added:", songViewModel, nowPlayingViewModel, barViewModel)
            }
        }
    }
}


