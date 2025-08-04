package com.github.korblu.astrud

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.korblu.astrud.data.media.AudioPermissionHelper
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudSongList
import com.github.korblu.astrud.ui.pages.NowPlaying
import com.github.korblu.astrud.ui.pages.components.AstrudHeader
import com.github.korblu.astrud.ui.pages.components.AstrudNavigationBar
import com.github.korblu.astrud.ui.theme.AstrudTheme
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
import com.github.korblu.astrud.ui.viewmodels.PlayerViewModel
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
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        window.isNavigationBarContrastEnforced = false

        setContent {
            val songViewModel = hiltViewModel<SongViewModel>()
            val barViewModel= hiltViewModel<AppBarViewModel>(
                LocalActivity.current as ComponentActivity
            )
            val playerViewModel = hiltViewModel<PlayerViewModel>(
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
                 AstrudApp(songViewModel, barViewModel, playerViewModel, navController)
            }
        }
    }
}

// Unrelated, but did you know Astrud sang Girl from Ipanema?
// You should listen to it. -K 05/25/2025
// TODO: It is probably a good idea to use a BottomSheetScaffold to make the NowPlayingExtension draggable
//  but I will keep it like this for now. Not like it's essential or anything.
// Another TODO: Fix the damn varied sized layouts and stop being lazy. -K 08/03/2025
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudApp(
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    playerViewModel: PlayerViewModel = hiltViewModel<PlayerViewModel>(),
    navController: NavHostController
) {
    AstrudTheme {
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

        val barVisibility by barViewModel.barVisibility.collectAsState()
        val playedAnimationStatus by barViewModel.playedBarAnimation.collectAsState()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
           bottomBar = {
                AnimatedVisibility(
                    visible = barVisibility,
                    enter = if (!playedAnimationStatus) {
                        slideInVertically(
                            initialOffsetY = { it },
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
                    Column {
                        AstrudNavigationBar(barViewModel, pagerState)
                    }
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
                    AstrudHeader(
                        navController = navController,
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        ) { innerPadding ->
            val context = LocalContext.current

            NavHost(
                navController = navController,
                startDestination = "Pager",
            ) {
                composable(
                    route = "Pager",
                    enterTransition = { fadeIn(animationSpec = tween(durationMillis = 500)) },
                    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 500)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 500)) },
                    popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 500)) }
                ) {
                    HorizontalPager(pagerState) { page ->
                        when (page) {
                            0 -> AstrudHome(
                                navController,
                                innerPadding,
                                barViewModel,
                                songViewModel,
                                playerViewModel
                            )
                            1 -> AstrudSongList(
                                innerPadding,
                                navController,
                                barViewModel,
                                playerViewModel
                            )
                        }
                    }

                    val nowPlayingClickState by barViewModel.nowPlayingClickState.collectAsState()

                    val currentSong = playerViewModel.currentMediaItem.collectAsState()
                    val encodedUri = Uri.encode(currentSong.value?.localConfiguration?.uri.toString())
                    val encodedTitle = Uri.encode(currentSong.value?.mediaMetadata?.title.toString())
                    val encodedArtist = Uri.encode(currentSong.value?.mediaMetadata?.artist.toString())
                    val encodedArtwork = Uri.encode(currentSong.value?.mediaMetadata?.artworkUri.toString())

                    if (currentSong.value != null) {
                        barViewModel.onSetMediaItemVisibility()
                    }

                    LaunchedEffect(nowPlayingClickState) {
                        if (currentSong.value != null && navController.currentDestination?.route != "NowPlayingScreen/{songUri}/{songTitle}/{songArtist}/{albumArtwork}" && nowPlayingClickState) {
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
                    enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(600)) },
                    exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(800)) + fadeOut(animationSpec = tween(600)) },
                    popEnterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(600)) },
                    popExitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(800)) + fadeOut(animationSpec = tween(600)) },
                ) { backStackEntry ->
                    val navBackStackEntry = backStackEntry
                    val backStackState = navBackStackEntry.lifecycle.currentStateFlow.collectAsState()

                    LaunchedEffect(backStackState.value.name) {
                        if (backStackState.value == Lifecycle.State.CREATED) {
                            barViewModel.onShowBars()
                        }
                    }

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




