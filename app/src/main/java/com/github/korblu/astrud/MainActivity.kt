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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.korblu.astrud.data.media.AudioPermissionHelper
import com.github.korblu.astrud.ui.pages.AstrudAppBar
import com.github.korblu.astrud.ui.pages.AstrudHeader
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudSongList
import com.github.korblu.astrud.ui.pages.NowPlaying
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
            val barViewModel= hiltViewModel<AppBarViewModel>()
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudApp(
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    playerViewModel: PlayerViewModel = hiltViewModel<PlayerViewModel>(),
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
                        scrollBehavior = scrollBehavior
                    )
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
                        playerViewModel = playerViewModel,
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




