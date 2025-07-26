package com.github.korblu.astrud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transitionFactory
import coil3.transition.CrossfadeTransition
import com.github.korblu.astrud.data.media.AudioPermissionHelper
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudSongList
import com.github.korblu.astrud.ui.pages.NowPlaying
import com.github.korblu.astrud.ui.theme.AstrudTheme
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
import com.github.korblu.astrud.ui.viewmodels.NowPlayingViewModel
import com.github.korblu.astrud.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.provider.Settings as AppSettings

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var audioPermissionHelper: AudioPermissionHelper
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted : Boolean ->
        audioPermissionHelper.handleResult(isGranted)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val songViewModel = hiltViewModel<SongViewModel>()
            val barViewModel= hiltViewModel<AppBarViewModel>()
            val nowPlayingViewModel = hiltViewModel<NowPlayingViewModel>(
                LocalActivity.current as ComponentActivity
            )

            val navController = rememberNavController()
            val isAudioPermissionGiven by songViewModel.wasPermissionGiven.collectAsState()

            audioPermissionHelper = AudioPermissionHelper(
                context = applicationContext,
                activity = this,
                permissionLauncher = requestPermissionLauncher,
                onGranted = {
                    songViewModel.onPermissionChange(true)
                },
                onDenied = {
                    val intent = Intent(AppSettings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    songViewModel.onPermissionChange(false)
                    Toast.makeText(applicationContext, "Please Grant Permission.", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onDeniedPermanently = {
                    if (isAudioPermissionGiven == false) {
                        val intent = Intent(AppSettings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        songViewModel.onPermissionChange(false)
                        Toast.makeText(
                            applicationContext,
                            "Please Grant Permission.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            )

            audioPermissionHelper.checkAndRequest()

            if (isAudioPermissionGiven == true) {
                 AstrudApp(songViewModel, barViewModel, nowPlayingViewModel, navController)
            }
        }
    }
}

// Unrelated, but did you know Astrud sang Girl from Ipanema?
// You should listen to it. -K 05/25/2025
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudHeader(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    songViewModel: AppBarViewModel
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 5.dp)
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
                        .padding(end = 5.dp)
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) }

@Composable
fun BottomBarIconButton(
    navController: NavController,
    targetRoute: String = "I'm gonna crash your ass!!",
    icon: ImageVector = Icons.Filled.Home,
    description: String = "Funny Little Place"
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedPrimary = if (currentRoute == targetRoute) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    IconButton(
        onClick = {
            if (currentRoute != targetRoute) {
                navController.navigate(targetRoute) {
                    val startDestinationRoute = navController.graph.findStartDestination().route

                    popUpTo(startDestinationRoute ?: "Home") {
                        saveState = false
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        content = {
            Icon(
                icon,
                description,
                tint = selectedPrimary
            )
        }
    )
}

@Composable
fun BottomAppBarExtension(
    navController: NavController,
    barViewModel: AppBarViewModel
) {
    val nowPlayingViewModel = hiltViewModel<NowPlayingViewModel>(
        LocalActivity.current as ComponentActivity
    )

    val currentMediaItem by nowPlayingViewModel.currentMediaItem.collectAsState()
    val currentRoute = navController.currentDestination?.route

    val currentSong = nowPlayingViewModel.currentMediaItem.collectAsState()
    val currentArtwork = currentSong.value?.mediaMetadata?.artworkUri
    val currentTitle = currentSong.value?.mediaMetadata?.title
    val currentArtist = currentSong.value?.mediaMetadata?.artist

    val encodedUri = Uri.encode(currentSong.value?.localConfiguration?.uri.toString())
    val encodedTitle = Uri.encode(currentSong.value?.mediaMetadata?.title.toString())
    val encodedArtist = Uri.encode(currentSong.value?.mediaMetadata?.artist.toString())
    val encodedArtwork = Uri.encode(currentSong.value?.mediaMetadata?.artworkUri.toString())

    AnimatedVisibility(
        visible = currentMediaItem != null && currentRoute != "NowPlayingScreen/{songUri}/{songTitle}/{songArtist}/{albumArtwork}",
        modifier = Modifier
            .clickable(
                onClick = {
                    barViewModel.onHideBars()
                    navController.navigate("NowPlayingScreen/$encodedUri/$encodedTitle/$encodedArtist/$encodedArtwork")
                }
            )
            .heightIn(max = 70.dp),
        enter = fadeIn(animationSpec = tween(durationMillis = 400)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400))
    ) {

        val songPosition by nowPlayingViewModel.currentPosition.collectAsState()
        val songDuration by nowPlayingViewModel.fullDuration.collectAsState()

        val currentProgress = remember(songPosition, songDuration) {
            if (songDuration > 0L) {
                songPosition.toFloat() / songDuration.toFloat()
            } else {
                0f
            }
        }

        Column {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .heightIn(max = 5.dp)
                    .align(Alignment.Start)
                    .fillMaxSize(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
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
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxSize()
                            .padding(start = 15.dp)
                            .clip(RoundedCornerShape(10.dp)),
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
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        val extendedText = remember(maxWidth) { mutableStateOf(300.dp) }

                        if (this.maxWidth > AppConstants.SCREEN_WIDTH_DP_EXTRA_SMALL) {
                            extendedText.value = 350.dp
                        } else {
                            extendedText.value = 200.dp
                        }

                        Column(
                            modifier = Modifier
                                .widthIn(max = extendedText.value)
                                .fillMaxSize()
                                .padding(start = 15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = currentTitle.toString(),
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .basicMarquee(repeatDelayMillis = 200),
                                style = MaterialTheme.typography.labelLarge,
                            )

                            Text(
                                text = currentArtist.toString(),
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .basicMarquee(repeatDelayMillis = 200),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 15.dp)
                    ) {
                        AppConstants.StarButton(
                            nowPlayingViewModel = nowPlayingViewModel,
                            buttonSize = 50.dp
                        )
                    }
                }
            }
        }
    }
}

@Keep
@Composable
fun AstrudAppBar(navController: NavController, barViewModel: AppBarViewModel) {
    Column {
        BottomAppBarExtension(navController, barViewModel)

        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            actions = {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(55.dp)
                        ) {
                            BottomBarIconButton(
                                navController,
                                "Home",
                                Icons.Filled.Home,
                                "Home"
                            )
                            BottomBarIconButton(
                                navController,
                                "List",
                                Icons.Filled.LibraryMusic,
                                "Songs"
                            )
                            BottomBarIconButton(
                                navController,
                                icon = Icons.Filled.Album,
                                description = "Albums"
                            )
                            BottomBarIconButton(
                                navController,
                                icon = Icons.Filled.Person,
                                description = "Artists"
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudApp(
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel<NowPlayingViewModel>(),
    navController: NavHostController
) {
    AstrudTheme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val barVisibility by barViewModel.barVisibility.collectAsState()
        val homeAppBarState = rememberTopAppBarState()
        val listAppBarState = rememberTopAppBarState()


        val homeBarState = TopAppBarDefaults.pinnedScrollBehavior(homeAppBarState)
        val listBarState = TopAppBarDefaults.pinnedScrollBehavior(listAppBarState)

        val playedAnimationStatus by barViewModel.playedBarAnimation.collectAsState()

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = barVisibility,
                    enter = if (!playedAnimationStatus) {
                        slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseOutExpo
                            )
                        )

                    } else {
                        EnterTransition.None
                    },
                    exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseOutExpo
                            )
                        ),
                ) {
                    AstrudAppBar(navController, barViewModel)
                }
            },
            topBar = {
                AnimatedVisibility(
                    visible = barVisibility,
                    enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseOutExpo
                            )
                        ) + fadeIn(animationSpec = tween(durationMillis = 500, easing = EaseOutExpo)),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = EaseOutExpo
                        )
                    ) + fadeOut(animationSpec = tween(durationMillis = 500, easing = EaseOutExpo))
                ) {
                    val scrollBehavior = when (currentRoute) {
                        "Home" -> TopAppBarDefaults.pinnedScrollBehavior(homeAppBarState)
                        "List" -> TopAppBarDefaults.pinnedScrollBehavior(listAppBarState)
                        else -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                    }
                    AstrudHeader(
                        navController = navController,
                        scrollBehavior = scrollBehavior,
                        barViewModel)
                }
            }
        ) { innerPadding ->
            val context = LocalContext.current

            NavHost(
                navController = navController,
                startDestination = "Home",
            ) {
                composable(
                    route = "Home",
                    enterTransition = {
                        fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(200))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = { fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(200)) },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(200))
                    }
                ) {

                    AstrudHome(
                        navController,
                        innerPadding,
                        scrollBehavior = homeBarState,
                        songViewModel = songViewModel,
                        nowPlayingViewModel = nowPlayingViewModel,
                        barViewModel = barViewModel
                    )

                    LaunchedEffect(true) {
                        barViewModel.onSetPlayedStatus()
                        barViewModel.onShowBars()
                    }
                }
                composable(
                    route = "List",
                    enterTransition = {
                        fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(200))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = { fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(200)) }
                ) {
                    LaunchedEffect(Unit) {
                        barViewModel.onShowBars()
                    }

                    AstrudSongList(
                        innerPadding,
                        listBarState,
                        barViewModel)
                }

                composable(
                    "NowPlayingScreen/{songUri}/{songTitle}/{songArtist}/{albumArtwork}",
                    listOf(
                        navArgument("songUri") { type = NavType.StringType },
                        navArgument("songTitle") { type = NavType.StringType },
                        navArgument("songArtist") { type = NavType.StringType },
                        navArgument("albumArtwork") {
                            type = NavType.StringType
                            nullable = true
                        }
                    ),
                    enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)) },
                    exitTransition = { fadeOut(animationSpec = tween(500)) },
                ) { backStackEntry ->
                    val songUri = Uri.decode((backStackEntry.arguments?.getString("songUri"))).toUri()
                    val songArtist = Uri.decode((backStackEntry.arguments?.getString("songArtist")))
                    val songTitle = Uri.decode(backStackEntry.arguments?.getString("songTitle"))
                    val albumArtwork = Uri.decode(backStackEntry.arguments?.getString("albumArtwork")).toUri()

                    NowPlaying(
                        navController,
                        context,
                        songTitle,
                        songArtist,
                        songUri,
                        albumArtwork
                    )
                }
            }
        }
    }
}




