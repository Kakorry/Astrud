package com.github.korblu.astrud.ui.pages.components

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transitionFactory
import coil3.transition.CrossfadeTransition
import com.github.korblu.astrud.R
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
import com.github.korblu.astrud.ui.viewmodels.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudHeader(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "As",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "trud",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
    )
}

@Composable
fun NowPlayingExtension(
    barViewModel: AppBarViewModel
) {
    val playerViewModel = hiltViewModel<PlayerViewModel>(
        LocalActivity.current as ComponentActivity
    )

    val mediaItemVisibility by barViewModel.mediaItemVisible.collectAsState()

    val currentSong = playerViewModel.currentMediaItem.collectAsState()
    val currentArtwork = currentSong.value?.mediaMetadata?.artworkUri
    val currentTitle = currentSong.value?.mediaMetadata?.title
    val currentArtist = currentSong.value?.mediaMetadata?.artist

    AnimatedVisibility(
        visible = mediaItemVisibility,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    barViewModel.onHideBars()
                    barViewModel.onClickedNowPlaying()
                }
            )
            .heightIn(max = 76.dp),
        enter = fadeIn(animationSpec = tween(durationMillis = 100)),
        exit = fadeOut(animationSpec = tween(durationMillis = 100))
    ) {
        val songPosition by playerViewModel.currentPosition.collectAsState()
        val songDuration by playerViewModel.fullDuration.collectAsState()

        val currentProgress = remember(songPosition, songDuration) {
            if (songDuration > 0L) {
                songPosition.toFloat() / songDuration.toFloat()
            } else {
                0f
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 6.dp)
            ) {
                LinearProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    progress = { currentProgress },
                    modifier = Modifier
                        .heightIn(max = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                )

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .widthIn(max = 60.dp)
                            .fillMaxSize()
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                                .padding(start = 15.dp)
                                .clip(RoundedCornerShape(25)),
                            contentDescription = "${currentTitle ?: "Unknown"}",
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentArtwork)
                                .placeholder(R.drawable.album_placeholder)
                                .fallback(R.drawable.album_placeholder)
                                .error(R.drawable.album_placeholder)
                                .transitionFactory(CrossfadeTransition.Factory())
                                .build(),
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterStart)
                        ) {
                            val maxWidth = this.maxWidth
                            val extendedText = remember(maxWidth) { mutableStateOf<Dp?>(null) }

                            LaunchedEffect(LocalConfiguration.current.orientation) {
                                when {
                                    maxWidth >= 900.dp -> extendedText.value = 900.dp - 80.dp
                                    maxWidth >= 362.dp -> extendedText.value = 362.dp - 80.dp
                                    maxWidth >= 240.dp -> extendedText.value = 240.dp - 80.dp
                                    else -> extendedText.value = 120.dp
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .widthIn(max = extendedText.value ?: 300.dp)
                                    .fillMaxSize()
                                    .padding(start = 15.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Text(
                                    text = currentTitle.toString(),
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .basicMarquee(repeatDelayMillis = 0, iterations = Int.MAX_VALUE),
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = currentArtist.toString(),
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .basicMarquee(repeatDelayMillis = 0, iterations = Int.MAX_VALUE),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 15.dp)
                        ) {
                            StarButton(
                                playerViewModel = playerViewModel,
                                buttonSize = 50.dp,
                                buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Routes(
    val page: Int,
    val description: String,
    val avd: Int,
    val staticIcon: Int
) {
    Home(0, "Home", R.drawable.avd_home, R.drawable.ic_home_outline),
    SongList(1, "Songs", R.drawable.avd_library_music, R.drawable.ic_library_music_outline),
    AlbumList(2, "Albums", R.drawable.avd_album, R.drawable.ic_album_outline),
    UserStats(3, "Stats", R.drawable.avd_face, R.drawable.ic_face_outline)
}

@Keep
@Composable
fun AstrudNavigationBar(
    barViewModel: AppBarViewModel,
    pagerState: PagerState,
) {
    val triggerEffect = remember { mutableStateOf(false) }
    val shouldScroll = remember { mutableStateOf(false) }
    val targetPage = remember { mutableIntStateOf(0) }
    val currentPage = remember { mutableIntStateOf(pagerState.currentPage) }

    Column {
        NowPlayingExtension(barViewModel)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
        ) {
            currentPage.intValue = pagerState.currentPage

            LaunchedEffect(triggerEffect.value, shouldScroll.value) {
                if (shouldScroll.value) {
                    pagerState.animateScrollToPage(targetPage.intValue)
                } else {
                    shouldScroll.value = false
                    triggerEffect.value = !triggerEffect.value
                }
            }

            Routes.entries.forEachIndexed { index, route ->
                val selected = route.page == currentPage.intValue
                val atEnd = remember { mutableStateOf<Boolean?>(null) }
                val animatedImageVector = AnimatedImageVector.animatedVectorResource(route.avd)
                val painter = rememberAnimatedVectorPainter(animatedImageVector, atEnd.value ?: true)

                NavigationBarItem(
                    modifier = Modifier.animateContentSize(),
                    selected = selected,
                    label = {
                        Text(
                            text = route.description,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    icon = {
                        if (selected) {
                            atEnd.value = true
                            Icon(
                                painter = painter,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = route.description
                            )
                        } else {
                            atEnd.value = false
                            Icon(
                                painter = painterResource(route.staticIcon),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = route.description
                            )
                        }
                    },
                    onClick = {
                        atEnd.value = true
                        targetPage.intValue = route.page
                        shouldScroll.value = true
                        triggerEffect.value = !triggerEffect.value
                    }
                )

            }
        }
    }

}

@Composable
fun NowPlayingListener(
    barViewModel: AppBarViewModel,
    playerViewModel: PlayerViewModel,
    navController: NavController
) {
    val nowPlayingClickState by barViewModel.nowPlayingClickState.collectAsState()
    val currentSong = playerViewModel.currentMediaItem.collectAsState()
    val encodedUri = Uri.encode(currentSong.value?.localConfiguration?.uri.toString())
    val encodedTitle = Uri.encode(currentSong.value?.mediaMetadata?.title.toString())
    val encodedArtist = Uri.encode(currentSong.value?.mediaMetadata?.artist.toString())
    val encodedArtwork = Uri.encode(currentSong.value?.mediaMetadata?.artworkUri.toString())

    LaunchedEffect(!barViewModel.barVisibility.collectAsState().value) {
        if (navController.currentDestination?.route == "Pager") {
            barViewModel.onShowBars()
        }
    }

    LaunchedEffect(nowPlayingClickState) {
        if (currentSong.value != null && nowPlayingClickState && navController.currentDestination?.route != "NowPlayingScreen/{songUri}/{songTitle}/{songArtist}/{albumArtwork}") {
            barViewModel.onNavigateToNowPlaying(
                navController,
                encodedUri,
                encodedTitle,
                encodedArtist,
                encodedArtwork
            )
        }
    }
}