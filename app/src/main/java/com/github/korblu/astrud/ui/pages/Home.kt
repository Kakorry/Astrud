package com.github.korblu.astrud.ui.pages

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.media3.common.util.UnstableApi
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
import com.github.korblu.astrud.ui.viewmodels.HomeViewModel
import com.github.korblu.astrud.ui.viewmodels.PlayerViewModel
import com.github.korblu.astrud.ui.viewmodels.SongViewModel

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
    playerViewModel: PlayerViewModel = hiltViewModel<PlayerViewModel>(),
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
                        playerViewModel.playSong(
                            songMetadata.uri,
                            songMetadata.title ?: "Unknown",
                            songMetadata.artist ?: "Unknown",
                            songMetadata.albumArtUri
                        )

                        val encodedUri = Uri.encode(songMetadata.uri.toString())
                        val encodedTitle = Uri.encode(songMetadata.title)
                        val encodedArtist = Uri.encode(songMetadata.artist)
                        val encodedAlbumArtUri = Uri.encode(songMetadata.albumArtUri.toString())
                        barViewModel.onHideBars()
                        navController.navigate("NowPlayingScreen/$encodedUri/$encodedTitle/$encodedArtist/$encodedAlbumArtUri")
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

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = songMetadata.title != null,
            enter = fadeIn(tween(300)),
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .fillMaxWidth()
                    .heightIn(min = 30.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = songMetadata.title ?: "Unknown",
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
fun AstrudDial(
    navController: NavController,
    songViewModel: SongViewModel,
    barViewModel: AppBarViewModel,
    playerViewModel: PlayerViewModel
) {
    val homeViewModel = hiltViewModel<HomeViewModel>(
        LocalActivity.current as ComponentActivity
    )
    val songAttributes by homeViewModel.dialList.collectAsState()

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
                    val songAlbumUri = songAttributes[index]?.get("albumArtUri")
                    val songTitle = songAttributes[index]?.get("title")
                    val songUri = songAttributes[index]?.get("uri")
                    val songArtist = songAttributes[index]?.get("artist")

                    SongArtwork(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                RoundedCornerShape(
                                    size = 12.dp
                                )
                            ),
                        playerViewModel = playerViewModel,
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
    playerViewModel: PlayerViewModel,
    barViewModel: AppBarViewModel
) {
    val homeViewModel = hiltViewModel<HomeViewModel>(
        LocalActivity.current as ComponentActivity
    )
    val songAttributes by homeViewModel.suggestionList.collectAsState()

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
        items(10) { song ->
            val songAlbumUri = songAttributes[song]?.get("albumArtUri")
            val songTitle = songAttributes[song]?.get("title")
            val songUri = songAttributes[song]?.get("uri")
            val songArtist = songAttributes[song]?.get("artist")

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
                    playerViewModel = playerViewModel,
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
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudHome(
    navController: NavController,
    innerPadding: PaddingValues,
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    playerViewModel: PlayerViewModel = hiltViewModel<PlayerViewModel>(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    AstrudTheme {
        val homeViewModel = hiltViewModel<HomeViewModel>(
            LocalActivity.current as ViewModelStoreOwner
        )
        val suggestionsUserSongs = UserSongs(songViewModel, LocalContext.current)
        val dialUserSongs = UserSongs(songViewModel, LocalContext.current)
        val lazyState by homeViewModel.homeListState.collectAsState()

        LaunchedEffect(Unit) {
            barViewModel.onShowBars()
            homeViewModel.getRandomDial(dialUserSongs)
            homeViewModel.getRandomSuggestions(suggestionsUserSongs)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            state = lazyState
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

            item(
                key = "speedDial"
            ) {
                AstrudDial(navController, songViewModel, barViewModel, playerViewModel)
            }
            // todo Actually make these different and interesting. 07/04/2025 -K
            item(
                key = "suggestions"
            ) {
                SongSuggestions(navController, "Suggestions:", playerViewModel, barViewModel)
            }

            item(
                key = "recentlyListened"
            ) {
                SongSuggestions(navController, "Recently Listened:", playerViewModel, barViewModel)
            }

            item (
                key = "recentlyAdded"
            ){
                SongSuggestions(navController, "Recently Added:", playerViewModel, barViewModel)
            }
        }
    }
}


