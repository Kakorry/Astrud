package com.github.korblu.astrud.ui.pages

import android.app.Activity
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transitionFactory
import coil3.transition.CrossfadeTransition
import com.github.korblu.astrud.AppConstants
import com.github.korblu.astrud.R
import com.github.korblu.astrud.ui.viewmodels.PlayerViewModel
import ir.mahozad.multiplatform.wavyslider.material3.WavySlider as WavySlider3

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NowPlaying(
    navController: NavController,
    context: Context,
    title: String? = "Unknown",
    artist: String? = "Unknown",
    uri: Uri?,
    artwork: Uri?
) {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = androidx.compose.material3.windowsizeclass.calculateWindowSizeClass(activity)
    val widthSizeClass = windowSizeClass.widthSizeClass // For future use when I decide to fix other layouts -K 07/21/2025

    val isRotated = LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE
    val playerViewModel = hiltViewModel<PlayerViewModel>(
        LocalActivity.current as ComponentActivity
    )

    @Composable
    fun SongPage(
        isLandscape: Boolean = false
    ) {
        if (!isLandscape) {
            Scaffold { innerPadding ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(Uri.decode(artwork.toString()))
                                .placeholder(R.drawable.album_placeholder)
                                .fallback(R.drawable.album_placeholder)
                                .error(R.drawable.album_placeholder)
                                .transitionFactory(CrossfadeTransition.Factory())
                                .build(),
                            contentDescription = "Album Cover",
                            modifier = Modifier
                                .widthIn(max = 350.dp)
                                .heightIn(max = 350.dp)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(size = 18.dp)),
                            contentScale = ContentScale.Crop,
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .widthIn(max = 380.dp)
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .basicMarquee(iterations = Int.MAX_VALUE, repeatDelayMillis = 400),
                                text = title ?: "Unknown",
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .basicMarquee(iterations = Int.MAX_VALUE, repeatDelayMillis = 400),
                                text = artist ?: "Unknown",
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val songPosition by playerViewModel.currentPosition.collectAsState()
                        val songDuration by playerViewModel.fullDuration.collectAsState()

                        val currentProgress = remember(songPosition, songDuration) {
                            if (songDuration > 0L) {
                                songPosition.toFloat() / songDuration.toFloat()
                            } else {
                                0f
                            }
                        }

                        LaunchedEffect(songPosition) {
                            Log.d("SongPage", currentProgress.toString())
                        }

                        var sliderPosition = currentProgress

                        WavySlider3(
                            modifier = Modifier
                                .widthIn(max = 370.dp)
                                .heightIn(max = 30.dp)
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            value = sliderPosition,
                            onValueChange =
                                {
                                    sliderPosition = it
                                    playerViewModel.seekTo((it * songDuration.toFloat()).toLong())
                                },
                            waveLength = 36.dp,
                            waveHeight = 8.dp,
                            waveThickness = 4.dp,
                            trackThickness = 4.dp,
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(top = 35.dp, start = 50.dp)
                                    .size(45.dp),
                                onClick = {}
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "Previous"
                                )
                            }

                            AppConstants.StarButton(
                                playerViewModel = playerViewModel,
                                buttonSize = 140.dp,
                                modifier = Modifier
                                    .padding(top = 40.dp)
                                    .align(Alignment.Center)
                            )

                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(top = 35.dp, end = 50.dp)
                                    .size(45.dp),
                                onClick = {}
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "Skip"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (isRotated) SongPage(isLandscape = true) else SongPage(isLandscape = false)
            }
        }
    }
}
