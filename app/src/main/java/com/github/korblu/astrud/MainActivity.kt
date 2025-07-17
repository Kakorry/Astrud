package com.github.korblu.astrud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.korblu.astrud.data.media.AudioPermissionHelper
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudSongList
import com.github.korblu.astrud.ui.theme.AstrudTheme
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel
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

            val navController = rememberNavController()
            val isLoading by songViewModel.isLoading.collectAsState()
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
                 AstrudApp(songViewModel, barViewModel, navController)
            }
        }
    }
}

// Unrelated, but did you know Astrud sang Girl from Ipanema?
// You should listen to it. -K 05/25/2025
@OptIn(ExperimentalMaterial3Api::class)
@Composable // Added Pair to return two values
fun AstrudHeader(navController: NavHostController, scrollBehavior: TopAppBarScrollBehavior, songViewModel: AppBarViewModel) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(start = 5.dp)
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
                        .padding(top = 2.dp, end = 5.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
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
            if (currentRoute != targetRoute && currentRoute != "Welcome") {
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

// I can't believe I had to make a separate function for this. -K 07/12/2025
fun getCorrectTransition(currentRoute: String?, previousRoute: String?): EnterTransition? {
    return if (currentRoute == "Home" && previousRoute == "Welcome" || previousRoute == null) {
        fadeIn(animationSpec = tween(600))
    } else {
        fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
    }
}

@Keep
@Composable
fun AstrudAppBar(navController: NavController) {
    BottomAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            actions = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(55.dp)
                    ) {
                        BottomBarIconButton(navController, "Home", Icons.Filled.Home, "Home")
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
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudApp(
    songViewModel: SongViewModel = hiltViewModel<SongViewModel>(),
    barViewModel: AppBarViewModel = hiltViewModel<AppBarViewModel>(),
    navController: NavHostController
) {
    AstrudTheme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val homeAppBarState = rememberTopAppBarState()
        val listAppBarState = rememberTopAppBarState()

        val astrudHeaderState = when (currentRoute) {
            "Home" -> TopAppBarDefaults.pinnedScrollBehavior(homeAppBarState)
            "List" -> TopAppBarDefaults.pinnedScrollBehavior(listAppBarState)
            else -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        }
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        val playedAnimationStatus by barViewModel.playedBarAnimation.collectAsState()

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = true,
                    enter = if (!playedAnimationStatus) {
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = LinearOutSlowInEasing
                            )
                        )
                    } else {
                        EnterTransition.None
                    },
                    exit = if (!playedAnimationStatus) {
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = LinearOutSlowInEasing
                            )
                        )
                    } else {
                        ExitTransition.None
                    },
                ) {
                    AstrudAppBar(navController)
                }
            },
            topBar = {
                AnimatedVisibility(
                    visible = true,
                    enter = if (!playedAnimationStatus) {
                        slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = LinearOutSlowInEasing
                            )
                        )
                    } else {
                        EnterTransition.None
                    },
                    exit = if (!playedAnimationStatus) {
                        slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = LinearOutSlowInEasing
                            )
                        )
                    } else {
                        ExitTransition.None
                    },
                ) {
                    AstrudHeader(navController = navController, scrollBehavior = astrudHeaderState, barViewModel)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "Home",
            ) {
                composable(
                    "Home",
                    enterTransition = {
                        getCorrectTransition(currentRoute, previousRoute)
                    },
                    exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) }
                ) {
                    AstrudHome(
                        navController,
                        innerPadding,
                        scrollBehavior = astrudHeaderState,
                        songViewModel = songViewModel,
                        barViewModel = barViewModel
                    )

                    LaunchedEffect(true) {
                        barViewModel.onSetPlayedStatus()
                    }
                }
                composable(
                    "List",
                    enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
                ) { AstrudSongList(innerPadding, astrudHeaderState, barViewModel) }
            }
        }
    }
}




